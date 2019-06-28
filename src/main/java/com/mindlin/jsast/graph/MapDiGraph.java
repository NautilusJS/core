package com.mindlin.jsast.graph;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.mindlin.nautilus.util.Iterators;
import com.mindlin.nautilus.util.Iterators.ChainIterator;
import com.mindlin.nautilus.util.LazyCollection.LazySet;

public class MapDiGraph<V, E> implements DiGraph<V, E> {
	protected final Map<V, Node> nodes;
	
	public MapDiGraph() {
		this(new LinkedHashMap<>());
	}
	
	public MapDiGraph(Map<V, MapDiGraph<V, E>.Node> nodes) {
		this.nodes = Objects.requireNonNull(nodes);
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param graph
	 */
	public MapDiGraph(DiGraph<V, E> graph) {
		this(new LinkedHashMap<>());
		// Copy nodes
		for (DiGraph.Node<V, E> node : graph.getNodes())
			this.addNode(node.getValue());
		for (DiGraph.Edge<V, E> edge : graph.getEdges())
			this.addEdge(edge.getStart().getValue(), edge.getValue(), edge.getEnd().getValue());
	}

	@Override
	public boolean hasNode(V value) {
		return this.nodes.containsKey(value);
	}

	@Override
	public Node addNode(V value) throws IllegalArgumentException {
		Node result = new Node(value);
		if (this.nodes.putIfAbsent(value, result) != null)
			throw new IllegalArgumentException();
		return result;
	}

	@Override
	public Node getNode(V value) throws IllegalArgumentException {
		Node result = this.nodes.get(value);
		if (result == null)
			throw new IllegalArgumentException();
		return result;
	}

	@Override
	public Node getOrAddNode(V value) throws IllegalArgumentException {
		return this.nodes.computeIfAbsent(value, Node::new);
	}

	@Override
	public Node getNodeIfPresent(V value) {
		return this.nodes.getOrDefault(value, null);
	}

	@Override
	public Collection<? extends Node> getNodes() {
		return new NodesView();
	}

	@Override
	public Collection<? extends Edge> getEdges() {
		//TODO: proxy object here?
		Set<Edge> result = new HashSet<>();
		for (Node node : this.nodes.values())
			result.addAll(node.getEdges());
		return result;
	}
	
	@Override
	public Collection<? extends Edge> getEdges(V src, V dst) {
		Node n1 = getNode(src);
		Node n2 = (src == dst) ? n1 : getNode(dst);
		
		return new FilteredEdges(n1, n2);
	}

	@Override
	public Edge addEdge(V node1, V node2) throws IllegalArgumentException {
		return addEdge(node1, null, node2);
	}

	@Override
	public boolean hasEdge(V src, E value, V dst) {
		for (Edge e : this.getEdges(src, dst))
			if (Objects.equals(e.getValue(), value))
				return true;
		return false;
	}

	protected Edge createEdge(E value, Node n1, Node n2) {
		return new Edge(n1, value, n2);
	}

	@Override
	public Edge addEdge(V src, E value, V dst) throws IllegalArgumentException {
		Node n1 = getNode(src);
		Node n2 = (src == dst) ? n1 : getNode(dst);
		
		Edge edge = createEdge(value, n1, n2);
		
		// Add edge to nodes
		if (!n1.outEdges.add(edge))
			throw new IllegalArgumentException("Duplicate");
		
		if (!n2.inEdges.add(edge)) {
			IllegalArgumentException e = new IllegalArgumentException("Duplicate");
			if (!n1.outEdges.remove(edge))
				e.addSuppressed(new ConcurrentModificationException().fillInStackTrace());
			throw e;
		}
		
		return edge;
	}
	
	protected boolean removeNode(Node node) {
		Objects.requireNonNull(node);
		if (!nodes.remove(node.getValue(), node))
			return false;
		
		node.getInEdges().clear();
		node.getOutEdges().clear();
		return true;
	}
	
	protected boolean removeEdge(Edge e) {
		Node n1 = e.getStart();
		Node n2 = e.getEnd();
		boolean result = n1.outEdges.remove(e);
		result |= n2.inEdges.remove(e);
		return result;
	}
	
	protected class Node implements DiGraph.Node<V, E> {
		private V value;
		private Set<Edge> inEdges = new LinkedHashSet<>();
		private Set<Edge> outEdges = new LinkedHashSet<>();
		
		public Node(V value) {
			this.value = value;
		}
		
		private MapDiGraph<V, E> getGraph() {
			return MapDiGraph.this;
		}
		
		@Override
		public V getValue() {
			return this.value;
		}

		@Override
		public Collection<? extends Edge> getEdges() {
			Set<Edge> result = new LinkedHashSet<>();
			result.addAll(this.getInEdges());
			result.addAll(this.getOutEdges());
			return result;
		}

		@Override
		public Edge connect(E value, Graph.Node<V, E> destination) {
			if (!(destination instanceof MapDiGraph.Node))
				throw new IllegalArgumentException("Inter-graph connection");
			return this.connect(value, (Node) destination);
		}

		public Edge connect(E value, Node destination) {
			if (destination.getGraph() != this.getGraph())
				throw new IllegalArgumentException("Inter-graph connection");
			Edge edge = new Edge(this, value, destination);
			
			this.outEdges.add(edge);
			destination.inEdges.add(edge);
			
			return edge;
		}

		@Override
		public Collection<? extends Edge> getOutEdges() {
			return this.getOutEdges();
		}

		@Override
		public Collection<? extends Edge> getInEdges() {
			return new InEdgesView();
		}
		
		protected abstract class AbstractNodeEdgesView extends AbstractCollection<Edge> {
			@SuppressWarnings("unchecked")
			protected boolean canContain(Object o) {
				return (o instanceof MapDiGraph.Edge) && canContain((Edge) o);
			}
			
			protected abstract boolean canContain(Edge e);
			
			protected abstract Set<Edge> getDelegate();
			
			@Override
			public boolean add(Edge e) {
				if (!canContain((Object) e))
					throw new IllegalArgumentException();
				return getDelegate().add(e);
			}

			@Override
			@SuppressWarnings("unchecked")
			public boolean contains(Object o) {
				return this.canContain(o) && this.contains((Edge) o);
			}
			
			public boolean contains(Edge e) {
				return getDelegate().contains(e);
			}
			
			@Override
			public int size() {
				return getDelegate().size();
			}

			@Override
			public Iterator<Edge> iterator() {
				// TODO custom iterator
				return getDelegate().iterator();
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public boolean remove(Object o) {
				return this.canContain(o) && this.contains((Edge) o);
			}
			
			public boolean remove(Edge e) {
				return MapDiGraph.this.removeEdge(e);
			}
			
			@Override
			public void clear() {
				// Make a copy because delegate will be modified
				Set<Edge> edges = new HashSet<>(getDelegate());
				for (Edge edge : edges)
					MapDiGraph.this.removeEdge(edge);
			}
		}
		
		protected class InEdgesView extends AbstractNodeEdgesView {
			@Override
			protected Set<MapDiGraph<V, E>.Edge> getDelegate() {
				return Node.this.inEdges;
			}
			
			@Override
			protected boolean canContain(Edge e) {
				return Objects.equals(e.getEnd(), Node.this);
			}
		}
		
		protected class OutEdgesView extends AbstractNodeEdgesView {
			@Override
			protected Set<MapDiGraph<V, E>.Edge> getDelegate() {
				return Node.this.outEdges;
			}
			
			@Override
			protected boolean canContain(Edge e) {
				return Objects.equals(e.getStart(), Node.this);
			}
		}
	}
	
	protected class Edge implements DiGraph.Edge<V, E> {
		protected Node start;
		protected E value;
		protected Node end;
		
		public Edge(MapDiGraph<V, E>.Node start, E value, MapDiGraph<V, E>.Node end) {
			this.start = start;
			this.value = value;
			this.end = end;
		}

		@Override
		public E getValue() {
			return this.value;
		}

		@Override
		public E setValue(E value) throws UnsupportedOperationException {
			E result = this.value;
			this.value = value;
			return result;
		}

		@Override
		public Node getStart() {
			return this.start;
		}

		@Override
		public Node getEnd() {
			return this.end;
		}
	}
	
	protected class NodesView extends AbstractCollection<Node> {
		protected MapDiGraph<V, E> graph() {
			return MapDiGraph.this;
		}
		
		protected Map<V, Node> delegate() {
			return graph().nodes;
		}
		
		@Override
		public Iterator<Node> iterator() {
			return delegate().values().iterator();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(Object o) {
			return (o instanceof MapDiGraph.Node)
					&& this.contains((Node) o);
		}
		
		public boolean contains(Node node) {
			Node actual = delegate().get(node.getValue());
			return Objects.equals(actual, node);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			return (o instanceof MapDiGraph.Node)
					&& this.remove((Node) o);
		}
		
		public boolean remove(Node n) {
			Objects.requireNonNull(n);
			return graph().removeNode(n);
		}
		
		@Override
		public void clear() {
			delegate().clear();
		}
		
		@Override
		public boolean isEmpty() {
			return delegate().isEmpty();
		}

		@Override
		public int size() {
			return delegate().size();
		}
	}
	
	protected Iterator<? extends Edge> edgeFilterIterator(Node src, Node dst) {
		Collection<? extends Edge> out = src.getOutEdges();
		Collection<? extends Edge> in = dst.getInEdges();
		if (in.size() < out.size())
			return Iterators.filter(in.iterator(), out::contains);
		else
			return Iterators.filter(out.iterator(), in::contains);
	}
	
	protected class FilteredEdges extends LazySet<Edge> {
		protected final Node src;
		protected final Node dst;
		
		public FilteredEdges(Node src, Node dst) {
			super(new LinkedHashSet<>(), edgeFilterIterator(src, dst));
			this.src = src;
			this.dst = dst;
		}
		
		@Override
		public boolean add(Edge e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(Collection<? extends MapDiGraph<V, E>.Edge> c) {
			throw new UnsupportedOperationException();
		}
		
		@SuppressWarnings("unchecked")
		protected Edge asElement(Object o) {
			if (!(o instanceof MapDiGraph.Edge))
				return null;
			return (Edge) o;
		}
		
		protected boolean matchEdge(Edge e) {
			Objects.requireNonNull(e);
			return Objects.equals(src, e.getStart()) && Objects.equals(dst, e.getEnd());
		}
		
		@Override
		public boolean contains(Object o) {
			Edge e = asElement(o);
			return (e != null) && this.contains(e);
		}
		
		public boolean contains(Edge e) {
			if (!matchEdge(e))
				return false;
			return super.contains(e);
		}
		
		@Override
		public boolean remove(Object o) {
			Edge e = asElement(o);
			return (e != null) && this.remove(e);
		}
		
		public boolean remove(Edge e) {
			if (!matchEdge(e))
				return false;
			if (!super.remove(e))
				return false;
			MapDiGraph.this.removeEdge(e);
			return true;
		}
	}
	
	protected Iterator<? extends Edge> edgesIterator() {
		return new ChainIterator<>(
				Iterators.map(
						this.nodes.values().iterator(),
						node -> node.getEdges().iterator()));
	}
	
	protected class EdgesView extends LazySet<Edge> {
		public EdgesView() {
			super(edgesIterator());
		}
		//TODO
	}
}

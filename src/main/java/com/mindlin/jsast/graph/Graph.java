package com.mindlin.jsast.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

public interface Graph<V, E> {
	// Node stuff
	/**
	 * @return Collection of nodes.
	 */
	Collection<? extends Node<V, E>> getNodes();
	boolean hasNode(V vertex);
	Node<V, E> addNode(V value) throws IllegalArgumentException;
	Node<V, E> getNode(V value) throws IllegalArgumentException;
	Node<V, E> getOrAddNode(V value) throws IllegalArgumentException;
	@Nullable Node<V, E> getNodeIfPresent(V value);
	
	
	// Edge stuff
	/**
	 * @return Collection of edges
	 */
	Collection<? extends Edge<V, E>> getEdges();
	Collection<? extends Edge<V, E>> getEdges(V src, V dst);
	boolean hasEdge(V src, V dst);
	boolean hasEdge(V src, E value, V dst);
	
	Edge<V, E> addEdge(V node1, V node2) throws IllegalArgumentException;
	Edge<V, E> addEdge(V node1, E value, V node2) throws IllegalArgumentException;
	
	// Helpers
	@Deprecated
	default boolean isConnected(V n1, E e, V n2) {
		Node<V, E> node1 = this.getNodeIfPresent(n1);
		if (node1 == null)
			return false;
		Node<V, E> node2 = this.getNodeIfPresent(n2);
		if (node2 == null)
			return false;
		for (Edge<V, E> edge : node1.getEdges()) {
			if (!Objects.equals(edge.getValue(), e))
				continue;
			if ((Objects.equals(edge.getStart(), node2) && Objects.equals(edge.getEnd(), node1))
					|| (Objects.equals(edge.getEnd(), node2) && Objects.equals(edge.getStart(), node1)))
				return true;
		}
		return false;
	}
	
	public static interface Node<V, E> {
		V getValue();
		
		Collection<? extends Edge<V, E>> getEdges();
		
		default int getDegree() {
			return getEdges().size();
		}
		
		Edge<V, E> connect(E value, Node<V, E> destination);
	}
	
	public static interface Edge<V, E> {
		Node<V, E> getStart();
		
		Node<V, E> getEnd();
		
		default Collection<? extends Node<V, E>> getNodes() {
			Set<Node<V, E>> result = new LinkedHashSet<>();
			result.add(getStart());
			result.add(getEnd());
			return Collections.unmodifiableSet(result);
		}
		
		E getValue();
		
		E setValue(E value) throws UnsupportedOperationException;
	}
}

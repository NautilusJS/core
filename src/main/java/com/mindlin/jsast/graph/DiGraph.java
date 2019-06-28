package com.mindlin.jsast.graph;

import java.util.Collection;
import java.util.Objects;

public interface DiGraph<V, E> extends Graph<V, E> {
	@Override
	Collection<? extends Node<V, E>> getNodes();
	
	@Override
	Node<V, E> addNode(V value) throws IllegalArgumentException;
	
	@Override
	Node<V, E> getNode(V value) throws IllegalArgumentException;

	@Override
	Node<V, E> getOrAddNode(V value) throws IllegalArgumentException;

	@Override
	Node<V, E> getNodeIfPresent(V value);
	
	@Override
	Collection<? extends Edge<V, E>> getEdges();
	
	@Override
	Collection<? extends Edge<V, E>> getEdges(V src, V dst);
	
	@Override
	default boolean hasEdge(V src, V dst) {
		return !getEdges(src, dst).isEmpty();
	}
	
	@Override
	default boolean hasEdge(V src, E value, V dst) {
		for (Edge<V, E> e : getEdges(src, dst))
			if (Objects.equals(e.getValue(), value))
				return true;
		return false;
	}
	
	@Override
	Edge<V, E> addEdge(V src, V dst) throws IllegalArgumentException;
	
	@Override
	Edge<V, E> addEdge(V src, E value, V dst) throws IllegalArgumentException;

	public static interface Node<V, E> extends Graph.Node<V, E> {
		Collection<? extends Edge<V, E>> getInEdges();
		
		default int inDegree() {
			return getInEdges().size();
		}
		
		Collection<? extends Edge<V, E>> getOutEdges();
		
		default int outDegree() {
			return getOutEdges().size();
		}
	}
	
	public static interface Edge<V, E> extends Graph.Edge<V, E> {
		@Override
		Node<V, E> getStart();
		
		@Override
		Node<V, E> getEnd();
		
		@Override
		@SuppressWarnings("unchecked")
		default Collection<? extends Node<V, E>> getNodes() {
			return (Collection<? extends Node<V, E>>) Graph.Edge.super.getNodes();
		}
	}
}

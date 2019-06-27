package com.mindlin.jsast.tree;

import java.util.List;

/**
 * Extends/implements clause
 * @author mailmindlin
 */
public interface HeritageClauseTree extends UnvisitableTree {
	List<HeritageExpressionTree> getTypes();
}

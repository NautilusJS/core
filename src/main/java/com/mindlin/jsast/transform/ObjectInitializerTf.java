package com.mindlin.jsast.transform;

import java.util.ArrayList;
import java.util.List;

import com.mindlin.jsast.impl.tree.ObjectLiteralTreeImpl;
import com.mindlin.jsast.tree.ComputedPropertyKeyTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ObjectLiteralElement;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.Tree.Kind;

public class ObjectInitializerTf implements TreeTransformation<Void> {

	protected ObjectLiteralElement mapElement() {
		
	}
	@Override
	public ExpressionTree visitObjectLiteral(ObjectLiteralTree node, Void d) {
		boolean modified = false;
		List<ObjectLiteralElement> props = new ArrayList<>();
		for (ObjectLiteralElement property : node.getProperties()) {
			ObjectPropertyKeyTree key = property.getKey();
			ExpressionTree value = property.getInitializer();
			if (key == value)
				continue;
			if (key.isComputed() && key.getKind() == Kind.OBJECT_LITERAL_PROPERTY) {
				ComputedPropertyKeyTree cp = (ComputedPropertyKeyTree) key;
				cp.getExpression();
			}
			
			props.add(property);
		}
		if (!modified)
			return node;
		return new ObjectLiteralTreeImpl(node.getStart(), node.getEnd(), props);
	}
	
	
}

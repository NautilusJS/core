package com.mindlin.jsast.tree;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public interface ObjectLiteralElementVisitor<R, D> {
	R visitAssignmentProperty(AssignmentPropertyTree node, D d);
	
	R visitGetAccessorDeclaration(GetAccessorDeclarationTree node, D d);
	
	R visitMethodDeclaration(MethodDeclarationTree node, D d);
	
	R visitSetAccessorDeclaration(SetAccessorDeclarationTree node, D d);
	
	R visitShorthandAssignmentProperty(ShorthandAssignmentPropertyTree node, D d);
	
	R visitSpread(SpreadElementTree node, D d);
}

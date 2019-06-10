package com.mindlin.jsast.impl.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mindlin.jsast.exception.JSSyntaxException;
import com.mindlin.jsast.exception.JSUnsupportedException;
import com.mindlin.jsast.impl.tree.ArrayPatternTreeImpl;
import com.mindlin.jsast.impl.tree.ObjectPatternTreeImpl;
import com.mindlin.jsast.tree.ArrayLiteralTree;
import com.mindlin.jsast.tree.AssignmentPropertyTree;
import com.mindlin.jsast.tree.AssignmentTree;
import com.mindlin.jsast.tree.BinaryExpressionTree;
import com.mindlin.jsast.tree.BooleanLiteralTree;
import com.mindlin.jsast.tree.CastExpressionTree;
import com.mindlin.jsast.tree.ClassTreeBase.ClassExpressionTree;
import com.mindlin.jsast.tree.ConditionalExpressionTree;
import com.mindlin.jsast.tree.ExpressionTree;
import com.mindlin.jsast.tree.ExpressionTreeVisitor;
import com.mindlin.jsast.tree.FunctionCallTree;
import com.mindlin.jsast.tree.FunctionExpressionTree;
import com.mindlin.jsast.tree.GetAccessorDeclarationTree;
import com.mindlin.jsast.tree.IdentifierTree;
import com.mindlin.jsast.tree.MemberExpressionTree;
import com.mindlin.jsast.tree.MethodDeclarationTree;
import com.mindlin.jsast.tree.NewTree;
import com.mindlin.jsast.tree.NullLiteralTree;
import com.mindlin.jsast.tree.NumericLiteralTree;
import com.mindlin.jsast.tree.ObjectLiteralElement;
import com.mindlin.jsast.tree.ObjectLiteralElementVisitor;
import com.mindlin.jsast.tree.ObjectLiteralTree;
import com.mindlin.jsast.tree.ObjectPatternTree.ObjectPatternElement;
import com.mindlin.jsast.tree.ParenthesizedTree;
import com.mindlin.jsast.tree.PatternTree;
import com.mindlin.jsast.tree.RegExpLiteralTree;
import com.mindlin.jsast.tree.SequenceExpressionTree;
import com.mindlin.jsast.tree.SetAccessorDeclarationTree;
import com.mindlin.jsast.tree.ShorthandAssignmentPropertyTree;
import com.mindlin.jsast.tree.SpreadElementTree;
import com.mindlin.jsast.tree.StringLiteralTree;
import com.mindlin.jsast.tree.SuperExpressionTree;
import com.mindlin.jsast.tree.TaggedTemplateLiteralTree;
import com.mindlin.jsast.tree.TemplateLiteralTree;
import com.mindlin.jsast.tree.ThisExpressionTree;
import com.mindlin.jsast.tree.Tree;
import com.mindlin.jsast.tree.Tree.Kind;
import com.mindlin.jsast.tree.UnaryTree;
import com.mindlin.jsast.tree.UnaryTree.AwaitTree;

public class PatternReinterpeter implements Function<ExpressionTree, PatternTree> {
	
	@Override
	public PatternTree apply(ExpressionTree expr) {
		return null;//TODO
	}
	
	protected PatternTree reinterpretAsPattern(ExpressionTree node) {
		return null;//TODO
	}
	
	protected PatternTree reinterpretAsPatternArrayElem(ExpressionTree node) {
		return null;//TODO
	}
	
	protected ObjectPatternElement reinterpretAsPatternObjectElem(ObjectLiteralElement node) {
		return null;//TODO
	}
	
	public class ObjectPropertyToPattern implements ObjectLiteralElementVisitor<ObjectPatternElement, Void> {
		protected ObjectPatternElement visitDefault(Tree node, Void inArray) {
			// TODO: I18N
			String message = String.format("Cannot reinterpret %s as pattern", node);
			throw new JSSyntaxException(message, node.getRange());
		}
		
		@Override
		public ObjectPatternElement visitGetAccessorDeclaration(GetAccessorDeclarationTree node, Void inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public ObjectPatternElement visitMethodDeclaration(MethodDeclarationTree node, Void inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public ObjectPatternElement visitSetAccessorDeclaration(SetAccessorDeclarationTree node, Void inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public ObjectPatternElement visitAssignmentProperty(AssignmentPropertyTree node, Void inArray) {
			// TODO: finish
			throw new JSUnsupportedException("spread -> rest", node.getRange());
		}
		
		@Override
		public ObjectPatternElement visitShorthandAssignmentProperty(ShorthandAssignmentPropertyTree node, Void inArray) {
			// TODO: finish
			throw new JSUnsupportedException("spread -> rest", node.getRange());
		}
		
		@Override
		public ObjectPatternElement visitSpread(SpreadElementTree node, Void inArray) {
			// Convert to rest
			PatternTree target = reinterpretAsPattern(node.getExpression());
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public class ExpressionToPattern implements ExpressionTreeVisitor<PatternTree, Boolean> {

		protected PatternTree visitDefault(Tree node, Boolean inArray) {
			// TODO: I18N
			String message = String.format("Cannot reinterpret %s as pattern", node);
			throw new JSSyntaxException(message, node.getRange());
		}
		
		@Override
		public PatternTree visitArrayLiteral(ArrayLiteralTree expr, Boolean inArray) {
			ArrayList<PatternTree> elements = new ArrayList<>();
			for (ExpressionTree elem : expr.getElements())
				elements.add(elem == null ? null : reinterpretAsPatternArrayElem(elem));
			elements.trimToSize();
			return new ArrayPatternTreeImpl(expr.getStart(), expr.getEnd(), elements);
		}
		
		@Override
		public PatternTree visitObjectLiteral(ObjectLiteralTree node, Boolean inArray) {
			List<ObjectPatternElement> properties = node.getProperties()
					.stream()
					.map(PatternReinterpeter.this::reinterpretAsPatternObjectElem)
					.collect(Collectors.toList());
			return new ObjectPatternTreeImpl(node.getStart(), node.getEnd(), properties);
		}
		
		@Override
		public PatternTree visitIdentifier(IdentifierTree node, Boolean inArray) {
			return node;
		}
		
		public PatternTree visitMemberExpression(MemberExpressionTree node, Boolean inArray) {
			return node;
		}

		@Override
		public PatternTree visitSpread(SpreadElementTree node, Boolean inArray) {
			if (inArray) {
				// Convert to rest
				PatternTree target = reinterpretAsPattern(node.getExpression());
				//TODO
				return null;
			} else {
				return this.visitDefault(node, inArray);
			}
		}
		
		@Override
		public PatternTree visitAssignment(AssignmentTree node, Boolean inArray) {
			//TODO
			// return new AssignmentPatternTreeImpl(expr.getStart(), expr.getEnd(), ((AssignmentTree)expr).getVariable(), ((AssignmentTree)expr).getValue());
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitAwait(AwaitTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitBinary(BinaryExpressionTree node, Boolean inArray) {
			if (node.getKind() == Kind.MEMBER_SELECT)
				return this.visitMemberExpression((MemberExpressionTree) node, inArray);
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitBooleanLiteral(BooleanLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitCast(CastExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitClassExpression(ClassExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitConditionalExpression(ConditionalExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitFunctionCall(FunctionCallTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitFunctionExpression(FunctionExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitNew(NewTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitNull(NullLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitNumericLiteral(NumericLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitParentheses(ParenthesizedTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitRegExpLiteral(RegExpLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitSequence(SequenceExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitStringLiteral(StringLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitSuper(SuperExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitTemplateLiteral(TemplateLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitTaggedTemplate(TaggedTemplateLiteralTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitThis(ThisExpressionTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
		
		@Override
		public PatternTree visitUnary(UnaryTree node, Boolean inArray) {
			return this.visitDefault(node, inArray);
		}
	}
	
}

/*
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.scoping

import com.ge.research.sadl.scoping.SADLScopeProvider
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import com.ge.research.sadl.sADL.ExpressionScope
import org.eclipse.xtext.EcoreUtil2
import com.ge.research.sadl.sADL.SadlModel

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class DialogScopeProvider extends SADLScopeProvider {

	override protected getSadlResourceScope(EObject obj, EReference reference) {
		val parent = createResourceScope(obj.eResource, null, newHashSet);
		val statement = EcoreUtil2.getContainerOfType(obj, ExpressionScope)
		if (statement !== null) {
			val model = EcoreUtil2.getContainerOfType(statement, SadlModel)
			var newParent = parent
//			for (context : model.elements.filter(RequirementContext)) {
//				if (context !== statement) {
//					newParent = getLocalVariableScope(#[context.expr], newParent)
//				}
//			}
//			if (statement instanceof RequirementContext) {
//				if (statement.expr !== null) {
//					newParent = getLocalVariableScope(#[statement.expr], newParent)
//				}
//			}
//			if (statement instanceof WherePart) {
//				if (statement.where !== null) {
//					newParent = getLocalVariableScope(#[statement.where], newParent)
//				}
//			}
//			if (statement instanceof WithWhenPart) {
//				if (statement.when !== null) {
//					newParent = getLocalVariableScope(#[statement.when], newParent);
//					if (obj.eContainer instanceof BinaryOperation) {
//						val container = obj.eContainer as BinaryOperation;
//						if ((container.op == 'is' || container.op == '=') && container.left == obj) {
//							val importedNamespace = converter.toQualifiedName(statement.name.concreteName);
//							newParent = newParent.doWrap(importedNamespace);
//						}
//					}
//				}
//			}
			return newParent
		}
		return super.getSadlResourceScope(obj, reference)
	}

}

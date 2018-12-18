/**
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.dialog;

import com.ge.research.sadl.sADL.Expression;
import com.ge.research.sadl.sADL.ExpressionScope;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Modified Ask Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.ModifiedAskStatement#getExpr <em>Expr</em>}</li>
 * </ul>
 *
 * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getModifiedAskStatement()
 * @model
 * @generated
 */
public interface ModifiedAskStatement extends ExpressionScope
{
  /**
   * Returns the value of the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expr</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expr</em>' containment reference.
   * @see #setExpr(Expression)
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getModifiedAskStatement_Expr()
   * @model containment="true"
   * @generated
   */
  Expression getExpr();

  /**
   * Sets the value of the '{@link com.ge.research.sadl.darpa.aske.dialog.ModifiedAskStatement#getExpr <em>Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expr</em>' containment reference.
   * @see #getExpr()
   * @generated
   */
  void setExpr(Expression value);

} // ModifiedAskStatement

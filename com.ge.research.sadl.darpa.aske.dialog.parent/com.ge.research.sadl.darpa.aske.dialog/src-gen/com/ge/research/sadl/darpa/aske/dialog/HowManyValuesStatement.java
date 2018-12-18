/**
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.dialog;

import com.ge.research.sadl.sADL.SadlModelElement;
import com.ge.research.sadl.sADL.SadlResource;
import com.ge.research.sadl.sADL.SadlTypeReference;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>How Many Values Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getProp <em>Prop</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getTyp <em>Typ</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getArticle <em>Article</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getCls <em>Cls</em>}</li>
 * </ul>
 *
 * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getHowManyValuesStatement()
 * @model
 * @generated
 */
public interface HowManyValuesStatement extends SadlModelElement
{
  /**
   * Returns the value of the '<em><b>Prop</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Prop</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Prop</em>' containment reference.
   * @see #setProp(SadlResource)
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getHowManyValuesStatement_Prop()
   * @model containment="true"
   * @generated
   */
  SadlResource getProp();

  /**
   * Sets the value of the '{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getProp <em>Prop</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Prop</em>' containment reference.
   * @see #getProp()
   * @generated
   */
  void setProp(SadlResource value);

  /**
   * Returns the value of the '<em><b>Typ</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Typ</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Typ</em>' containment reference.
   * @see #setTyp(SadlTypeReference)
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getHowManyValuesStatement_Typ()
   * @model containment="true"
   * @generated
   */
  SadlTypeReference getTyp();

  /**
   * Sets the value of the '{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getTyp <em>Typ</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Typ</em>' containment reference.
   * @see #getTyp()
   * @generated
   */
  void setTyp(SadlTypeReference value);

  /**
   * Returns the value of the '<em><b>Article</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Article</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Article</em>' attribute.
   * @see #setArticle(String)
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getHowManyValuesStatement_Article()
   * @model
   * @generated
   */
  String getArticle();

  /**
   * Sets the value of the '{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getArticle <em>Article</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Article</em>' attribute.
   * @see #getArticle()
   * @generated
   */
  void setArticle(String value);

  /**
   * Returns the value of the '<em><b>Cls</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Cls</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Cls</em>' containment reference.
   * @see #setCls(SadlTypeReference)
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#getHowManyValuesStatement_Cls()
   * @model containment="true"
   * @generated
   */
  SadlTypeReference getCls();

  /**
   * Sets the value of the '{@link com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement#getCls <em>Cls</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Cls</em>' containment reference.
   * @see #getCls()
   * @generated
   */
  void setCls(SadlTypeReference value);

} // HowManyValuesStatement

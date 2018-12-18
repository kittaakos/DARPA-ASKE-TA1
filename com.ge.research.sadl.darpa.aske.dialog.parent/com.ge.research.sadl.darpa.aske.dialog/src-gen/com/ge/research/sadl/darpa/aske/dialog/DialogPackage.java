/**
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.dialog;

import com.ge.research.sadl.sADL.SADLPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.ge.research.sadl.darpa.aske.dialog.DialogFactory
 * @model kind="package"
 * @generated
 */
public interface DialogPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "dialog";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.ge.com/research/sadl/darpa/aske/Dialog";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "dialog";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  DialogPackage eINSTANCE = com.ge.research.sadl.darpa.aske.dialog.impl.DialogPackageImpl.init();

  /**
   * The meta object id for the '{@link com.ge.research.sadl.darpa.aske.dialog.impl.DialogQuestionImpl <em>Question</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.ge.research.sadl.darpa.aske.dialog.impl.DialogQuestionImpl
   * @see com.ge.research.sadl.darpa.aske.dialog.impl.DialogPackageImpl#getDialogQuestion()
   * @generated
   */
  int DIALOG_QUESTION = 0;

  /**
   * The feature id for the '<em><b>Article</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIALOG_QUESTION__ARTICLE = SADLPackage.EXPRESSION_SCOPE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIALOG_QUESTION__TARGET = SADLPackage.EXPRESSION_SCOPE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Question</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIALOG_QUESTION_FEATURE_COUNT = SADLPackage.EXPRESSION_SCOPE_FEATURE_COUNT + 2;


  /**
   * Returns the meta object for class '{@link com.ge.research.sadl.darpa.aske.dialog.DialogQuestion <em>Question</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Question</em>'.
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogQuestion
   * @generated
   */
  EClass getDialogQuestion();

  /**
   * Returns the meta object for the attribute '{@link com.ge.research.sadl.darpa.aske.dialog.DialogQuestion#getArticle <em>Article</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Article</em>'.
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogQuestion#getArticle()
   * @see #getDialogQuestion()
   * @generated
   */
  EAttribute getDialogQuestion_Article();

  /**
   * Returns the meta object for the containment reference '{@link com.ge.research.sadl.darpa.aske.dialog.DialogQuestion#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Target</em>'.
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogQuestion#getTarget()
   * @see #getDialogQuestion()
   * @generated
   */
  EReference getDialogQuestion_Target();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DialogFactory getDialogFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link com.ge.research.sadl.darpa.aske.dialog.impl.DialogQuestionImpl <em>Question</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.ge.research.sadl.darpa.aske.dialog.impl.DialogQuestionImpl
     * @see com.ge.research.sadl.darpa.aske.dialog.impl.DialogPackageImpl#getDialogQuestion()
     * @generated
     */
    EClass DIALOG_QUESTION = eINSTANCE.getDialogQuestion();

    /**
     * The meta object literal for the '<em><b>Article</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DIALOG_QUESTION__ARTICLE = eINSTANCE.getDialogQuestion_Article();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIALOG_QUESTION__TARGET = eINSTANCE.getDialogQuestion_Target();

  }

} //DialogPackage
/**
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.dialog.impl;

import com.ge.research.sadl.darpa.aske.dialog.DialogFactory;
import com.ge.research.sadl.darpa.aske.dialog.DialogPackage;
import com.ge.research.sadl.darpa.aske.dialog.DialogQuestion;

import com.ge.research.sadl.sADL.SADLPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DialogPackageImpl extends EPackageImpl implements DialogPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dialogQuestionEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see com.ge.research.sadl.darpa.aske.dialog.DialogPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private DialogPackageImpl()
  {
    super(eNS_URI, DialogFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link DialogPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static DialogPackage init()
  {
    if (isInited) return (DialogPackage)EPackage.Registry.INSTANCE.getEPackage(DialogPackage.eNS_URI);

    // Obtain or create and register package
    DialogPackageImpl theDialogPackage = (DialogPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DialogPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DialogPackageImpl());

    isInited = true;

    // Initialize simple dependencies
    SADLPackage.eINSTANCE.eClass();

    // Create package meta-data objects
    theDialogPackage.createPackageContents();

    // Initialize created meta-data
    theDialogPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theDialogPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(DialogPackage.eNS_URI, theDialogPackage);
    return theDialogPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDialogQuestion()
  {
    return dialogQuestionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDialogQuestion_Article()
  {
    return (EAttribute)dialogQuestionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDialogQuestion_Target()
  {
    return (EReference)dialogQuestionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DialogFactory getDialogFactory()
  {
    return (DialogFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    dialogQuestionEClass = createEClass(DIALOG_QUESTION);
    createEAttribute(dialogQuestionEClass, DIALOG_QUESTION__ARTICLE);
    createEReference(dialogQuestionEClass, DIALOG_QUESTION__TARGET);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    SADLPackage theSADLPackage = (SADLPackage)EPackage.Registry.INSTANCE.getEPackage(SADLPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    dialogQuestionEClass.getESuperTypes().add(theSADLPackage.getExpressionScope());

    // Initialize classes and features; add operations and parameters
    initEClass(dialogQuestionEClass, DialogQuestion.class, "DialogQuestion", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDialogQuestion_Article(), ecorePackage.getEString(), "article", null, 0, 1, DialogQuestion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDialogQuestion_Target(), theSADLPackage.getSadlTypeReference(), null, "target", null, 0, 1, DialogQuestion.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //DialogPackageImpl
/**
 * generated by Xtext 2.14.0.RC1
 */
package com.ge.research.sadl.darpa.aske.dialog.impl;

import com.ge.research.sadl.darpa.aske.dialog.DialogPackage;
import com.ge.research.sadl.darpa.aske.dialog.HowManyValuesStatement;

import com.ge.research.sadl.sADL.SadlResource;
import com.ge.research.sadl.sADL.SadlTypeReference;

import com.ge.research.sadl.sADL.impl.SadlModelElementImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>How Many Values Statement</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.impl.HowManyValuesStatementImpl#getProp <em>Prop</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.impl.HowManyValuesStatementImpl#getTyp <em>Typ</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.impl.HowManyValuesStatementImpl#getArticle <em>Article</em>}</li>
 *   <li>{@link com.ge.research.sadl.darpa.aske.dialog.impl.HowManyValuesStatementImpl#getCls <em>Cls</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HowManyValuesStatementImpl extends SadlModelElementImpl implements HowManyValuesStatement
{
  /**
   * The cached value of the '{@link #getProp() <em>Prop</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getProp()
   * @generated
   * @ordered
   */
  protected SadlResource prop;

  /**
   * The cached value of the '{@link #getTyp() <em>Typ</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTyp()
   * @generated
   * @ordered
   */
  protected SadlTypeReference typ;

  /**
   * The default value of the '{@link #getArticle() <em>Article</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArticle()
   * @generated
   * @ordered
   */
  protected static final String ARTICLE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getArticle() <em>Article</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArticle()
   * @generated
   * @ordered
   */
  protected String article = ARTICLE_EDEFAULT;

  /**
   * The cached value of the '{@link #getCls() <em>Cls</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCls()
   * @generated
   * @ordered
   */
  protected SadlTypeReference cls;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HowManyValuesStatementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DialogPackage.Literals.HOW_MANY_VALUES_STATEMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SadlResource getProp()
  {
    return prop;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProp(SadlResource newProp, NotificationChain msgs)
  {
    SadlResource oldProp = prop;
    prop = newProp;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP, oldProp, newProp);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProp(SadlResource newProp)
  {
    if (newProp != prop)
    {
      NotificationChain msgs = null;
      if (prop != null)
        msgs = ((InternalEObject)prop).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP, null, msgs);
      if (newProp != null)
        msgs = ((InternalEObject)newProp).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP, null, msgs);
      msgs = basicSetProp(newProp, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP, newProp, newProp));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SadlTypeReference getTyp()
  {
    return typ;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTyp(SadlTypeReference newTyp, NotificationChain msgs)
  {
    SadlTypeReference oldTyp = typ;
    typ = newTyp;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP, oldTyp, newTyp);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTyp(SadlTypeReference newTyp)
  {
    if (newTyp != typ)
    {
      NotificationChain msgs = null;
      if (typ != null)
        msgs = ((InternalEObject)typ).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP, null, msgs);
      if (newTyp != null)
        msgs = ((InternalEObject)newTyp).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP, null, msgs);
      msgs = basicSetTyp(newTyp, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP, newTyp, newTyp));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getArticle()
  {
    return article;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setArticle(String newArticle)
  {
    String oldArticle = article;
    article = newArticle;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__ARTICLE, oldArticle, article));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SadlTypeReference getCls()
  {
    return cls;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCls(SadlTypeReference newCls, NotificationChain msgs)
  {
    SadlTypeReference oldCls = cls;
    cls = newCls;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS, oldCls, newCls);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCls(SadlTypeReference newCls)
  {
    if (newCls != cls)
    {
      NotificationChain msgs = null;
      if (cls != null)
        msgs = ((InternalEObject)cls).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS, null, msgs);
      if (newCls != null)
        msgs = ((InternalEObject)newCls).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS, null, msgs);
      msgs = basicSetCls(newCls, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS, newCls, newCls));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP:
        return basicSetProp(null, msgs);
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP:
        return basicSetTyp(null, msgs);
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS:
        return basicSetCls(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP:
        return getProp();
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP:
        return getTyp();
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__ARTICLE:
        return getArticle();
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS:
        return getCls();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP:
        setProp((SadlResource)newValue);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP:
        setTyp((SadlTypeReference)newValue);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__ARTICLE:
        setArticle((String)newValue);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS:
        setCls((SadlTypeReference)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP:
        setProp((SadlResource)null);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP:
        setTyp((SadlTypeReference)null);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__ARTICLE:
        setArticle(ARTICLE_EDEFAULT);
        return;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS:
        setCls((SadlTypeReference)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__PROP:
        return prop != null;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__TYP:
        return typ != null;
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__ARTICLE:
        return ARTICLE_EDEFAULT == null ? article != null : !ARTICLE_EDEFAULT.equals(article);
      case DialogPackage.HOW_MANY_VALUES_STATEMENT__CLS:
        return cls != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (article: ");
    result.append(article);
    result.append(')');
    return result.toString();
  }

} //HowManyValuesStatementImpl
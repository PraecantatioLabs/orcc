/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.sf.orcc.backends.ir.impl;

import java.util.Collection;

import net.sf.orcc.backends.ir.InstGetElementPtr;
import net.sf.orcc.backends.ir.IrSpecificPackage;
import net.sf.orcc.ir.Def;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.Use;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inst Get Element Ptr</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sf.orcc.backends.ir.impl.InstGetElementPtrImpl#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link net.sf.orcc.backends.ir.impl.InstGetElementPtrImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link net.sf.orcc.backends.ir.impl.InstGetElementPtrImpl#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InstGetElementPtrImpl extends IrInstSpecificImpl implements
		InstGetElementPtr {
	/**
	 * The cached value of the '{@link #getIndexes() <em>Indexes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndexes()
	 * @generated
	 * @ordered
	 */
	protected EList<Expression> indexes;

	/**
	 * The cached value of the '{@link #getTarget() <em>Target</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTarget()
	 * @generated
	 * @ordered
	 */
	protected Def target;

	/**
	 * The cached value of the '{@link #getSource() <em>Source</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSource()
	 * @generated
	 * @ordered
	 */
	protected Use source;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InstGetElementPtrImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return IrSpecificPackage.Literals.INST_GET_ELEMENT_PTR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Expression> getIndexes() {
		if (indexes == null) {
			indexes = new EObjectContainmentEList<Expression>(Expression.class,
					this, IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES);
		}
		return indexes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Def getTarget() {
		return target;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(Def newTarget,
			NotificationChain msgs) {
		Def oldTarget = target;
		target = newTarget;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET,
					IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET, oldTarget,
					newTarget);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTarget(Def newTarget) {
		if (newTarget != target) {
			NotificationChain msgs = null;
			if (target != null)
				msgs = ((InternalEObject) target)
						.eInverseRemove(
								this,
								EOPPOSITE_FEATURE_BASE
										- IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET,
								null, msgs);
			if (newTarget != null)
				msgs = ((InternalEObject) newTarget)
						.eInverseAdd(
								this,
								EOPPOSITE_FEATURE_BASE
										- IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET,
								null, msgs);
			msgs = basicSetTarget(newTarget, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET, newTarget,
					newTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Use getSource() {
		return source;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSource(Use newSource,
			NotificationChain msgs) {
		Use oldSource = source;
		source = newSource;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET,
					IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE, oldSource,
					newSource);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(Use newSource) {
		if (newSource != source) {
			NotificationChain msgs = null;
			if (source != null)
				msgs = ((InternalEObject) source)
						.eInverseRemove(
								this,
								EOPPOSITE_FEATURE_BASE
										- IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE,
								null, msgs);
			if (newSource != null)
				msgs = ((InternalEObject) newSource)
						.eInverseAdd(
								this,
								EOPPOSITE_FEATURE_BASE
										- IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE,
								null, msgs);
			msgs = basicSetSource(newSource, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE, newSource,
					newSource));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES:
			return ((InternalEList<?>) getIndexes())
					.basicRemove(otherEnd, msgs);
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET:
			return basicSetTarget(null, msgs);
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE:
			return basicSetSource(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES:
			return getIndexes();
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET:
			return getTarget();
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE:
			return getSource();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES:
			getIndexes().clear();
			getIndexes().addAll((Collection<? extends Expression>) newValue);
			return;
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET:
			setTarget((Def) newValue);
			return;
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE:
			setSource((Use) newValue);
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
	public void eUnset(int featureID) {
		switch (featureID) {
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES:
			getIndexes().clear();
			return;
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET:
			setTarget((Def) null);
			return;
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE:
			setSource((Use) null);
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
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__INDEXES:
			return indexes != null && !indexes.isEmpty();
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__TARGET:
			return target != null;
		case IrSpecificPackage.INST_GET_ELEMENT_PTR__SOURCE:
			return source != null;
		}
		return super.eIsSet(featureID);
	}

	@Override
	public boolean isGep() {
		return true;
	}

	@Override
	public boolean isLoad() {
		return true;
	}

} //InstGetElementPtrImpl

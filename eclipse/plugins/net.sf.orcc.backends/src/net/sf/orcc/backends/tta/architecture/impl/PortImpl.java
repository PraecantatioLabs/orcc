/*
 * Copyright (c) 2011, IRISA
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of IRISA nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package net.sf.orcc.backends.tta.architecture.impl;

import net.sf.orcc.backends.tta.architecture.ArchitecturePackage;
import net.sf.orcc.backends.tta.architecture.Port;
import net.sf.orcc.backends.tta.architecture.Socket;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sf.orcc.backends.tta.architecture.impl.PortImpl#getName <em>Name</em>}</li>
 *   <li>{@link net.sf.orcc.backends.tta.architecture.impl.PortImpl#getConnectedSocket <em>Connected Socket</em>}</li>
 *   <li>{@link net.sf.orcc.backends.tta.architecture.impl.PortImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link net.sf.orcc.backends.tta.architecture.impl.PortImpl#isTrigger <em>Trigger</em>}</li>
 *   <li>{@link net.sf.orcc.backends.tta.architecture.impl.PortImpl#isSetsOpcode <em>Sets Opcode</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PortImpl extends EObjectImpl implements Port {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConnectedSocket() <em>Connected Socket</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnectedSocket()
	 * @generated
	 * @ordered
	 */
	protected Socket connectedSocket;

	/**
	 * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected static final int WIDTH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected int width = WIDTH_EDEFAULT;

	/**
	 * The default value of the '{@link #isTrigger() <em>Trigger</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTrigger()
	 * @generated
	 * @ordered
	 */
	protected static final boolean TRIGGER_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isTrigger() <em>Trigger</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isTrigger()
	 * @generated
	 * @ordered
	 */
	protected boolean trigger = TRIGGER_EDEFAULT;

	/**
	 * The default value of the '{@link #isSetsOpcode() <em>Sets Opcode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetsOpcode()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SETS_OPCODE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSetsOpcode() <em>Sets Opcode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetsOpcode()
	 * @generated
	 * @ordered
	 */
	protected boolean setsOpcode = SETS_OPCODE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PortImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ArchitecturePackage.Literals.PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArchitecturePackage.PORT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Socket getConnectedSocket() {
		if (connectedSocket != null && connectedSocket.eIsProxy()) {
			InternalEObject oldConnectedSocket = (InternalEObject)connectedSocket;
			connectedSocket = (Socket)eResolveProxy(oldConnectedSocket);
			if (connectedSocket != oldConnectedSocket) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ArchitecturePackage.PORT__CONNECTED_SOCKET, oldConnectedSocket, connectedSocket));
			}
		}
		return connectedSocket;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Socket basicGetConnectedSocket() {
		return connectedSocket;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConnectedSocket(Socket newConnectedSocket) {
		Socket oldConnectedSocket = connectedSocket;
		connectedSocket = newConnectedSocket;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArchitecturePackage.PORT__CONNECTED_SOCKET, oldConnectedSocket, connectedSocket));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWidth(int newWidth) {
		int oldWidth = width;
		width = newWidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArchitecturePackage.PORT__WIDTH, oldWidth, width));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isTrigger() {
		return trigger;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTrigger(boolean newTrigger) {
		boolean oldTrigger = trigger;
		trigger = newTrigger;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArchitecturePackage.PORT__TRIGGER, oldTrigger, trigger));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetsOpcode() {
		return setsOpcode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSetsOpcode(boolean newSetsOpcode) {
		boolean oldSetsOpcode = setsOpcode;
		setsOpcode = newSetsOpcode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ArchitecturePackage.PORT__SETS_OPCODE, oldSetsOpcode, setsOpcode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ArchitecturePackage.PORT__NAME:
				return getName();
			case ArchitecturePackage.PORT__CONNECTED_SOCKET:
				if (resolve) return getConnectedSocket();
				return basicGetConnectedSocket();
			case ArchitecturePackage.PORT__WIDTH:
				return getWidth();
			case ArchitecturePackage.PORT__TRIGGER:
				return isTrigger();
			case ArchitecturePackage.PORT__SETS_OPCODE:
				return isSetsOpcode();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ArchitecturePackage.PORT__NAME:
				setName((String)newValue);
				return;
			case ArchitecturePackage.PORT__CONNECTED_SOCKET:
				setConnectedSocket((Socket)newValue);
				return;
			case ArchitecturePackage.PORT__WIDTH:
				setWidth((Integer)newValue);
				return;
			case ArchitecturePackage.PORT__TRIGGER:
				setTrigger((Boolean)newValue);
				return;
			case ArchitecturePackage.PORT__SETS_OPCODE:
				setSetsOpcode((Boolean)newValue);
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
			case ArchitecturePackage.PORT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ArchitecturePackage.PORT__CONNECTED_SOCKET:
				setConnectedSocket((Socket)null);
				return;
			case ArchitecturePackage.PORT__WIDTH:
				setWidth(WIDTH_EDEFAULT);
				return;
			case ArchitecturePackage.PORT__TRIGGER:
				setTrigger(TRIGGER_EDEFAULT);
				return;
			case ArchitecturePackage.PORT__SETS_OPCODE:
				setSetsOpcode(SETS_OPCODE_EDEFAULT);
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
			case ArchitecturePackage.PORT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ArchitecturePackage.PORT__CONNECTED_SOCKET:
				return connectedSocket != null;
			case ArchitecturePackage.PORT__WIDTH:
				return width != WIDTH_EDEFAULT;
			case ArchitecturePackage.PORT__TRIGGER:
				return trigger != TRIGGER_EDEFAULT;
			case ArchitecturePackage.PORT__SETS_OPCODE:
				return setsOpcode != SETS_OPCODE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", width: ");
		result.append(width);
		result.append(", trigger: ");
		result.append(trigger);
		result.append(", setsOpcode: ");
		result.append(setsOpcode);
		result.append(')');
		return result.toString();
	}

} //PortImpl

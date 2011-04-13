/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.sf.orcc.ir.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.orcc.OrccException;
import net.sf.orcc.ir.Action;
import net.sf.orcc.ir.FSM;
import net.sf.orcc.ir.IrFactory;
import net.sf.orcc.ir.IrPackage;
import net.sf.orcc.ir.State;
import net.sf.orcc.ir.Transition;
import net.sf.orcc.ir.Transitions;
import net.sf.orcc.util.UniqueEdge;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>FSM</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.sf.orcc.ir.impl.FSMImpl#getInitialState <em>Initial State</em>}</li>
 *   <li>{@link net.sf.orcc.ir.impl.FSMImpl#getStates <em>States</em>}</li>
 *   <li>{@link net.sf.orcc.ir.impl.FSMImpl#getTransitions <em>Transitions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FSMImpl extends EObjectImpl implements FSM {

	/**
	 * The cached value of the '{@link #getInitialState() <em>Initial State</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #getInitialState()
	 * @generated
	 * @ordered
	 */
	protected State initialState;
	/**
	 * The cached value of the '{@link #getStates() <em>States</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getStates()
	 * @generated
	 * @ordered
	 */
	protected EList<State> states;
	/**
	 * The cached value of the '{@link #getTransitions() <em>Transitions</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransitions()
	 * @generated
	 * @ordered
	 */
	protected EMap<State, Transitions> transitions;
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected FSMImpl() {
		super();
	}

	@Override
	public void addTransition(State source, Action action, State target) {
		Transitions transitions = getTransitions(source);
		if (transitions == null) {
			transitions = IrFactory.eINSTANCE.createTransitions();
			getTransitions().put(source, transitions);
		}
		
		Transition transition = IrFactory.eINSTANCE.createTransition(action, target);		
		transitions.getList().add(transition);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public State basicGetInitialState() {
		return initialState;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case IrPackage.FSM__INITIAL_STATE:
				if (resolve) return getInitialState();
				return basicGetInitialState();
			case IrPackage.FSM__STATES:
				return getStates();
			case IrPackage.FSM__TRANSITIONS:
				if (coreType) return getTransitions();
				else return getTransitions().map();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case IrPackage.FSM__STATES:
				return ((InternalEList<?>)getStates()).basicRemove(otherEnd, msgs);
			case IrPackage.FSM__TRANSITIONS:
				return ((InternalEList<?>)getTransitions()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case IrPackage.FSM__INITIAL_STATE:
				return initialState != null;
			case IrPackage.FSM__STATES:
				return states != null && !states.isEmpty();
			case IrPackage.FSM__TRANSITIONS:
				return transitions != null && !transitions.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case IrPackage.FSM__INITIAL_STATE:
				setInitialState((State)newValue);
				return;
			case IrPackage.FSM__STATES:
				getStates().clear();
				getStates().addAll((Collection<? extends State>)newValue);
				return;
			case IrPackage.FSM__TRANSITIONS:
				((EStructuralFeature.Setting)getTransitions()).set(newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return IrPackage.Literals.FSM;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case IrPackage.FSM__INITIAL_STATE:
				setInitialState((State)null);
				return;
			case IrPackage.FSM__STATES:
				getStates().clear();
				return;
			case IrPackage.FSM__TRANSITIONS:
				getTransitions().clear();
				return;
		}
		super.eUnset(featureID);
	}

	@Override
	public DirectedGraph<State, UniqueEdge> getGraph() {
		DirectedGraph<State, UniqueEdge> graph = new DirectedMultigraph<State, UniqueEdge>(
				UniqueEdge.class);
		for (State source : getStates()) {
			graph.addVertex(source);
			Transitions transitions = getTransitions(source);

			for (Transition transition : transitions.getList()) {
				State target = transition.getState();
				graph.addVertex(target);
				graph.addEdge(source, target,
						new UniqueEdge(transition.getAction()));
			}
		}

		return graph;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public State getInitialState() {
		if (initialState != null && initialState.eIsProxy()) {
			InternalEObject oldInitialState = (InternalEObject)initialState;
			initialState = (State)eResolveProxy(oldInitialState);
			if (initialState != oldInitialState) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, IrPackage.FSM__INITIAL_STATE, oldInitialState, initialState));
			}
		}
		return initialState;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList<State> getStates() {
		if (states == null) {
			states = new EObjectContainmentEList<State>(State.class, this, IrPackage.FSM__STATES);
		}
		return states;
	}

	@Override
	public List<Action> getTargetActions(State source) {
		Transitions transitions = getTransitions(source);
		List<Action> actions = new ArrayList<Action>();
		if (transitions != null) {
			for (Transition transition : transitions.getList()) {
				actions.add(transition.getAction());
			}
		}
		return actions;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<State, Transitions> getTransitions() {
		if (transitions == null) {
			transitions = new EcoreEMap<State,Transitions>(IrPackage.Literals.STATE_TO_TRANSITIONS_MAP_ENTRY, StateToTransitionsMapEntryImpl.class, this, IrPackage.FSM__TRANSITIONS);
		}
		return transitions;
	}

	@Override
	public Transitions getTransitions(State state) {
		return getTransitions().get(state);
	}

	@Override
	public void printGraph(File file) throws OrccException {
		try {
			OutputStream out = new FileOutputStream(file);
			DOTExporter<State, UniqueEdge> exporter = new DOTExporter<State, UniqueEdge>(
					new StringNameProvider<State>(), null,
					new StringEdgeNameProvider<UniqueEdge>());
			exporter.export(new OutputStreamWriter(out), getGraph());
		} catch (IOException e) {
			throw new OrccException("I/O error", e);
		}
	}

	@Override
	public void removeTransition(State source, Action action) {
		Transitions transitions = getTransitions(source);
		Iterator<Transition> it = transitions.getList().iterator();
		while (it.hasNext()) {
			Transition transition = it.next();
			Action candidate = transition.getAction();
			if (candidate == action) {
				it.remove();
				return;
			}
		}
	}

	@Override
	public void replaceTarget(State source, Action action, State target) {
		Transitions transitions = getTransitions(source);
		Iterator<Transition> it = transitions.getList().iterator();
		while (it.hasNext()) {
			Transition transition = it.next();
			Action candidate = transition.getAction();
			if (candidate == action) {
				// updates target state of this transition
				transition.setState(target);
				return;
			}
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitialState(State newInitialState) {
		State oldInitialState = initialState;
		initialState = newInitialState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, IrPackage.FSM__INITIAL_STATE, oldInitialState, initialState));
	}

} // FSMImpl

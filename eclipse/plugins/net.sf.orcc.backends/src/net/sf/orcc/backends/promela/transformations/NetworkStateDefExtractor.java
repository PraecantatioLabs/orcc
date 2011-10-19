/*
 * Copyright (c) 2011, �bo Akademi University
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
 *   * Neither the name of the �bo Akademi University nor the names of its
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

package net.sf.orcc.backends.promela.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import net.sf.orcc.OrccException;
import net.sf.orcc.ir.Action;
import net.sf.orcc.ir.Actor;
import net.sf.orcc.ir.ExprVar;
import net.sf.orcc.ir.InstAssign;
import net.sf.orcc.ir.InstCall;
import net.sf.orcc.ir.InstLoad;
import net.sf.orcc.ir.InstPhi;
import net.sf.orcc.ir.InstStore;
import net.sf.orcc.ir.NodeIf;
import net.sf.orcc.ir.NodeWhile;
import net.sf.orcc.ir.Pattern;
import net.sf.orcc.ir.Port;
import net.sf.orcc.ir.Var;
import net.sf.orcc.ir.util.AbstractActorVisitor;
import net.sf.orcc.network.Connection;
import net.sf.orcc.network.Network;
import net.sf.orcc.network.transformations.INetworkTransformation;

/**
 * This class extracts the variables/ports needed to schedule a network. The
 * resulting information is used by the promela backend to highlight the
 * variables that we need to observe when generating schedules.
 * 
 * @author Johan Ersfolk
 * 
 */
public class NetworkStateDefExtractor extends AbstractActorVisitor<Object>
		implements INetworkTransformation {

	private Map<Var, Set<Var>> variableDependency = new HashMap<Var, Set<Var>>();

	private Set<Var> varsUsedInScheduling = new HashSet<Var>();

	private Set<Port> portsUsedInScheduling = new HashSet<Port>();
	
	private Set<Port> inputPortsUsedInScheduling = new HashSet<Port>();

	private Set<Port> outputPortsUsedInScheduling = new HashSet<Port>();

	private Set<Var> variablesWithLoops = new HashSet<Var>();

	// private List<Var> stateVarsInGrd;

	// private List<Port> peekPortsInGrd;

	private Map<Port, Set<Port>> outputPortToInputPortMap = new HashMap<Port, Set<Port>>();

	// private Map<Actor, List<Var>> stateVarsInGrdMap = new HashMap<Actor,
	// List<Var>>();

	// private Map<Actor, List<Port>> peekPortsInGrdMap = new HashMap<Actor,
	// List<Port>>();

	private Map<Port, Set<Var>> outputPortToVariableMap = new HashMap<Port, Set<Var>>();

	private Var currentDeps = null;

	private Stack<Set<Var>> conditionVars = new Stack<Set<Var>>();

	private boolean inCondition = false;

	private boolean inScheduler = false;

	private Map<Port, Port> fifoTargetToSourceMap = new HashMap<Port, Port>();

	private Set<Var> visited = new HashSet<Var>();

	public NetworkStateDefExtractor() {
		super(true);
	}

	private void addVariableDep(Var target, Var source) {
		if (!variableDependency.containsKey(target)) {
			variableDependency.put(target, new HashSet<Var>());
		}
		variableDependency.get(target).add(source);
		for (Set<Var> s : conditionVars) {
			for (Var var : s) {
				variableDependency.get(target).add(var);
			}
		}
	}

	void analyzeVarDeps() {
		visited.clear();
		for (Var currentVar : variableDependency.keySet()) {
			transitiveClosure(currentVar, visited);
			if (visited.contains(currentVar)) {
				variablesWithLoops.add(currentVar);
			}
			visited.clear();
		}
	}

	@Override
	public Object caseAction(Action action) {
		// solve the port dependency
		doSwitch(action.getBody());
		inScheduler = true;
		doSwitch(action.getScheduler());
		doSwitch(action.getPeekPattern());
		inScheduler = false;
		return null;
	}

	@Override
	public Object caseActor(Actor actor) {
		// stateVarsInGrd = new ArrayList<Var>();
		// peekPortsInGrd = new ArrayList<Port>();
		// stateVarsInGrdMap.put(actor, stateVarsInGrd);
		// peekPortsInGrdMap.put(actor, peekPortsInGrd);
		for (Action action : actor.getActions()) {
			doSwitch(action);
		}
		// Find self-loops in transitive closure (a var depending on itself)
		analyzeVarDeps();
		// System.out.println(actor.getName() + "---------------------");
		// For each output port, find the variables and input ports used to produce the output value
		for (Action action : actor.getActions()) {
			for (Port port : action.getOutputPattern().getPorts()) {
				// System.out.println("Port: " + port.getName());
				visited.clear();
				Var portVar = action.getOutputPattern().getVariable(port);
				transitiveClosure(portVar, visited);
				if (!outputPortToInputPortMap.containsKey(port)) {
					outputPortToVariableMap.put(port, new HashSet<Var>());
					outputPortToInputPortMap.put(port, new HashSet<Port>());
				}
				for (Var var : visited) {
					if (var.isGlobal()) {
						outputPortToVariableMap.get(port).add(var);
						// System.out.println("Depends on Var: "+ var.getName()
						// +"  Loop: " +variablesWithLoops.contains(var));
					}
					if (action.getInputPattern().contains(var)) {
						outputPortToInputPortMap.get(port).add(
								action.getInputPattern().getPort(var));
						// System.out.println("Depends on input: " +
						// action.getInputPattern().getPort(var).getName());
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object caseExprVar(ExprVar var) {
		if (inCondition) {
			conditionVars.peek().add(var.getUse().getVariable());
		} else {
			addVariableDep(currentDeps, var.getUse().getVariable());
		}
		return null;
	}

	@Override
	public Object caseInstAssign(InstAssign assign) {
		currentDeps = assign.getTarget().getVariable();
		super.caseInstAssign(assign);
		return null;
	}

	@Override
	public Object caseInstCall(InstCall call) {
		if (call.hasResult()) {
			currentDeps = call.getTarget().getVariable();
		}
		super.caseInstCall(call);
		return null;
	}

	@Override
	public Object caseInstLoad(InstLoad load) {
		addVariableDep(load.getTarget().getVariable(), load.getSource()
				.getVariable());
		if (inScheduler) {
			// stateVarsInGrd.add(load.getSource().getVariable());
			varsUsedInScheduling.add(load.getSource().getVariable());
		}
		return null;
	}

	@Override
	public Object caseInstPhi(InstPhi phi) {
		currentDeps = phi.getTarget().getVariable();
		super.caseInstPhi(phi);
		return null;
	}

	@Override
	public Object caseInstStore(InstStore store) {
		currentDeps = store.getTarget().getVariable();
		doSwitch(store.getValue());
		return null;
	}

	@Override
	public Object caseNodeIf(NodeIf nodeIf) {
		conditionVars.push(new HashSet<Var>());
		inCondition = true;
		doSwitch(nodeIf.getCondition());
		inCondition = false;
		doSwitch(nodeIf.getThenNodes());
		doSwitch(nodeIf.getElseNodes());
		conditionVars.pop();
		doSwitch(nodeIf.getJoinNode());
		return null;
	}

	@Override
	public Object caseNodeWhile(NodeWhile nodeWhile) {
		conditionVars.push(new HashSet<Var>());
		inCondition = true;
		doSwitch(nodeWhile.getCondition());
		inCondition = false;
		doSwitch(nodeWhile.getNodes());
		conditionVars.pop();
		doSwitch(nodeWhile.getJoinNode());
		return null;
	}

	@Override
	public Object casePattern(Pattern pattern) {
		for (Port port : pattern.getPorts()) {
			// peekPortsInGrd.add(port);
			inputPortsUsedInScheduling.add(port);
		}
		return null;
	}

	/**
	 * @return the varsUsedInScheduling
	 */
	public Set<Var> getVarsUsedInScheduling() {
		return varsUsedInScheduling;
	}

	/*
	 * 1) Collects a map from Actor input ports to the output port the
	 * corresponding fifo is connected to. The map describes where the inputs
	 * comes from. 2.1) Finds output ports that produce control tokens 2.2)
	 * Finds input ports connected to output ports found in (2.1)
	 * 
	 * @param network
	 */
	private void identifyControlTokenPorts(Network network) {
		for (Connection con : network.getConnections()) {
			fifoTargetToSourceMap.put(con.getTarget(), con.getSource());
		}
		Set<Port> temp = new HashSet<Port>();
		while (true) {
			for (Port port : inputPortsUsedInScheduling) {
				if (fifoTargetToSourceMap.containsKey(port)) {
					outputPortsUsedInScheduling.add(fifoTargetToSourceMap
							.get(port));
					if (fifoTargetToSourceMap.get(port) != null) {
						for (Port in : outputPortToInputPortMap
							.get(fifoTargetToSourceMap.get(port))) {
							if (!inputPortsUsedInScheduling.contains(in)) {
								temp.add(in);
							}
						}
					}
				}
			}
			if (temp.isEmpty()) {
				break;
			} else {
				inputPortsUsedInScheduling.addAll(temp);
				temp.clear();
			}
		}
		portsUsedInScheduling.addAll(inputPortsUsedInScheduling);
		portsUsedInScheduling.addAll(outputPortsUsedInScheduling);
	}

	/**
	 * @return the portsUsedInScheduling
	 */
	public Set<Port> getPortsUsedInScheduling() {
		return portsUsedInScheduling;
	}

	private void identifySchedulingVars() {
		for (Port port : outputPortsUsedInScheduling) {
			if (outputPortToVariableMap.containsKey(port)) {
				varsUsedInScheduling.addAll(outputPortToVariableMap.get(port));
			}
		}
	}

	private void transitiveClosure(Var variable, Set<Var> transitiveClosure) {
		if (variableDependency.containsKey(variable)) {
			for (Var v : variableDependency.get(variable)) {
				if (!transitiveClosure.contains(v)) {
					transitiveClosure.add(v);
					transitiveClosure(v, transitiveClosure);
				}
			}
		}
	}

	@Override
	public void transform(Network network) throws OrccException {
		for (Actor actor : network.getActors()) {
			doSwitch(actor);
		}
		identifyControlTokenPorts(network);
		identifySchedulingVars();
	}

}

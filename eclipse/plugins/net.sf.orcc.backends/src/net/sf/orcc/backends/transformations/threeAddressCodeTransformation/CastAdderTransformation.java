/*
 * Copyright (c) 2009-2010, IETR/INSA of Rennes
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
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
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
package net.sf.orcc.backends.transformations.threeAddressCodeTransformation;

import java.util.List;

import net.sf.orcc.ir.Actor;
import net.sf.orcc.ir.CFGNode;
import net.sf.orcc.ir.Cast;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.LocalVariable;
import net.sf.orcc.ir.Location;
import net.sf.orcc.ir.Procedure;
import net.sf.orcc.ir.Type;
import net.sf.orcc.ir.Use;
import net.sf.orcc.ir.Variable;
import net.sf.orcc.ir.expr.AbstractExpressionInterpreter;
import net.sf.orcc.ir.expr.BinaryExpr;
import net.sf.orcc.ir.expr.BinaryOp;
import net.sf.orcc.ir.expr.VarExpr;
import net.sf.orcc.ir.instructions.Assign;
import net.sf.orcc.ir.instructions.Call;
import net.sf.orcc.ir.instructions.Load;
import net.sf.orcc.ir.instructions.PhiAssignment;
import net.sf.orcc.ir.instructions.Return;
import net.sf.orcc.ir.instructions.Store;
import net.sf.orcc.ir.nodes.BlockNode;
import net.sf.orcc.ir.nodes.IfNode;
import net.sf.orcc.ir.nodes.WhileNode;
import net.sf.orcc.ir.transformations.AbstractActorTransformation;

/**
 * Add cast in IR in the form of assign instruction where target's type differs
 * from source type.
 * 
 * @author Jerome GORIN
 * @author Herve Yviquel
 * 
 */
public class CastAdderTransformation extends AbstractActorTransformation {

	private class CastExprInterpreter extends AbstractExpressionInterpreter {

		@Override
		public Object interpret(BinaryExpr expr, Object... args) {
			BinaryOp op = expr.getOp();
			Expression e1 = expr.getE1();
			Expression e2 = expr.getE2();

			if (isSpecificOperation(op)) {
				Cast specificCast = new Cast(e1.getType(), e2.getType());

				if (specificCast.isExtended()) {
					expr.setType(e2.getType());
				} else if (specificCast.isTrunced()) {
					expr.setType(e1.getType());
				}
			}

			if (op.isComparison()) {
				// Check coherence between e1 and e2
				Cast castExprs = new Cast(e1.getType(), e2.getType());

				if (castExprs.isExtended()) {
					// Take e2 as the reference type
					e1 = (Expression) e1.accept(this, e2.getType());
				} else if (castExprs.isTrunced()) {
					// Take e1 as the reference type
					e2 = (Expression) e2.accept(this, e1.getType());
				}
			} else {
				// Check coherence of the overall expression
				e1 = (Expression) e1.accept(this, expr.getType());
				e2 = (Expression) e2.accept(this, expr.getType());
			}

			// Set expressions
			expr.setE1(e1);
			expr.setE2(e2);

			return expr;
		}

		@Override
		public Object interpret(VarExpr expr, Object... args) {
			Type type = (Type) args[0];

			// Check coherence between expression and type
			Cast cast = new Cast(expr.getType(), type);

			if (cast.isExtended() || cast.isTrunced()) {
				// Make a new assignment to the binary expression
				LocalVariable newVar = procedure.newTempLocalVariable(file,
						cast.getTarget(), procedure.getName() + "_" + "expr");

				newVar.setIndex(1);

				Assign assign = new Assign(newVar, expr);

				// Add assignement to instruction's list
				itInstruction.add(assign);

				return new VarExpr(new Use(newVar));
			}

			return expr;
		}

		private boolean isSpecificOperation(BinaryOp op) {
			return op == BinaryOp.MOD;
		}

	}

	private boolean castType;

	private String file;

	public CastAdderTransformation(boolean castType) {
		this.castType = castType;
	}

	private LocalVariable castTarget(LocalVariable target, Type type) {
		Cast castTarget = new Cast(target.getType(), type);

		if (castType & castTarget.isDifferent()) {
			Location location = target.getLocation();

			// Make a new assignment to the binary expression
			LocalVariable transitionVar = procedure.newTempLocalVariable(file,
					castTarget.getTarget(), procedure.getName() + "_" + "expr");

			transitionVar.setIndex(1);

			VarExpr varExpr = new VarExpr(new Use(transitionVar));

			Assign newAssign = new Assign(location, target, varExpr);

			// Add assignement to instruction's list
			itInstruction.add(newAssign);

			return transitionVar;
		} else if (castTarget.isExtended() || castTarget.isTrunced()) {
			Location location = target.getLocation();

			// Make a new assignment to the binary expression
			LocalVariable transitionVar = procedure.newTempLocalVariable(file,
					castTarget.getTarget(), procedure.getName() + "_" + "expr");

			transitionVar.setIndex(1);

			VarExpr varExpr = new VarExpr(new Use(transitionVar));

			Assign newAssign = new Assign(location, target, varExpr);

			// Add assignement to instruction's list
			itInstruction.add(newAssign);

			return transitionVar;
		}

		return target;
	}

	@Override
	public void transform(Actor actor) {
		this.file = actor.getFile();
		super.transform(actor);
	}

	@Override
	public void visit(Assign assign) {
		Expression value = assign.getValue();

		if (value.isBinaryExpr()) {
			BinaryExpr binExpr = (BinaryExpr) value;

			itInstruction.previous();

			Expression expr = (Expression) binExpr.accept(
					new CastExprInterpreter(), binExpr.getType());

			if (expr != binExpr) {
				assign.setValue(expr);
			}
			Use.addUses(assign, expr);

			itInstruction.next();

			if (!binExpr.getOp().isComparison()) {
				LocalVariable newVar = castTarget(assign.getTarget(),
						binExpr.getType());
				assign.setTarget(newVar);
			}
		}
	}

	@Override
	public void visit(Call call) {
		List<Expression> parameters = call.getParameters();
		Procedure procedure = call.getProcedure();
		if (!procedure.isNative()) {
			List<Variable> variables = call.getProcedure().getParameters()
					.getList();

			for (Expression parameter : parameters) {
				Variable variable = variables
						.get(parameters.indexOf(parameter));
				itInstruction.previous();
				Expression newParam = (Expression) parameter.accept(
						new CastExprInterpreter(), variable.getType());
				parameters.set(parameters.indexOf(parameter), newParam);
				itInstruction.next();
			}
			Use.addUses(call, call.getParameters());
		}
	}

	@Override
	public void visit(Load load) {
		LocalVariable target = load.getTarget();
		Use use = load.getSource();

		LocalVariable newVar = castTarget(target, use.getVariable().getType());

		load.setTarget(newVar);
	}

	@Override
	public void visit(PhiAssignment phi) {
		List<Expression> values = phi.getValues();
		Type type = phi.getTarget().getType();

		for (Expression value : values) {
			int indexValue = values.indexOf(value);
			CFGNode node = phi.getBlock().getPredecessors().get(indexValue);

			if (node.isBlockNode()) {
				itInstruction = ((BlockNode) node).lastListIterator();
			} else if (node.isIfNode()) {
				itInstruction = ((IfNode) node).getJoinNode()
						.lastListIterator();
			} else {
				itInstruction = ((WhileNode) node).getJoinNode()
						.lastListIterator();
			}

			Expression newValue = (Expression) value.accept(
					new CastExprInterpreter(), type);
			values.set(indexValue, newValue);
		}
		Use.addUses(phi, phi.getValues());
	}

	@Override
	public void visit(Return returnInstr) {
		Type returnType = procedure.getReturnType();

		if ((returnType != null) && (!returnType.isVoid())) {
			itInstruction.previous();
			Expression value = returnInstr.getValue();
			Expression newValue = (Expression) value.accept(
					new CastExprInterpreter(), returnType);
			returnInstr.setValue(newValue);
			itInstruction.next();
			Use.addUses(returnInstr, returnInstr.getValue());
		}
	}

	@Override
	public void visit(Store store) {
		Expression value = store.getValue();
		Variable target = store.getTarget();

		itInstruction.previous();

		Expression newValue = (Expression) value.accept(
				new CastExprInterpreter(), target.getType());

		if (value != newValue) {
			store.setValue(newValue);
			Use.addUses(store, newValue);
		}

		itInstruction.next();
	}
}

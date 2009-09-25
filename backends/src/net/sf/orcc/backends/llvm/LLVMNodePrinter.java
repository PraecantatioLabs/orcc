/*
 * Copyright (c) 2009, IETR/INSA of Rennes
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
package net.sf.orcc.backends.llvm;

import java.util.List;

import net.sf.orcc.backends.llvm.LLVMExprPrinter;
import net.sf.orcc.backends.llvm.nodes.BitcastNode;
import net.sf.orcc.backends.llvm.nodes.BrLabelNode;
import net.sf.orcc.backends.llvm.nodes.BrNode;
import net.sf.orcc.backends.llvm.nodes.GetElementPtrNode;
import net.sf.orcc.backends.llvm.nodes.LLVMNodeVisitor;
import net.sf.orcc.backends.llvm.nodes.LabelNode;
import net.sf.orcc.backends.llvm.nodes.LoadFifo;
import net.sf.orcc.backends.llvm.nodes.SelectNode;
import net.sf.orcc.backends.llvm.nodes.SextNode;
import net.sf.orcc.backends.llvm.nodes.TruncNode;
import net.sf.orcc.backends.llvm.nodes.ZextNode;
import net.sf.orcc.backends.llvm.type.PointType;
import net.sf.orcc.backends.llvm.type.LLVMAbstractType;
import net.sf.orcc.ir.VarDef;
import net.sf.orcc.ir.actor.Procedure;
import net.sf.orcc.ir.actor.VarUse;
import net.sf.orcc.ir.expr.AbstractExpr;
import net.sf.orcc.ir.expr.IntExpr;
import net.sf.orcc.ir.nodes.AbstractNode;
import net.sf.orcc.ir.nodes.AssignVarNode;
import net.sf.orcc.ir.nodes.CallNode;
import net.sf.orcc.ir.nodes.EmptyNode;
import net.sf.orcc.ir.nodes.HasTokensNode;
import net.sf.orcc.ir.nodes.IfNode;
import net.sf.orcc.ir.nodes.InitPortNode;
import net.sf.orcc.ir.nodes.JoinNode;
import net.sf.orcc.ir.nodes.LoadNode;
import net.sf.orcc.ir.nodes.PeekNode;
import net.sf.orcc.ir.nodes.PhiAssignment;
import net.sf.orcc.ir.nodes.ReadNode;
import net.sf.orcc.ir.nodes.ReturnNode;
import net.sf.orcc.ir.nodes.StoreNode;
import net.sf.orcc.ir.nodes.WhileNode;
import net.sf.orcc.ir.nodes.WriteNode;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * 
 * @author J�r�me GORIN
 * 
 */
public class LLVMNodePrinter implements LLVMNodeVisitor {

	private String actorName;

	private String attrName;

	private StringTemplateGroup group;

	private StringTemplate template;

	private LLVMVarDefPrinter varDefPrinter;

	private LLVMExprPrinter exprPrinter;
	
	private LLVMTypePrinter typeToString;
	
	public LLVMNodePrinter(StringTemplateGroup group,
			StringTemplate template, String actorName,
			LLVMVarDefPrinter varDefPrinter, LLVMExprPrinter exprPrinter,
			LLVMTypePrinter typeToString) {
		attrName = "nodes";
		this.actorName = actorName;
		this.group = group;
		this.template = template;
		this.varDefPrinter = varDefPrinter;
		this.exprPrinter = exprPrinter;
		this.typeToString = typeToString;
	}

	@Override
	public void visit(AssignVarNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("assignVarNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVar();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(BitcastNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("BitcastNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVar();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));

		template.setAttribute(attrName, nodeTmpl);
	}

	public void visit(BrLabelNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("brlabelNode");

		// varDef contains the variable (with the same name as the port)
		LabelNode labelnode = node.getLabelNode();
		nodeTmpl.setAttribute("name", labelnode.getLabelName());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(BrNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("brNode");
		 List<AbstractNode> thenNodes = node.getThenNodes();
		 List<AbstractNode> elseNodes = node.getElseNodes();

		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getCondition(),true));
		nodeTmpl.setAttribute("thenLabelNode", node.getLabelTrueNode());
		nodeTmpl.setAttribute("elseLabelNode", node.getLabelFalseNode());
		nodeTmpl.setAttribute("endLabelNode", node.getLabelEndNode());
		
		// save current template
		StringTemplate previousTempl = template;
		String previousAttrName = attrName;
		template = nodeTmpl;
		
		attrName = "thenNodes";
		for (AbstractNode subNode : thenNodes) {
			subNode.accept(this, args);
		}

		attrName = "elseNodes";
		for (AbstractNode subNode : elseNodes) {
			subNode.accept(this, args);
		}

		// restore previous template and attribute name
		attrName = previousAttrName;
		template = previousTempl;
		template.setAttribute(attrName, nodeTmpl);

		LabelNode labelTrueNode = node.getLabelTrueNode();
		LabelNode labelFalseNode = node.getLabelFalseNode();
		
		JoinNode joinNode = node.getJoinNode();
		
		if (thenNodes.isEmpty()){
			labelTrueNode = node.getLabelEntryNode();
		}
		if (elseNodes.isEmpty()){
			labelFalseNode = node.getLabelEntryNode();
		}
		
		joinNode.accept(this, labelTrueNode, labelFalseNode);
	}

	@Override
	public void visit(CallNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("callNode");
		
		if (node.hasRes()) {
			VarDef varDef = node.getRes();
			nodeTmpl.setAttribute("res", varDefPrinter.getVarDefName(varDef, false));
		}
		
		Procedure proc = node.getProcedure();

		nodeTmpl.setAttribute("return", typeToString.toString(proc.getReturnType()));
		nodeTmpl.setAttribute("name", proc.getName());
		for (AbstractExpr parameter : node.getParameters()) {
			nodeTmpl.setAttribute("parameters", exprPrinter.toString(parameter, true));
		}

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(EmptyNode node, Object... args) {
		// nothing to print
	}

	@Override
	public void visit(HasTokensNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("hasTokensNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVarDef();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("numTokens", node.getNumTokens());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(IfNode node, Object... args) {

	}

	@Override
	public void visit(JoinNode node, Object... args) {
		// there is nothing to print.
		List<PhiAssignment> phis = node.getPhis();
		if (!phis.isEmpty()) {
			for (PhiAssignment phi : phis) {
				int i = 0;

				VarDef varDef = phi.getVarDef();
				List<VarUse> varuses = phi.getVars();
				StringTemplate nodeTmpl = group.getInstanceOf("joinNode");

				// varDef contains the variable (with the same name as the port)
				nodeTmpl.setAttribute("target", varDefPrinter
						.getVarDefName(varDef, false));
				nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));
				for (VarUse varuse : varuses) {
					StringTemplate phiTmpl = group.getInstanceOf("phiNode");
					VarDef value = varuse.getVarDef();

					// name
					phiTmpl.setAttribute("value", varDefPrinter
							.getVarDefName(value, false));
					phiTmpl.setAttribute("label", (LabelNode) args[i++]);

					nodeTmpl.setAttribute("phiNode", phiTmpl);
				}

				template.setAttribute(attrName, nodeTmpl);

			}
		}
	}

	public void visit(LabelNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("labelNode");
		// varDef contains the variable (with the same name as the port)
		nodeTmpl.setAttribute("name", node.getLabelName());
		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(LoadFifo node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("loadFifo");

		// varDef contains the variable (with the same name as the port)
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("index", node.getIndex());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(LoadNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("loadNode");

		VarDef varDef = node.getTarget();
		nodeTmpl.setAttribute("target", varDefPrinter.getVarDefName(varDef, false));

		varDef = node.getSource().getVarDef();
		nodeTmpl.setAttribute("source", varDefPrinter.getVarDefName(varDef, true));

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(PeekNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("peekNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVarDef();
		nodeTmpl.setAttribute("typevar", typeToString.toString(varDef.getType()));
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("numTokens", node.getNumTokens());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(ReadNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("readNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVarDef();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("numTokens", node.getNumTokens());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(ReturnNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("returnNode");
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));
		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(SelectNode node, Object... args) {

		// there is nothing to print.
		List<PhiAssignment> phis = node.getPhis();
		AbstractExpr condition = node.getCondition();

		if (!phis.isEmpty()) {
			for (PhiAssignment phi : phis) {

				VarDef varDef = phi.getVarDef();
				List<VarUse> varuses = phi.getVars();

				StringTemplate nodeTmpl = group.getInstanceOf("selectNode");

				// varDef contains the variable (with the same name as the port)
				nodeTmpl.setAttribute("var", varDefPrinter
						.getVarDefName(varDef, false));
				nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));

				nodeTmpl.setAttribute("expr", exprPrinter.toString(condition,true));

				VarDef varDefTrue = varuses.get(0).getVarDef();
				VarDef varDefFalse = varuses.get(1).getVarDef();
		
				nodeTmpl.setAttribute("trueVar", varDefPrinter
						.getVarDefName(varDefTrue, true));
				nodeTmpl.setAttribute("falseVar", varDefPrinter
						.getVarDefName(varDefFalse, true));

				template.setAttribute(attrName, nodeTmpl);
			}
		}
	}

	@Override
	public void visit(StoreNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("storeNode");

		VarDef varDef = node.getTarget().getVarDef();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, true));

		AbstractExpr abstractexpr = node.getValue();
		if (abstractexpr instanceof IntExpr) {
			LLVMAbstractType type = (LLVMAbstractType)varDef.getType();	
			if (type instanceof PointType){
				PointType iType = (PointType)type;
				nodeTmpl.setAttribute("expr", typeToString.toString(iType.getType()) + " "
						+ exprPrinter.toString(node.getValue(),true));
			}
		} else {
			nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));
		}
		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(WhileNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("whileNode");

		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getCondition(),true));

		// save current template
		StringTemplate previousTempl = template;
		String previousAttrName = attrName;
		template = nodeTmpl;
		attrName = "nodes";

		for (AbstractNode subNode : node.getNodes()) {
			subNode.accept(this, args);
		}

		// restore previous template
		attrName = previousAttrName;
		template = previousTempl;
		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(WriteNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("writeNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVarDef();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, true));
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("numTokens", node.getNumTokens());

		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(TruncNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("TruncNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVar();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));

		template.setAttribute(attrName, nodeTmpl);
		
	}

	@Override
	public void visit(ZextNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("ZextNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVar();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));

		template.setAttribute(attrName, nodeTmpl);
		
	}

	@Override
	public void visit(GetElementPtrNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("getElementPtrNode");

		VarDef varDef = node.getVarDef();
		nodeTmpl.setAttribute("target", varDefPrinter.getVarDefName(varDef, false));

		varDef = node.getSource().getVarDef();
		nodeTmpl.setAttribute("source", varDefPrinter.getVarDefName(varDef, true));

		List<AbstractExpr> indexes = node.getIndexes();
		for (AbstractExpr index : indexes) {
			nodeTmpl.setAttribute("indexes", exprPrinter.toString(index,true));
		}

		template.setAttribute(attrName, nodeTmpl);
		
	}

	@Override
	public void visit(InitPortNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("initPortNode");
		
		nodeTmpl.setAttribute("actorName", actorName);
		nodeTmpl.setAttribute("fifoName", node.getFifoName());
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),false));
		nodeTmpl.setAttribute("index", (Integer) args[0]);
		
		template.setAttribute(attrName, nodeTmpl);
	}

	@Override
	public void visit(SextNode node, Object... args) {
		StringTemplate nodeTmpl = group.getInstanceOf("SextNode");

		// varDef contains the variable (with the same name as the port)
		VarDef varDef = node.getVar();
		nodeTmpl.setAttribute("var", varDefPrinter.getVarDefName(varDef, false));
		nodeTmpl.setAttribute("type", typeToString.toString(varDef.getType()));
		nodeTmpl.setAttribute("expr", exprPrinter.toString(node.getValue(),true));

		template.setAttribute(attrName, nodeTmpl);
		
	}
}

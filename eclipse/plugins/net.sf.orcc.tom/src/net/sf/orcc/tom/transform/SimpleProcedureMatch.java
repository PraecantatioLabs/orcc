package net.sf.orcc.tom.transform;

import org.eclipse.emf.common.util.EList;

import net.sf.orcc.df.*;
import net.sf.orcc.ir.*;
import net.sf.orcc.util.OrccLogger;
/**
* This simple class print the list of procedures named "untagged_0" in an actor
*/
public class SimpleProcedureMatch {

	private static boolean tom_equal_term_Block(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Block(Object t) {return  t instanceof net.sf.orcc.ir.Block;}private static boolean tom_equal_term_BlockL(Object l1, Object l2) {return (l1!=null && l1.equals(l2)) || l1==l2 ;}private static boolean tom_is_sort_BlockL(Object t) {return  t instanceof EList<?> && (((EList<net.sf.orcc.ir.Block>)t).size() == 0 ||
                  (((EList<net.sf.orcc.ir.Block>)t).size() > 0 && 
                    ((EList<net.sf.orcc.ir.Block>)t).get(0) instanceof net.sf.orcc.ir.Block));}private static boolean tom_equal_term_BlockBasic(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_BlockBasic(Object t) {return  t instanceof net.sf.orcc.ir.BlockBasic ;}private static boolean tom_equal_term_BlockIf(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_BlockIf(Object t) {return  t instanceof net.sf.orcc.ir.BlockIf ;}private static boolean tom_equal_term_BlockWhile(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_BlockWhile(Object t) {return  t instanceof net.sf.orcc.ir.BlockWhile ;}private static boolean tom_equal_term_Instruction(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Instruction(Object t) {return  t instanceof net.sf.orcc.ir.Instruction ;}private static boolean tom_equal_term_InstructionL(Object l1, Object l2) {return (l1!=null && l1.equals(l2)) || l1==l2 ;}private static boolean tom_is_sort_InstructionL(Object t) {return  t instanceof EList<?> && (((EList<net.sf.orcc.ir.Instruction>)t).size() == 0 ||
                  (((EList<net.sf.orcc.ir.Instruction>)t).size() > 0 && 
                    ((EList<net.sf.orcc.ir.Instruction>)t).get(0) instanceof net.sf.orcc.ir.Instruction));}private static boolean tom_equal_term_InstCall(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstCall(Object t) {return  t instanceof net.sf.orcc.ir.InstCall ;}private static boolean tom_equal_term_InstLoad(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstLoad(Object t) {return  t instanceof net.sf.orcc.ir.InstLoad ;}private static boolean tom_equal_term_InstAssign(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstAssign(Object t) {return  t instanceof net.sf.orcc.ir.InstAssign ;}private static boolean tom_equal_term_InstPhi(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstPhi(Object t) {return  t instanceof net.sf.orcc.ir.Instruction ;}private static boolean tom_equal_term_InstReturn(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstReturn(Object t) {return  t instanceof net.sf.orcc.ir.InstReturn ;}private static boolean tom_equal_term_InstStore(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstStore(Object t) {return  t instanceof net.sf.orcc.ir.InstStore ;}private static boolean tom_equal_term_InstSpecific(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_InstSpecific(Object t) {return  t instanceof net.sf.orcc.ir.InstSpecific ;}private static boolean tom_equal_term_Expression(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Expression(Object t) {return  t instanceof net.sf.orcc.ir.Expression ;}private static boolean tom_equal_term_ExpressionL(Object l1, Object l2) {return (l1!=null && l1.equals(l2)) || l1==l2 ;}private static boolean tom_is_sort_ExpressionL(Object t) {return  t instanceof EList<?> && (((EList<net.sf.orcc.ir.Expression>)t).size() == 0 ||
                  (((EList<net.sf.orcc.ir.Expression>)t).size() > 0 && 
                    ((EList<net.sf.orcc.ir.Expression>)t).get(0) instanceof net.sf.orcc.ir.Expression));}private static boolean tom_equal_term_ExprBinary(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprBinary(Object t) {return  t instanceof net.sf.orcc.ir.ExprBinary ;}private static boolean tom_equal_term_ExprUnary(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprUnary(Object t) {return  t instanceof net.sf.orcc.ir.ExprUnary ;}private static boolean tom_equal_term_ExprBool(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprBool(Object t) {return  t instanceof net.sf.orcc.ir.ExprBool ;}private static boolean tom_equal_term_ExprFloat(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprFloat(Object t) {return  t instanceof net.sf.orcc.ir.ExprFloat ;}private static boolean tom_equal_term_ExprInt(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprInt(Object t) {return  t instanceof net.sf.orcc.ir.ExprInt ;}private static boolean tom_equal_term_ExprList(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprList(Object t) {return  t instanceof net.sf.orcc.ir.ExprList ;}private static boolean tom_equal_term_ExprString(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprString(Object t) {return  t instanceof net.sf.orcc.ir.ExprString ;}private static boolean tom_equal_term_ExprVar(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ExprVar(Object t) {return  t instanceof net.sf.orcc.ir.ExprVar ;}private static boolean tom_equal_term_Type(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Type(Object t) {return  t instanceof net.sf.orcc.ir.Type ;}private static boolean tom_equal_term_TypeVoid(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeVoid(Object t) {return  t instanceof net.sf.orcc.ir.TypeVoid ;}private static boolean tom_equal_term_TypeString(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeString(Object t) {return  t instanceof net.sf.orcc.ir.TypeString ;}private static boolean tom_equal_term_TypeInt(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeInt(Object t) {return  t instanceof net.sf.orcc.ir.TypeInt ;}private static boolean tom_equal_term_TypeUint(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeUint(Object t) {return  t instanceof net.sf.orcc.ir.TypeUint ;}private static boolean tom_equal_term_TypeBool(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeBool(Object t) {return  t instanceof net.sf.orcc.ir.TypeBool ;}private static boolean tom_equal_term_TypeList(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeList(Object t) {return  t instanceof net.sf.orcc.ir.TypeList ;}private static boolean tom_equal_term_TypeFloat(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_TypeFloat(Object t) {return  t instanceof net.sf.orcc.ir.TypeFloat ;}private static boolean tom_equal_term_Procedure(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Procedure(Object t) {return  t instanceof net.sf.orcc.ir.Procedure ;}private static boolean tom_equal_term_Param(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Param(Object t) {return  t instanceof net.sf.orcc.ir.Param ;}private static boolean tom_equal_term_ParamL(Object l1, Object l2) {return (l1!=null && l1.equals(l2)) || l1==l2 ;}private static boolean tom_is_sort_ParamL(Object t) {return  t instanceof EList<?> && (((EList<net.sf.orcc.ir.Param>)t).size() == 0 ||
                  (((EList<net.sf.orcc.ir.Param>)t).size() > 0 && 
                    ((EList<net.sf.orcc.ir.Param>)t).get(0) instanceof net.sf.orcc.ir.Param));}private static boolean tom_equal_term_Arg(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Arg(Object t) {return  t instanceof net.sf.orcc.ir.Arg ;}private static boolean tom_equal_term_ArgByRef(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ArgByRef(Object t) {return  t instanceof net.sf.orcc.ir.ArgByRef ;}private static boolean tom_equal_term_ArgByVal(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_ArgByVal(Object t) {return  t instanceof net.sf.orcc.ir.ArgByVal ;}private static boolean tom_equal_term_Var(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Var(Object t) {return  t instanceof net.sf.orcc.ir.Var ;}private static boolean tom_equal_term_Use(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Use(Object t) {return  t instanceof net.sf.orcc.ir.Use ;}private static boolean tom_equal_term_Def(Object l1, Object l2) {return  l1.equals(l2) || l1 == l2 ;}private static boolean tom_is_sort_Def(Object t) {return  t instanceof net.sf.orcc.ir.Def ;}  private static boolean tom_equal_term_char(char t1, char t2) {return  t1==t2 ;}private static boolean tom_is_sort_char(char t) {return  true ;} private static boolean tom_equal_term_String(String t1, String t2) {return  t1.equals(t2) ;}private static boolean tom_is_sort_String(String t) {return  t instanceof String ;} private static boolean tom_equal_term_boolean(boolean t1, boolean t2) {return  t1==t2 ;}private static boolean tom_is_sort_boolean(boolean t) {return  true ;} private static boolean tom_is_fun_sym_proc( net.sf.orcc.ir.Procedure  t) {return t instanceof net.sf.orcc.ir.Procedure;}private static  String  tom_get_slot_proc_name( net.sf.orcc.ir.Procedure  t) {return ((net.sf.orcc.ir.Procedure)t).getName() ;}private static  net.sf.orcc.ir.Type  tom_get_slot_proc_returnType( net.sf.orcc.ir.Procedure  t) {return ((net.sf.orcc.ir.Procedure)t).getReturnType() ;}private static  EList<net.sf.orcc.ir.Param>  tom_get_slot_proc_parameters( net.sf.orcc.ir.Procedure  t) {return  ((net.sf.orcc.ir.Procedure)t).getParameters() ;}private static  EList<net.sf.orcc.ir.Block>  tom_get_slot_proc_blocks( net.sf.orcc.ir.Procedure  t) {return  ((net.sf.orcc.ir.Procedure)t).getBlocks() ;}  


	public void exec(Actor actor) {

		OrccLogger.traceln(actor.getName());
		
		for(Procedure procedure : actor.getProcs()) {
			{{if (tom_is_sort_Procedure(procedure)) {if (tom_is_fun_sym_proc((( net.sf.orcc.ir.Procedure )procedure))) {if (tom_equal_term_String("untagged_0", tom_get_slot_proc_name((( net.sf.orcc.ir.Procedure )procedure)))) {

					OrccLogger.traceln(procedure.getName());
				}}}}}

		}
	}
}

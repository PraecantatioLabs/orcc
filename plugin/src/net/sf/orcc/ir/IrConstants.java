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
package net.sf.orcc.ir;

/**
 * Constants IR fields.
 * 
 * @author Matthieu Wipliez
 * 
 */
public interface IrConstants {

	public static final String BINARY_EXPR = "2 op";

	public static final String BOP_BAND = "bitand";

	public static final String BOP_BOR = "bitor";

	public static final String BOP_BXOR = "bitxor";

	public static final String BOP_DIV = "div";

	public static final String BOP_DIV_INT = "intDiv";

	public static final String BOP_EQ = "equal";

	public static final String BOP_EXP = "exp";

	public static final String BOP_GE = "ge";

	public static final String BOP_GT = "gt";

	public static final String BOP_LAND = "and";

	public static final String BOP_LE = "le";

	public static final String BOP_LOR = "or";

	public static final String BOP_LT = "lt";

	public static final String BOP_MINUS = "minus";

	public static final String BOP_MOD = "mod";

	public static final String BOP_NE = "ne";

	public static final String BOP_PLUS = "plus";

	public static final String BOP_SHIFT_LEFT = "lshift";

	public static final String BOP_SHIFT_RIGHT = "rshift";

	public static final String BOP_TIMES = "mul";

	public static final String KEY_ACTION_SCHED = "action scheduler";

	public final static String KEY_ACTIONS = "actions";

	public final static String KEY_INITIALIZES = "initializes";

	public final static String KEY_INPUTS = "inputs";

	public final static String KEY_NAME = "name";

	public final static String KEY_OUTPUTS = "outputs";

	public final static String KEY_PROCEDURES = "procedures";

	public static final String KEY_SOURCE_FILE = "source file";

	public final static String KEY_STATE_VARS = "state variables";

	public static final String NAME_ASSIGN = "assign";

	public static final String NAME_CALL = "call";

	public static final String NAME_EMPTY = "empty";

	public static final String NAME_HAS_TOKENS = "hasTokens";

	public static final String NAME_IF = "if";

	public static final String NAME_JOIN = "join";

	public static final String NAME_LOAD = "load";

	public static final String NAME_PEEK = "peek";

	public static final String NAME_READ = "read";

	public static final String NAME_RETURN = "return";

	public static final String NAME_STORE = "store";

	public static final String NAME_WHILE = "while";

	public static final String NAME_WRITE = "write";

	public static final String UNARY_EXPR = "1 op";

	public static final String UOP_BNOT = "bitnot";

	public static final String UOP_LNOT = "not";

	public static final String UOP_MINUS = "unaryMinus";

	public static final String UOP_NUM_ELTS = "numElements";

	public static final String VAR_EXPR = "var";

}

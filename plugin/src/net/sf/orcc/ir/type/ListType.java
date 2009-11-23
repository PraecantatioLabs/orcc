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
package net.sf.orcc.ir.type;

import java.util.ArrayList;
import java.util.List;

import net.sf.orcc.OrccException;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.Type;
import net.sf.orcc.ir.expr.ExpressionEvaluator;

/**
 * This class defines a List type.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class ListType extends AbstractType {

	public static final String NAME = "List";

	private Expression size;

	private Type type;

	/**
	 * Creates a new list type with the given size and element type.
	 * 
	 * @param size
	 *            the size of this list type
	 * @param type
	 *            the type of this list's elements
	 */
	public ListType(Expression size, Type type) {
		setSize(size);
		setType(type);
	}

	@Override
	public Object accept(TypeInterpreter interpreter) {
		return interpreter.interpret(this);
	}

	@Override
	public void accept(TypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UintType) {
			ListType list = (ListType) obj;
			return (size.equals(list.size) && type.equals(list.type));
		} else {
			return false;
		}
	}

	@Override
	public List<Integer> getDimensions() {
		ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
		ArrayList<Integer> dimensions = new ArrayList<Integer>(1);
		try {
			Object size = getSize().accept(expressionEvaluator);
			if (size instanceof Integer) {
				dimensions.add((Integer) size);
				dimensions.addAll(getElementType().getDimensions());
			} else {
				throw new OrccException("expected int");
			}
		} catch (OrccException e) {
			e.printStackTrace();
		}

		return dimensions;
	}

	public Type getElementType() {
		return type;
	}

	public Expression getSize() {
		return size;
	}

	@Override
	public int getType() {
		return LIST;
	}

	public void setSize(Expression size) {
		this.size = size;
	}

	public void setType(Type type) {
		if (type == null) {
			throw new NullPointerException();
		}

		this.type = type;
	}

}

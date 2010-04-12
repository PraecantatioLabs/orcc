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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.orcc.OrccException;
import net.sf.orcc.backends.AbstractBackend;
import net.sf.orcc.backends.c.transforms.MoveReadsWritesTransformation;
import net.sf.orcc.backends.llvm.transforms.ThreeAddressCodeTransformation;
import net.sf.orcc.ir.Actor;
import net.sf.orcc.ir.ActorTransformation;
import net.sf.orcc.ir.transforms.AddInstantationProcedure;
import net.sf.orcc.ir.transforms.BuildCFG;

/**
 * LLVM back-end.
 * 
 * @author J�r�me GORIN
 * 
 */
public class LLVMBackendImpl extends AbstractBackend {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		main(LLVMBackendImpl.class, args);
	}

	private LLVMActorPrinter printer;

	@Override
	protected void doVtlCodeGeneration(List<Actor> actors) throws OrccException {
		boolean classify = getAttribute("net.sf.orcc.backends.classify", false);
		if (classify) {
			// TODO classify actors
		}

		printer = new LLVMActorPrinter();

		// transforms and prints actors
		transformActors(actors);
		printActors(actors);
	}

	@Override
	protected void printActor(Actor actor) throws OrccException {
		String outputName = path + File.separator + actor.getName() + ".s";

		try {
			printer.printActor(outputName, actor);

			boolean llvmBitcode = getAttribute(
					"net.sf.orcc.backends.llvmBitcode", false);
			if (llvmBitcode) {
				String llvmAs = getAttribute("net.sf.orcc.backends.llvm-as", "");
				if (!llvmAs.isEmpty()) {
					printBitcode(llvmAs, outputName, actor.getName());
				}
			}
		} catch (IOException e) {
			throw new OrccException("I/O error", e);
		}
	}

	protected void printBitcode(String execPath, String inputName, String actor) {
		List<String> cmdList = new ArrayList<String>();
		String outputName = path + File.separator + actor + ".bc";

		Runtime run = Runtime.getRuntime();
		cmdList.add(execPath);
		cmdList.add(inputName);
		cmdList.add("-f");
		cmdList.add("-o");
		cmdList.add(outputName);
		String[] cmd = cmdList.toArray(new String[] {});

		try {
			run.exec(cmd);
		} catch (IOException e) {
			System.err.println("Could not print bitcode : ");
			e.printStackTrace();
		}
	}

	@Override
	protected void transformActor(Actor actor) throws OrccException {
		ActorTransformation[] transformations = {
				new AddInstantationProcedure(),
				new ThreeAddressCodeTransformation(),
				new MoveReadsWritesTransformation(), new BuildCFG() };

		for (ActorTransformation transformation : transformations) {
			transformation.transform(actor);
		}
	}

}

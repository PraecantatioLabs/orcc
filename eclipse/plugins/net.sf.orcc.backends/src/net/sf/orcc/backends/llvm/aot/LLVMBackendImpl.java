/*
 * Copyright (c) 2009-2011, Artemis SudParis-IETR/INSA of Rennes
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
package net.sf.orcc.backends.llvm.aot;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dftools.graph.Vertex;
import net.sf.orcc.OrccException;
import net.sf.orcc.backends.AbstractBackend;
import net.sf.orcc.backends.StandardPrinter;
import net.sf.orcc.backends.llvm.transformations.BlockNumbering;
import net.sf.orcc.backends.llvm.transformations.BoolToIntTransformation;
import net.sf.orcc.backends.llvm.transformations.GetElementPtrAdder;
import net.sf.orcc.backends.llvm.transformations.ListInitializer;
import net.sf.orcc.backends.llvm.transformations.StringTransformation;
import net.sf.orcc.backends.transformations.CastAdder;
import net.sf.orcc.backends.transformations.EmptyBlockRemover;
import net.sf.orcc.backends.transformations.InstPhiTransformation;
import net.sf.orcc.backends.transformations.TypeResizer;
import net.sf.orcc.backends.transformations.UnitImporter;
import net.sf.orcc.backends.transformations.ssa.ConstantPropagator;
import net.sf.orcc.backends.transformations.ssa.CopyPropagator;
import net.sf.orcc.df.Actor;
import net.sf.orcc.df.Instance;
import net.sf.orcc.df.Network;
import net.sf.orcc.df.transformations.BroadcastAdder;
import net.sf.orcc.df.transformations.Instantiator;
import net.sf.orcc.df.transformations.NetworkFlattener;
import net.sf.orcc.df.util.DfSwitch;
import net.sf.orcc.df.util.DfVisitor;
import net.sf.orcc.ir.CfgNode;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.transformations.BlockCombine;
import net.sf.orcc.ir.transformations.CfgBuilder;
import net.sf.orcc.ir.transformations.DeadCodeElimination;
import net.sf.orcc.ir.transformations.DeadGlobalElimination;
import net.sf.orcc.ir.transformations.DeadVariableRemoval;
import net.sf.orcc.ir.transformations.RenameTransformation;
import net.sf.orcc.ir.transformations.SSATransformation;
import net.sf.orcc.ir.transformations.TacTransformation;

import org.eclipse.core.resources.IFile;

/**
 * LLVM back-end.
 * 
 * @author Herve Yviquel
 * 
 */
public class LLVMBackendImpl extends AbstractBackend {

	protected StandardPrinter printer;
	protected final Map<String, String> transformations;

	/**
	 * Creates a new instance of the LLVM back-end. Initializes the
	 * transformation hash map.
	 */
	public LLVMBackendImpl() {
		transformations = new HashMap<String, String>();
		transformations.put("abs", "abs_");
		transformations.put("getw", "getw_");
		transformations.put("index", "index_");
		transformations.put("min", "min_");
		transformations.put("max", "max_");
		transformations.put("select", "select_");
	}

	@Override
	public void doInitializeOptions() {
		// Set build and src directory
		File srcDir = new File(path + "/src");
		File buildDir = new File(path + "/build");

		// If directories don't exist, create them
		if (!srcDir.exists()) {
			srcDir.mkdirs();
		}

		// If directories don't exist, create them
		if (!buildDir.exists()) {
			buildDir.mkdirs();
		}

		// Set src directory as path
		path = srcDir.getAbsolutePath();
	}

	@Override
	protected void doTransformActor(Actor actor) throws OrccException {
		// do not transform actor
	}

	protected Network doTransformNetwork(Network network) throws OrccException {
		DfSwitch<?>[] transformations = { new Instantiator(fifoSize),
				new NetworkFlattener(), new BroadcastAdder(),
				new TypeResizer(true, true, false), new UnitImporter(),
				new DfVisitor<Void>(new SSATransformation()),
				new DeadGlobalElimination(),
				new DfVisitor<Void>(new DeadCodeElimination()),
				new DfVisitor<Void>(new DeadVariableRemoval()),
				new BoolToIntTransformation(), new StringTransformation(),
				new RenameTransformation(this.transformations),
				new DfVisitor<Expression>(new TacTransformation()),
				new DfVisitor<Void>(new CopyPropagator()),
				new DfVisitor<Void>(new ConstantPropagator()),
				new DfVisitor<Void>(new InstPhiTransformation()),
				new DfVisitor<Void>(new GetElementPtrAdder()),
				new DfVisitor<Expression>(new CastAdder(false)),
				new DfVisitor<Void>(new EmptyBlockRemover()),
				new DfVisitor<Void>(new BlockCombine()),
				new DfVisitor<CfgNode>(new CfgBuilder()),
				new DfVisitor<Void>(new ListInitializer()),
				new DfVisitor<Void>(new BlockNumbering()) };

		for (DfSwitch<?> transformation : transformations) {
			transformation.doSwitch(network);
		}

		network.computeTemplateMaps();

		for (Actor actor : network.getAllActors()) {
			actor.setTemplateData(new LLVMTemplateData().compute(actor));
		}

		return network;
	}

	@Override
	protected void doVtlCodeGeneration(List<IFile> files) throws OrccException {
		// do not generate a VTL
	}

	@Override
	protected void doXdfCodeGeneration(Network network) throws OrccException {
		network = doTransformNetwork(network);

		// print instances and entities
		printer = new StandardPrinter("net/sf/orcc/backends/llvm/aot/Actor.stg");
		printer.setExpressionPrinter(new LLVMExpressionPrinter());
		printer.setTypePrinter(new LLVMTypePrinter());
		printer.getOptions().put("fifoSize", fifoSize);
		printInstances(network);
		printEntities(network);

		// print network
		write("Printing network...\n");
		StandardPrinter printer = new StandardPrinter(
				"net/sf/orcc/backends/llvm/aot/Network.stg");
		printer.setExpressionPrinter(new LLVMExpressionPrinter());
		printer.setTypePrinter(new LLVMTypePrinter());
		printer.print(network.getSimpleName() + ".ll", path, network);

		StandardPrinter networkPrinter = new StandardPrinter(
				"net/sf/orcc/backends/llvm/aot/CMakeLists.stg");
		networkPrinter.print("CMakeLists.txt", path, network);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.orcc.backends.AbstractBackend#exportRuntimeLibrary()
	 */
	@Override
	public boolean exportRuntimeLibrary() throws OrccException {
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			File targetPath = new File(path).getParentFile();
			copyFileToFilesystem("/runtime/run_cmake_with_VS_env.bat",
					targetPath + File.separator + "run_cmake_with_VS_env.bat");
		}

		String target = path + File.separator + "libs";
		write("Export libraries sources into " + target + "... ");
		if (copyFolderToFileSystem("/runtime/C", target)) {
			write("OK" + "\n");
			return true;
		} else {
			write("Error" + "\n");
			return false;
		}
	}

	@Override
	protected boolean printInstance(Instance instance) {
		return printer.print(instance.getSimpleName() + ".ll", path, instance);
	}

	@Override
	protected boolean printEntity(Vertex vertex) {
		return printer.print(vertex.getLabel() + ".ll", path, vertex);
	}

}
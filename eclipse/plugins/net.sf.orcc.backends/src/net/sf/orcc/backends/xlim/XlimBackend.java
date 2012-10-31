/*
 * Copyright (c) 2009-2011, Ecole Polytechnique Fédérale de Lausanne
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
 *   * Neither the name of the Ecole Polytechnique Fédérale de Lausanne nor the names of its
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
package net.sf.orcc.backends.xlim;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.sf.orcc.backends.AbstractBackend;
import net.sf.orcc.backends.CustomPrinter;
import net.sf.orcc.backends.StandardPrinter;
import net.sf.orcc.backends.transform.CastAdder;
import net.sf.orcc.backends.transform.DivisionSubstitution;
import net.sf.orcc.backends.transform.EmptyBlockRemover;
import net.sf.orcc.backends.transform.Inliner;
import net.sf.orcc.backends.transform.InstPhiTransformation;
import net.sf.orcc.backends.transform.Multi2MonoToken;
import net.sf.orcc.backends.transform.StoreOnceTransformation;
import net.sf.orcc.backends.transform.TypeResizer;
import net.sf.orcc.backends.xlim.transform.CustomPeekAdder;
import net.sf.orcc.backends.xlim.transform.GlobalArrayInitializer;
import net.sf.orcc.backends.xlim.transform.InstTernaryAdder;
import net.sf.orcc.backends.xlim.transform.ListFlattener;
import net.sf.orcc.backends.xlim.transform.LiteralIntegersAdder;
import net.sf.orcc.backends.xlim.transform.LocalArrayRemoval;
import net.sf.orcc.backends.xlim.transform.UnaryListRemoval;
import net.sf.orcc.backends.xlim.transform.XlimVariableRenamer;
import net.sf.orcc.df.Actor;
import net.sf.orcc.df.DfFactory;
import net.sf.orcc.df.Instance;
import net.sf.orcc.df.Network;
import net.sf.orcc.df.transform.Instantiator;
import net.sf.orcc.df.transform.NetworkFlattener;
import net.sf.orcc.df.transform.UnitImporter;
import net.sf.orcc.df.util.DfSwitch;
import net.sf.orcc.df.util.DfVisitor;
import net.sf.orcc.graph.Vertex;
import net.sf.orcc.ir.CfgNode;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.transform.BlockCombine;
import net.sf.orcc.ir.transform.ControlFlowAnalyzer;
import net.sf.orcc.ir.transform.DeadCodeElimination;
import net.sf.orcc.ir.transform.DeadGlobalElimination;
import net.sf.orcc.ir.transform.DeadVariableRemoval;
import net.sf.orcc.ir.transform.SSATransformation;
import net.sf.orcc.ir.transform.TacTransformation;
import net.sf.orcc.ir.util.IrUtil;
import net.sf.orcc.util.OrccLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * This class defines a template-based XLIM back-end.
 * 
 * @author Ghislain Roquier
 * @author Herve Yviquel
 * @author Mickael Raulet
 * @author Endri Bezati
 * 
 */
@Deprecated
public class XlimBackend extends AbstractBackend {

	private List<String> entities;

	private HashSet<String> entitySet;

	private String fpgaType;

	private boolean useHw;

	private boolean useMulti2mono;

	private void computeEntityList(Instance instance) {
		if (instance.isActor()) {
			String name = instance.getName();
			if (!entitySet.contains(name)) {
				entitySet.add(name);
				entities.add(name);
			}
		} else if (instance.isNetwork()) {
			String name = instance.getName();
			Network network = instance.getNetwork();
			if (!entitySet.contains(name)) {
				for (Vertex vertex : network.getChildren()) {
					Instance subInstance = vertex.getAdapter(Instance.class);
					computeEntityList(subInstance);
				}

				entitySet.add(name);
			}
		}
	}

	private Map<Integer, List<Instance>> computeMapping(Network network,
			Map<String, String> mapping) {
		Map<Integer, List<Instance>> computedMap = new HashMap<Integer, List<Instance>>();

		for (Vertex vertex : network.getChildren()) {
			Instance instance = vertex.getAdapter(Instance.class);
			String path = instance.getHierarchicalName();
			String component = mapping.get(path);
			if (component != null) {
				int coreId = Integer.parseInt(component.substring(1));
				List<Instance> actors = computedMap.get(coreId);
				if (actors == null) {
					actors = new ArrayList<Instance>();
					computedMap.put(coreId, actors);
				}
				actors.add(instance);
			}
		}

		return computedMap;
	}

	@Override
	public void doInitializeOptions() {
		useHw = getAttribute("net.sf.orcc.backends.xlimHard", false);
		fpgaType = getAttribute("net.sf.orcc.backends.xlimFpgaType",
				"xc2vp30-7-ff1152");
		useMulti2mono = getAttribute("net.sf.orcc.backends.multi2mono", false);
	}

	@Override
	protected void doTransformActor(Actor actor) {
		XlimActorTemplateData data = new XlimActorTemplateData();
		actor.setTemplateData(data);

		if (useHw) {
			new StoreOnceTransformation().doSwitch(actor);
			new TypeResizer(false, true, false);
		}

		if (useMulti2mono) {
			new Multi2MonoToken().doSwitch(actor);
			new LocalArrayRemoval().doSwitch(actor);
			new DivisionSubstitution().doSwitch(actor);
			new UnaryListRemoval().doSwitch(actor);
		}

		DfSwitch<?>[] transformations = { new UnitImporter(),
				new DfVisitor<Void>(new Inliner(true, true)),
				new DfVisitor<Void>(new SSATransformation()),
				new DeadGlobalElimination(),
				new DfVisitor<Void>(new DeadCodeElimination()),
				new DfVisitor<Void>(new DeadVariableRemoval()),
				new GlobalArrayInitializer(useHw),
				new DfVisitor<Void>(new InstTernaryAdder()),
				new CustomPeekAdder(),
				new DfVisitor<Void>(new ListFlattener()),
				new DfVisitor<Expression>(new TacTransformation()),
				new DfVisitor<Void>(new InstPhiTransformation()),
				new DfVisitor<Void>(new EmptyBlockRemover()),
				new DfVisitor<Void>(new BlockCombine()),
				new DfVisitor<CfgNode>(new ControlFlowAnalyzer()),

				new DfVisitor<Expression>(new LiteralIntegersAdder()),
				new DfVisitor<Expression>(new CastAdder(true, true)),
				new XlimVariableRenamer() };

		for (DfSwitch<?> transformation : transformations) {
			transformation.doSwitch(actor);
			ResourceSet set = new ResourceSetImpl();
			if (debug && !IrUtil.serializeActor(set, path, actor)) {
				System.out.println("oops " + transformation + " "
						+ actor.getName());
			}
		}

		data.computeTemplateMaps(actor);
	}

	@Override
	protected void doVtlCodeGeneration(List<IFile> files) {
		// do not generate an XLIM VTL
	}

	@Override
	protected void doXdfCodeGeneration(Network network) {
		// instantiate and flattens network
		new Instantiator(false, fifoSize).doSwitch(network);
		new NetworkFlattener().doSwitch(network);

		transformActors(network.getAllActors());

		printInstances(network);

		network.computeTemplateMaps();
		XlimNetworkTemplateData data = new XlimNetworkTemplateData();
		data.computeTemplateMaps(network);
		network.setTemplateData(data);

		OrccLogger.traceln("Printing network...");
		printNetwork(network);
	}

	private void printCMake(Network network) {
		StandardPrinter networkPrinter = new StandardPrinter(
				"net/sf/orcc/backends/xlim/sw/CMakeLists.stg");
		networkPrinter.print("CMakeLists.txt", path, network);
	}

	@Override
	protected boolean printInstance(Instance instance) {
		StandardPrinter printer;
		if (useHw) {
			printer = new StandardPrinter(
					"net/sf/orcc/backends/xlim/hw/Actor.stg", !debug);
			printer.getOptions().put("fpgaType", fpgaType);
		} else {
			printer = new StandardPrinter(
					"net/sf/orcc/backends/xlim/sw/Actor.stg", !debug);
		}

		printer.setExpressionPrinter(new XlimExprPrinter());
		printer.setTypePrinter(new XlimTypePrinter());
		return printer.print(instance.getName() + ".xlim", path, instance);
	}

	private void printMapping(Network network, Map<String, String> mapping) {
		StandardPrinter networkPrinter = new StandardPrinter(
				"net/sf/orcc/backends/xlim/sw/Mapping.stg");
		networkPrinter.getOptions().put("mapping",
				computeMapping(network, mapping));
		networkPrinter.getOptions().put("fifoSize", fifoSize);
		networkPrinter.print(network.getName() + ".xcf", path, network);
	}

	private void printNetwork(Network network) {
		StandardPrinter printer;
		String file = network.getSimpleName();
		if (useHw) {
			// create a folder where to put .v files generated with openforge
			File sourceFolder = new File(path + File.separator + "Design");
			if (!sourceFolder.exists()) {
				sourceFolder.mkdir();
			}
			// generate instances test bench
			File folder = new File(path + File.separator + "Testbench");
			if (!folder.exists()) {
				folder.mkdir();
			}
			StandardPrinter instancePrinter = new StandardPrinter(
					"net/sf/orcc/backends/xlim/hw/ModelSim_Testbench.stg");
			Instance instance = DfFactory.eINSTANCE.createInstance(
					network.getName(), network);
			printTestbench(instancePrinter, instance);
			printTCL(instance);

			file += ".vhd";
			printer = new StandardPrinter(
					"net/sf/orcc/backends/xlim/hw/Network.stg");
		} else {
			file += ".c";
			printer = new StandardPrinter(
					"net/sf/orcc/backends/xlim/sw/Network.stg");
		}

		printer.setExpressionPrinter(new XlimExprPrinter());
		printer.setTypePrinter(new XlimTypePrinter());
		printer.getOptions().put("fifoSize", fifoSize);
		printer.print(file, path, network);
		if (!useHw) {
			printCMake(network);
			if (!mapping.isEmpty()) {
				printMapping(network, mapping);
			}
		}
	}

	private void printTCL(Instance instance) {
		CustomPrinter printer = new CustomPrinter(
				"net/sf/orcc/backends/xlim/hw/ModelSim_Script.stg");

		entities = new ArrayList<String>();
		entitySet = new HashSet<String>();
		computeEntityList(instance);

		printer.print("TCLLists.tcl", path, "TCLLists", "name", instance
				.getNetwork().getName(), "entities", entities);
	}

	private void printTestbench(StandardPrinter printer, Instance instance) {
		printer.print(instance.getName() + "_tb.vhd", path + File.separator
				+ "Testbench", instance);
	}

	public void setFpgaType(String fpgaType) {
		this.fpgaType = fpgaType;
	}

	public void setHardwareGen(Boolean hardwareGen) {
		this.useHw = hardwareGen;
	}
}
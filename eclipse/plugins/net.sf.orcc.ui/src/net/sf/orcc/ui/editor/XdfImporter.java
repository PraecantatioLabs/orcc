/*
 * Copyright (c) 2011, IETR/INSA of Rennes
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
package net.sf.orcc.ui.editor;

import static net.sf.graphiti.GraphitiModelPlugin.getDefault;
import static net.sf.graphiti.model.ObjectType.PARAMETER_ID;
import static net.sf.graphiti.model.ObjectType.PARAMETER_REFINEMENT;
import static net.sf.graphiti.model.ObjectType.PARAMETER_SOURCE_PORT;
import static net.sf.graphiti.model.ObjectType.PARAMETER_TARGET_PORT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.graphiti.io.LayoutReader;
import net.sf.graphiti.model.Configuration;
import net.sf.graphiti.model.Edge;
import net.sf.graphiti.model.Graph;
import net.sf.graphiti.model.ObjectType;
import net.sf.graphiti.model.Vertex;
import net.sf.orcc.OrccException;
import net.sf.orcc.OrccRuntimeException;
import net.sf.orcc.ir.Expression;
import net.sf.orcc.ir.Port;
import net.sf.orcc.ir.Type;
import net.sf.orcc.ir.Var;
import net.sf.orcc.ir.util.ExpressionPrinter;
import net.sf.orcc.network.Connection;
import net.sf.orcc.network.Instance;
import net.sf.orcc.network.Network;
import net.sf.orcc.network.attributes.IAttribute;
import net.sf.orcc.network.attributes.IFlagAttribute;
import net.sf.orcc.network.attributes.IValueAttribute;
import net.sf.orcc.network.serialize.XDFParser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jgrapht.DirectedGraph;

/**
 * This class defines a transformation from a file containing an XDF network to
 * a Graphiti graph.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class XdfImporter {

	private Map<net.sf.orcc.network.Vertex, Vertex> vertexMap;

	private void addConnections(Graph graph, ObjectType type,
			DirectedGraph<net.sf.orcc.network.Vertex, Connection> networkGraph) {
		for (Connection connection : networkGraph.edgeSet()) {
			Port srcPort = connection.getSource();
			Port tgtPort = connection.getTarget();
			net.sf.orcc.network.Vertex srcVertex = networkGraph
					.getEdgeSource(connection);
			net.sf.orcc.network.Vertex tgtVertex = networkGraph
					.getEdgeTarget(connection);

			Vertex source = vertexMap.get(srcVertex);
			Vertex target = vertexMap.get(tgtVertex);

			Edge edge = new Edge(type, source, target);
			if (srcPort != null) {
				edge.setValue(PARAMETER_SOURCE_PORT, srcPort.getName());
			}
			if (tgtPort != null) {
				edge.setValue(PARAMETER_TARGET_PORT, tgtPort.getName());
			}

			// buffer size
			Integer size = connection.getSize();
			if (size != null) {
				edge.setValue("buffer size", size);
			}

			graph.addEdge(edge);
		}
	}

	private void addParameters(Graph graph, Network network) {
		@SuppressWarnings("unchecked")
		List<Object> parameters = (List<Object>) graph
				.getValue("network parameter");
		for (Var parameter : network.getParameters()) {
			Type type = parameter.getType();
			String name = parameter.getName();
			parameters.add(type.toString() + " " + name);
		}
	}

	private void addVariables(Graph graph, Network network) {
		@SuppressWarnings("unchecked")
		Map<Object, Object> parameters = (Map<Object, Object>) graph
				.getValue("network variable declaration");
		for (Var variable : network.getVariables()) {
			Type type = variable.getType();
			String name = variable.getName();
			Expression value = variable.getInitialValue();
			parameters.put(type.toString() + " " + name,
					new ExpressionPrinter().doSwitch(value));
		}
	}

	private void addVertices(Graph graph, Network network) {
		Configuration configuration = graph.getConfiguration();
		for (net.sf.orcc.network.Vertex networkVertex : network.getGraph()
				.vertexSet()) {
			Vertex vertex;
			if (networkVertex.isPort()) {
				Port port = networkVertex.getPort();
				String kind = (network.getInputs().contains(port)) ? "Input"
						: "Output";
				ObjectType type = configuration.getVertexType(kind + " port");

				vertex = new Vertex(type);
				vertex.setValue("port type", port.getType().toString());
				vertex.setValue("native", port.isNative());
				vertex.setValue(PARAMETER_ID, port.getName());
				graph.addVertex(vertex);
			} else {
				Instance instance = networkVertex.getInstance();
				vertex = getVertex(instance,
						configuration.getVertexType("Instance"));
			}

			// add vertex
			vertexMap.put(networkVertex, vertex);
			graph.addVertex(vertex);
		}
	}

	private Vertex getVertex(Instance instance, ObjectType type) {
		Vertex vertex = new Vertex(type);
		vertex.setValue(PARAMETER_ID, instance.getId());
		vertex.setValue(PARAMETER_REFINEMENT, instance.getClasz());

		// parameters
		Map<String, String> parameters = new HashMap<String, String>();
		vertex.setValue("instance parameter", parameters);
		for (Entry<String, Expression> entry : instance.getParameters()
				.entrySet()) {
			parameters.put(entry.getKey(),
					new ExpressionPrinter().doSwitch(entry.getValue()));
		}

		// attributes
		IAttribute partName = instance.getAttribute("partName");
		if (partName instanceof IValueAttribute) {
			vertex.setValue("partName", new ExpressionPrinter()
					.doSwitch(((IValueAttribute) partName).getValue()));
		}

		IAttribute clockDomain = instance.getAttribute("clockDomain");
		if (clockDomain instanceof IValueAttribute) {
			vertex.setValue("clockDomain", new ExpressionPrinter()
					.doSwitch(((IValueAttribute) clockDomain).getValue()));
		}

		IAttribute skip = instance.getAttribute("skip");
		if (skip instanceof IFlagAttribute) {
			vertex.setValue("skip", true);
		}

		return vertex;
	}

	/**
	 * Transforms the given file to a Graphiti graph.
	 * 
	 * @param file
	 *            a file
	 * @return a graph
	 */
	public Graph transform(IFile file) {
		vertexMap = new HashMap<net.sf.orcc.network.Vertex, Vertex>();

		XDFParser parser = new XDFParser(file);
		Network network;
		try {
			network = parser.parseNetwork();
		} catch (OrccException e) {
			throw new RuntimeException(e.getCause());
		}

		Configuration configuration = getDefault().getConfiguration("XDF");
		ObjectType type = configuration.getGraphType("XML Dataflow Network");
		Graph graph = new Graph(configuration, type, true);

		graph.setValue(ObjectType.PARAMETER_ID, network.getName());

		addParameters(graph, network);
		addVariables(graph, network);

		addVertices(graph, network);
		addConnections(graph, configuration.getEdgeType("Connection"),
				network.getGraph());

		// read layout
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		file = root.getFile(file.getFullPath().removeFileExtension()
				.addFileExtension("layout"));
		if (file.exists()) {
			try {
				new LayoutReader().read(graph, file.getContents());
			} catch (CoreException e) {
				throw new OrccRuntimeException("error when reading layout", e);
			}
		}

		return graph;
	}

}
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
package net.sf.orcc.df.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.orcc.OrccException;
import net.sf.orcc.df.Attribute;
import net.sf.orcc.df.Connection;
import net.sf.orcc.df.DfFactory;
import net.sf.orcc.df.Instance;
import net.sf.orcc.df.Network;
import net.sf.orcc.df.Vertex;

/**
 * This class defines a transformation that flattens a given network in-place.
 * 
 * @author Matthieu Wipliez
 * @author Ghislain Roquier
 * 
 */
public class NetworkFlattener implements INetworkTransformation {

	private Map<String, Integer> identifiers;

	public NetworkFlattener() {
		identifiers = new HashMap<String, Integer>();
	}

	/**
	 * Copies all instances and edges between them of subGraph in graph
	 * 
	 * @throws OrccException
	 */
	private void copySubGraph(List<Attribute> attrs, Network network,
			Instance instance) {
		Network subNetwork = instance.getNetwork();

		List<Vertex> vertexSet = new ArrayList<Vertex>(subNetwork.getVertices());
		List<Connection> edgeSet = new ArrayList<Connection>(
				subNetwork.getConnections());

		for (Vertex vertex : vertexSet) {
			if (vertex.isInstance()) {
				Instance subInstance = (Instance) vertex;

				// get a unique identifier
				String id = getUniqueIdentifier(instance.getId(), subInstance);
				subInstance.setId(id);

				// copy attributes
				List<Attribute> vertexAttrs = subInstance.getAttributes();
				vertexAttrs.addAll(attrs);

				network.getVertices().add(vertex);
			}
		}

		for (Connection edge : edgeSet) {
			Vertex srcVertex = edge.getSource();
			Vertex tgtVertex = edge.getTarget();

			if (srcVertex.isInstance() && tgtVertex.isInstance()) {
				network.addConnection(srcVertex, tgtVertex, edge);
			}
		}
	}

	/**
	 * Returns a unique id in the given network.
	 */
	private String getUniqueIdentifier(String parentId, Instance instance) {
		String id = parentId + "_" + instance.getId();
		if (identifiers.containsKey(id)) {
			// identifier exists in the graph => generates a new one
			int num = identifiers.get(id);
			String newId = String.format(id + "_%02d", num);
			while (identifiers.containsKey(newId)) {
				num++;
				newId = String.format(id + "_%02d", num);
			}
			identifiers.put(id, num + 1);
			return newId;
		} else {
			// identifier does not exist in the graph: returns the original id
			identifiers.put(id, 0);
			return id;
		}
	}

	/**
	 * Links each predecessor of vertex to the successors of the input port in
	 * subGraph
	 * 
	 * @param vertex
	 *            the parent graph
	 * @param graph
	 *            the parent graph
	 * @param subNetwork
	 *            the child network
	 * @throws OrccException
	 */
	private void linkIncomingConnections(Vertex vertex, Network network,
			Network subNetwork) {
		List<Connection> incomingEdges = new ArrayList<Connection>(
				vertex.getIncomingEdges());
		for (Connection edge : incomingEdges) {
			Vertex v = (Vertex) edge.getTargetPort();
			List<Connection> outgoingEdges = v.getOutgoingEdges();

			for (Connection newEdge : outgoingEdges) {
				Connection incoming = DfFactory.eINSTANCE.createConnection(
						edge.getSourcePort(), newEdge.getTargetPort(),
						edge.getAttributes());
				network.addConnection(edge.getSource(), newEdge.getTarget(),
						incoming);
			}
		}
	}

	/**
	 * Links each successor of vertex to the predecessors of the output port in
	 * subGraph
	 * 
	 * @param vertex
	 *            the current vertex
	 * @param graph
	 *            the parent graph
	 * @param subNetwork
	 *            the child network
	 * @throws OrccException
	 */
	private void linkOutgoingConnections(Vertex vertex, Network network,
			Network subNetwork) {
		List<Connection> outgoingEdges = new ArrayList<Connection>(
				vertex.getOutgoingEdges());
		for (Connection edge : outgoingEdges) {
			Vertex v = (Vertex) edge.getSourcePort();
			List<Connection> incomingEdges = v.getIncomingEdges();

			for (Connection newEdge : incomingEdges) {
				Connection incoming = DfFactory.eINSTANCE.createConnection(
						newEdge.getSourcePort(), edge.getTargetPort(),
						edge.getAttributes());
				network.addConnection(newEdge.getSource(), edge.getTarget(),
						incoming);
			}
		}
	}

	@Override
	public void transform(Network network) {
		for (Instance instance : network.getInstances()) {
			identifiers.put(instance.getId(), 0);
		}

		List<Instance> instances = new ArrayList<Instance>(
				network.getInstances());
		for (Instance instance : instances) {
			if (instance.isNetwork()) {
				Network subNetwork = instance.getNetwork();

				// flatten this sub-network
				subNetwork.flatten();

				// copy vertices and edges
				copySubGraph(instance.getAttributes(), network, instance);
				linkOutgoingConnections(instance, network, subNetwork);
				linkIncomingConnections(instance, network, subNetwork);

				// remove instance from network
				network.getInstances().remove(instance);
			}
		}

		for (Instance instance : network.getInstances()) {
			instance.getHierarchicalClass().add(0, network.getName());
		}
	}

}

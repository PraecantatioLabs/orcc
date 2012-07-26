/*
 * Copyright (c) 2012, IETR/INSA of Rennes
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
 *   * Neither the name of IRISA nor the names of its
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
package net.sf.orcc.backends.java;

import java.io.File;
import java.util.Map;

import net.sf.orcc.OrccRuntimeException;
import net.sf.orcc.backends.CommonPrinter;
import net.sf.orcc.df.Actor;
import net.sf.orcc.df.Network;
import net.sf.orcc.ir.util.ExpressionPrinter;

/**
 * This class defines a printer for "standard" objects, namely actors,
 * instances, and networks. This class supports caching in order not to
 * regenerate all files all the time, which can be annoying.
 * 
 * @author Herve Yviquel
 * @author Matthieu Wipliez
 * @author Ghislain Roquier
 * @author Antoine Lorence
 * 
 */
public class JavaPrinter extends CommonPrinter {
	
	ExpressionPrinter exprPrinter;

	public JavaPrinter() {
		this(false);
	}

	public JavaPrinter(boolean keepUnchangedFiles) {
		super(keepUnchangedFiles);
		exprPrinter = new JavaExprPrinter();
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	/**
	 * Prints the given instance to a file whose name and path are given.
	 * 
	 * @param folder
	 *            output directory
	 * @param actor
	 *            the actor to generate code for
	 * @return <code>true</code> if the actor file was cached
	 */
	public boolean print(String folder, Actor actor) {
		String file = folder + File.separator + actor.getSimpleName() + ".java";
		
		if (!actor.isNative()) {
			if (keepUnchangedFiles) {
				long sourceLastModified = getLastModified(actor);
				File targetFile = new File(file);
				if (sourceLastModified < targetFile.lastModified()) {
					return true;
				}
			}
			CharSequence sequence = new ActorPrinter(actor).getActorFileContent();
			if (!printFile(sequence.toString(), file)) {
				throw new OrccRuntimeException("Unable to write file " + file);
			}
		}
		return false;
	}

	/**
	 * Prints the given network to a file whose name and path are given.
	 * 
	 * @param folder
	 *            output directory
	 * @param network
	 *            the network to generate code for
	 * @return <code>true</code> if the network file was cached
	 */
	public boolean print(String folder, Network network) {

		String file = folder + File.separator + network.getSimpleName() + ".java";
		if (keepUnchangedFiles) {
			// if source file is older than target file, do not generate
			long sourceTimeStamp = network.getFile().getLocalTimeStamp();
			File targetFile = new File(file);
			if (sourceTimeStamp < targetFile.lastModified()) {
				return true;
			}
		}
		CharSequence sequence = new NetworkPrinter(network)
				.getNetworkFileContent(options);
		if (!printFile(sequence.toString(), file)) {
			throw new OrccRuntimeException("Unable to write file " + file);
		}
		return false;
	}

	public void printEclipseProjectFiles(String folder, Network network) {
		CharSequence sequence = "";

		String projFile = folder + File.separator + ".project";
		if( ! new File(projFile).exists() || ! keepUnchangedFiles) {
			sequence = new NetworkPrinter(network)
					.getProjectFileContent(options);
			printFile(sequence.toString(), projFile);
		}

		String cpFile = folder + File.separator + ".classpath";
		if( ! new File(cpFile).exists() || ! keepUnchangedFiles) {
			sequence = new NetworkPrinter(network)
					.getClasspathFileContent(options);
			printFile(sequence.toString(), cpFile);
		}
	}
}
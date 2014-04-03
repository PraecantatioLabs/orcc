/*
 * Copyright (c) 2009-2010, IETR/INSA of Rennes
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
package net.sf.orcc.frontend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.sf.orcc.OrccException;
import net.sf.orcc.cal.CalStandaloneSetup;
import net.sf.orcc.cal.cal.AstEntity;
import net.sf.orcc.cal.cal.Import;
import net.sf.orcc.cal.generator.CalGenerator;
import net.sf.orcc.df.util.XdfConstants;
import net.sf.orcc.util.DomUtil;
import net.sf.orcc.util.OrccLogger;
import net.sf.orcc.util.OrccUtil;
import net.sf.orcc.util.util.EcoreHelper;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;

/**
 * This class defines an RVC-CAL command line version of the frontend. It should
 * be used with the folowing command-line, when all plugins are loaded by
 * eclipse :
 * 
 * <pre>
 * eclipse -application net.sf.orcc.cal.cli -data &lt;workspacePath&gt; &lt;projectName&gt;
 * </pre>
 * 
 * @author Matthieu Wipliez
 * @author Antoine Lorence
 * 
 */
public class FrontendCli implements IApplication {

	private final String USAGE = "Usage : \n"
			+ "net.sf.orcc.cal.cli <project> [<network>]";

	private final ResourceSet resourceSet;
	private final IWorkspace workspace;
	private boolean isAutoBuildActivated;

	private IProject project;
	private IFile networkFile;

	final Injector injector;

	public FrontendCli() {

		injector = new CalStandaloneSetup()
				.createInjectorAndDoEMFRegistration();

		workspace = ResourcesPlugin.getWorkspace();
		isAutoBuildActivated = false;

		project = null;
		networkFile = null;

		// Get the resource set used by Frontend
		resourceSet = injector.getInstance(ResourceSet.class);
	}

	@Override
	public Object start(IApplicationContext context) {

		final String[] args = (String[]) context.getArguments().get(
				IApplicationContext.APPLICATION_ARGS);

		if (!parseCommandLine(args)) {
			// parseCommandLine already displayed an error message before
			// returning false
			return IApplication.EXIT_RELAUNCH;
		}

		// The IR generation process starts now
		try {
			// IMPORTANT : Disable auto-building, because it requires Xtext UI
			// plugins to be launched
			disableAutoBuild();

			// Get the projects to compile in the right order
			OrccLogger.traceln("Setup " + project.getName() + " as working project ");
			final Collection<IProject> orderedProjects = getOrderedProjects(project);

			// Check for missing output folders in project
			for (final IProject proj : orderedProjects) {
				final IFolder outDir = OrccUtil.getOutputFolder(proj);
				if (!outDir.exists()) {
					outDir.create(true, true, new NullProgressMonitor());
				}
			}

			final Multimap<IProject, Resource> resourcesMap = HashMultimap
					.create();
			if (networkFile == null) {
				for (final IProject project : orderedProjects) {
					final List<IFile> files = OrccUtil.getAllFiles(
							OrccUtil.CAL_SUFFIX,
							OrccUtil.getSourceFolders(project));

					for (final IFile file : files) {
						resourcesMap.put(project,
								EcoreHelper.getResource(resourceSet, file));
					}
				}
			} else {
				final Map<String, IFile> allFiles = new HashMap<String, IFile>();
				for (final IProject project : orderedProjects) {
					allFiles.putAll(getAllFiles(project));
				}

				storeReferencedActors(networkFile, allFiles, resourcesMap);
			}

			final CalGenerator calGenerator = (CalGenerator) injector
					.getInstance(IGenerator.class);
			final JavaIoFileSystemAccess fsa = injector
					.getInstance(JavaIoFileSystemAccess.class);

			for (final IProject project : orderedProjects) {

				OrccLogger.traceln("+-------------------");
				OrccLogger.traceln("| " + project.getName());
				OrccLogger.traceln("+-------------------");

				fsa.setOutputPath(OrccUtil.getOutputFolder(project)
						.getLocation().toString());
				calGenerator.beforeBuild(project, resourceSet);
				for (final Resource res : resourcesMap.get(project)) {
					calGenerator.doGenerate(res, fsa);
					OrccLogger.traceln("Build " + res.getURI().toString());
				}
				calGenerator.afterBuild();
			}

			// If needed, restore autoBuild state in eclipse config file
			restoreAutoBuild();

			workspace.getRoot().refreshLocal(IWorkspaceRoot.DEPTH_INFINITE,
					new NullProgressMonitor());
			workspace.save(true, new NullProgressMonitor());
			OrccLogger.traceln("Build ends");

		} catch (OrccException oe) {
			System.err.println(oe.getMessage());
		} catch (CoreException ce) {
			System.err.println(ce.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception eee) {
			eee.printStackTrace();
		} finally {
			try {
				restoreAutoBuild();
				return IApplication.EXIT_OK;
			} catch (CoreException e) {
				System.err.println(e.getMessage());
			}
		}

		return IApplication.EXIT_RESTART;
	}

	private void disableAutoBuild() throws CoreException {
		IWorkspaceDescription desc = workspace.getDescription();
		if (desc.isAutoBuilding()) {
			isAutoBuildActivated = true;
			desc.setAutoBuilding(false);
			workspace.setDescription(desc);
		}
	}

	private void restoreAutoBuild() throws CoreException {
		if (isAutoBuildActivated) {
			IWorkspaceDescription desc = workspace.getDescription();
			desc.setAutoBuilding(true);
			workspace.setDescription(desc);
		}
	}

	/**
	 * Parse the command line and initialize the project to work with. If a
	 * network qualified name is passed in cli arguments, initialize the
	 * networkFile class member.
	 * 
	 * @param args
	 * @return
	 */
	private boolean parseCommandLine(final String[] args) {

		if (args.length == 0) {
			OrccLogger.severeln("Unable to parse command line arguments");
			OrccLogger.traceln(USAGE);
			return false;
		}

		OrccLogger.traceln("Command line arguments are \""
				+ StringUtils.join(args, ' ') + "\"");

		final String projectName = args[0];
		project = workspace.getRoot().getProject(projectName);
		if (project == null) {
			OrccLogger.severeln("Unable to find the project " + projectName);
			OrccLogger.traceln(USAGE);
			return false;
		}

		if (args.length >= 2 && !args[1].isEmpty()) {
			networkFile = OrccUtil.getFile(project, args[1],
					OrccUtil.NETWORK_SUFFIX);
		}

		return true;
	}

	/**
	 * Return a Collection containing all projects required to build the given
	 * project. The collection is sorted in the correct build order: the given
	 * project will be the last in the resulting Collection.
	 * 
	 * @param project
	 * @return
	 * @throws JavaModelException
	 */
	private Collection<IProject> getOrderedProjects(final IProject project)
			throws JavaModelException {
		final Collection<IProject> projects = new LinkedHashSet<IProject>();

		final IJavaProject javaProject = JavaCore.create(project);
		if (javaProject == null) {
			OrccLogger.severeln("");
			return projects;
		}

		for (final String required : javaProject.getRequiredProjectNames()) {
			final IProject proj = OrccUtil.workspaceRoot().getProject(required);
			projects.addAll(getOrderedProjects(proj));
		}

		projects.add(project);

		return projects;
	}

	/**
	 * Get all actors, units and network files from container (IProject or
	 * IFolder) and all its subfolders. IFile instances are indexed by their
	 * qualified name.
	 * 
	 * @param container
	 *            instance of IProject or IFolder to search in
	 * @return a map of qualified names / IFile descriptors
	 * @throws OrccException
	 * @throws CoreException
	 */
	private Map<String, IFile> getAllFiles(final IContainer container)
			throws OrccException, CoreException {

		final Map<String, IFile> calFiles = new HashMap<String, IFile>();
		for (final IResource resource : container.members()) {

			if (resource.getType() == IResource.FOLDER) {
				calFiles.putAll(getAllFiles((IFolder) resource));

			} else if (resource.getType() == IResource.FILE) {

				final String suffix = resource.getFileExtension();
				if (OrccUtil.CAL_SUFFIX.equals(suffix)
						|| (OrccUtil.NETWORK_SUFFIX.equals(suffix))) {
					final IFile ifile = (IFile) resource;
					calFiles.put(OrccUtil.getQualifiedName(ifile), ifile);
				}
			}
		}

		return calFiles;
	}

	private void storeReferencedActors(final IFile netFile,
			final Map<String, IFile> workspaceMap,
			final Multimap<IProject, Resource> files)
			throws FileNotFoundException {

		final Document document = DomUtil.parseDocument(new FileInputStream(
				netFile.getLocation().toFile()));
		final Element root = document.getDocumentElement();

		final NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			final Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				// Only ELEMENT nodes in XDF file
				continue;
			}

			final Element tag = (Element) child;
			if (tag.getNodeName().equals(XdfConstants.INSTANCE_TAG)) {
				final NodeList instChildren = tag.getChildNodes();
				for (int j = 0; j < instChildren.getLength(); ++j) {
					final Node instChild = instChildren.item(j);
					if(instChild.getNodeType() != Node.ELEMENT_NODE) continue;

					final Element classElement = (Element) instChild;
					if (classElement.getNodeName().equals(XdfConstants.CLASS_TAG)) {
						final String qualifiedName = classElement
								.getAttribute(XdfConstants.NAME_ATTR);

						if (workspaceMap.containsKey(qualifiedName)) {
							final IFile file = workspaceMap.get(qualifiedName);
							if (file.getFileExtension().equals(
									OrccUtil.NETWORK_SUFFIX)) {
								storeReferencedActors(file, workspaceMap, files);
							} else {
								storeImportedResources(file, files);
								files.put(file.getProject(), EcoreHelper
										.getResource(resourceSet, file));
							}
						}
					}
				}
			}
		}
	}

	private void storeImportedResources(final IFile calFile,
			final Multimap<IProject, Resource> resultMap) {

		final AstEntity astEntity = EcoreHelper
				.getEObject(resourceSet, calFile);
		final EList<Import> imports = astEntity.getImports();
		for (final Import imp : imports) {

			final String namespace = imp.getImportedNamespace();
			final String qname = namespace.substring(0,
					namespace.lastIndexOf('.'));

			final IFile importedFile = OrccUtil.getFile(project, qname,
					OrccUtil.CAL_SUFFIX);

			// The imported file can import files itself
			storeImportedResources(importedFile, resultMap);

			resultMap.put(importedFile.getProject(),
					EcoreHelper.getResource(resourceSet, calFile));
		}
	}

	@Override
	public void stop() {
	}

}

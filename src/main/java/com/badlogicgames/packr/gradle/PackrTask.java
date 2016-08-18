/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogicgames.packr.gradle;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.badlogicgames.packr.PackrConfig;
import com.badlogicgames.packr.PackrConfig.Platform;

public class PackrTask extends DefaultTask {

	private final PackrConfig config;

	public PackrTask() {
		this.config = new PackrConfig();
		this.config.classpath = new ArrayList<String>();
		this.config.vmArgs = new ArrayList<String>();
		this.config.platform = Platform.thisPlatform();
		this.config.jdk = System.getProperty("java.home");
		this.config.executable = getProject().getName();
		this.config.resources = new ArrayList<File>();
		String suffix = "";
		if (config.platform == Platform.MacOS) {
			suffix = ".app";
		}
		this.config.outDir = getProject().file(format("%s/%s/%s/%s%s", getProject().getBuildDir(), PackrPlugin.NAME,
				config.platform, getProject().getName(), suffix));
	}

	@TaskAction
	public void run() throws Exception {
		// workaround mkdirs failing on windows
		getProject().mkdir(config.outDir);
		if (config.mainClass == null) {
			throw new IllegalArgumentException("must provide config.mainClass");
		}
		if (config.classpath == null || config.classpath.isEmpty()) {
			throw new IllegalArgumentException("must provide config.classpath");
		}
		PackrTaskMain.startNewVM(config);
	}

	public void resources(String resources) {
		config.resources.add(getProject().file(resources));
	}

	public void resources(List<String> resources) {
		ArrayList<File> files = new ArrayList<File>();
		for (String r : resources) {
			files.add(getProject().file(r));
		}
		config.resources = files;
	}

	public void vmArgs(String vmArgs) {
		config.vmArgs.add(vmArgs);
	}

	public void vmArgs(List<String> vmArgs) {
		config.vmArgs = vmArgs;
	}

	public void classpath(String classpath) {
		config.classpath.add(classpath);
	}

	public void classpath(List<String> classpath) {
		config.classpath = classpath;
	}

	public void mainClass(String mainClass) {
		config.mainClass = mainClass;
	}

	public void platform(Platform platform) {
		config.platform = platform;
	}

	public void jdk(String jdk) {
		config.jdk = jdk;
	}

	public void executable(String executable) {
		config.executable = executable;
	}

	public void minimizeJre(String minimizeJre) {
		config.minimizeJre = minimizeJre;
	}

	public void outDir(String outDir) {
		config.outDir = getProject().file(outDir);
	}

	public void iconResource(String iconResource) {
		config.iconResource = getProject().file(iconResource);
	}

	public void bundleIdentifier(String bundleIdentifier) {
		config.bundleIdentifier = bundleIdentifier;
	}

}
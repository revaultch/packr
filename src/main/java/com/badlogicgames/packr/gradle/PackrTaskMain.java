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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.badlogicgames.packr.Packr;
import com.badlogicgames.packr.PackrConfig;
import com.google.gson.Gson;

public class PackrTaskMain {
	/**
	 * Need to spawn a new process, since gradle comes with jna3 that already
	 * has a dll loaded
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(args[0]);
		new Packr().pack(readConfig(args[0]));
	}

	public static void startNewVM(PackrConfig config) throws Exception {

		String pathSeparator = File.pathSeparator;
		StringBuilder cp = new StringBuilder();
		for (URL u : urls()) {
			if (u.toExternalForm().matches(".*/packr[/-].*\\.jar$")) {
				cp.append(new File(u.toURI()).getAbsolutePath()).append(pathSeparator);
			}
		}
		File path = new File(new File(System.getProperty("java.home"), "bin"), "java");

		ProcessBuilder processBuilder = new ProcessBuilder(path.getAbsolutePath(), "-cp", cp.toString(),
				PackrTaskMain.class.getCanonicalName(), writeConfig(config));
		
		System.out.println(processBuilder.command());
		Process process = processBuilder.start();

		if (0 != process.waitFor()) {
			throw new IllegalStateException("couldn't run packr");
		}

	}

	private static String writeConfig(PackrConfig config) throws IOException {
		File tempFile = File.createTempFile("config", ".json");
		Writer writer = null;
		try {
			writer = new FileWriter(tempFile);
			new Gson().toJson(config, writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
		return tempFile.getAbsolutePath();
	}

	private static PackrConfig readConfig(String file) throws FileNotFoundException {
		Reader reader = null;
		try {
			reader = new FileReader(file);
			return new Gson().fromJson(reader, PackrConfig.class);
		} finally {
			IOUtils.closeQuietly(reader);
		}

	}

	private static URL[] urls() {
		List<URL> urls = new ArrayList<URL>();
		collectUrls(PackrTaskMain.class.getClassLoader(), urls);
		return urls.toArray(new URL[urls.size()]);
	}

	private static void collectUrls(ClassLoader cl, List<URL> urls) {
		if (cl == null) {
			return;
		}
		collectUrls(cl.getParent(), urls);
		if (!(cl instanceof URLClassLoader)) {
			return;
		}
		urls.addAll(Arrays.asList(((URLClassLoader) cl).getURLs()));
	}
}

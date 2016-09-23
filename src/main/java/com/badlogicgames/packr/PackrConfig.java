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

package com.badlogicgames.packr;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Packr configuration can be read from command line, read from a JSON config file, or
 * created from Java code directly.
 *
 * Command line parameters can be used to override (single-argument parameters) or extend
 * (multi-argument parameters) JSON settings.
 */
public class PackrConfig {

	public enum Platform {
		Windows32      ("windows32", "win32-x86"),
		Windows64      ("windows64", "win32-x86-64"),
		Linux32        ("linux32", "linux-x86"),
		Linux64        ("linux64", "linux-x86-64"),
		MacOS          ("mac", "darwin"),
		aix_ppc        ("aix-ppc", "aix-ppc"),
		freebsd_x86    ("freebsd-x86", "freebsd-x86"),
		freebsd_x86_64 ("freebsd-x86-64", "freebsd-x86-64"),
		linux_aarch64  ("linux-aarch64", "linux-aarch64"),
		linux_arm      ("linux-arm", "linux-arm"),
		linux_ppc64    ("linux-ppc64", "linux-ppc64"),
		linux_ppc64le  ("linux-ppc64le", "linux-ppc64le"),
		linux_sparcv9  ("linux-sparcv9", "linux-sparcv9"),
		linux_x86      ("linux-x86", "linux-x86"),
		linux_x86_64   ("linux-x86-64", "linux-x86-64"),
		openbsd_x86    ("openbsd-x86", "openbsd-x86"),
		openbsd_x86_64 ("openbsd-x86-64", "openbsd-x86-64"),
		sunos_sparc    ("sunos-sparc", "sunos-sparc"),
		sunos_sparcv9  ("sunos-sparcv9", "sunos-sparcv9"),
		sunos_x86      ("sunos-x86", "sunos-x86"),
		sunos_x86_64   ("sunos-x86-64", "sunos-x86-64"),
		w32ce_arm      ("w32ce-arm", "w32ce-arm");
		
		final String desc;
		final String jnaPath;

		Platform(String desc, String jnaPath) {
			this.desc = desc;
			this.jnaPath = jnaPath;
		}

		static Platform byDesc(String desc) throws IOException {
			for (Platform value : values()) {
				if (value.desc.equalsIgnoreCase(desc)) {
					return value;
				}
			}
			throw new IOException("Invalid platform '" + desc + "'");
		}

		public static Platform thisPlatform() {

			String os = System.getProperty("os.name");
			String arch = System.getProperty("os.arch");
			if (os.matches("(?i).*mac.*")) {
				return Platform.MacOS;
			}
			if (os.matches("(?i).*windows.*")) {
				if (arch.endsWith("64")) {
					return Platform.Windows64;
				}
				return Platform.Windows32;
			}

			if (arch.endsWith("64")) {
				return Platform.Linux64;
			}
			return Platform.Linux32;
		}
	}

	public Platform platform;
	public String jdk;
	public String executable;
	public List<String> classpath;
	public String mainClass;
	public List<String> vmArgs;
	public String minimizeJre;
	public List<File> resources;
	public File outDir;
	public File iconResource;
	public String bundleIdentifier;

	public boolean verbose;

	public PackrConfig() {

	}

	public PackrConfig(Platform platform, String jdk, String executable,
					   List<String> classpath, String mainClass, File outDir) throws IOException {

		this.platform = platform;
		this.jdk = jdk;
		this.executable = executable;
		this.classpath = classpath;
		this.mainClass = mainClass;
		this.outDir = outDir;
	}

	public PackrConfig(PackrCommandLine commandLine) throws IOException {

		verbose = commandLine.verbose();

		// parse config file, if one is given

		if (commandLine.isConfig()) {
			readConfigJson(commandLine.config());
		}

		// evaluate optional command line parameters
		// if given, they override the config file settings

		if (commandLine.platform() != null) {
			platform = Platform.byDesc(commandLine.platform());
		}

		if (commandLine.jdk() != null) {
			jdk = commandLine.jdk();
		}

		if (commandLine.executable() != null) {
			executable = commandLine.executable();
		}

		if (commandLine.classpath() != null) {
			classpath = appendTo(classpath, commandLine.classpath());
		}

		if (commandLine.mainClass() != null) {
			mainClass = commandLine.mainClass();
		}

		if (commandLine.vmArgs() != null) {
			vmArgs = appendTo(vmArgs, commandLine.vmArgs());
		}

		if (commandLine.minimizeJre() != null) {
			minimizeJre = commandLine.minimizeJre();
		}

		if (commandLine.resources() != null) {
			resources = appendTo(resources, commandLine.resources());
		}

		if (commandLine.outDir() != null) {
			outDir = commandLine.outDir();
		}

		if (commandLine.iconResource() != null) {
			iconResource = commandLine.iconResource();
		}

		if (commandLine.bundleIdentifier() != null) {
			bundleIdentifier = commandLine.bundleIdentifier();
		}
	}

	private void readConfigJson(File configJson) throws IOException {

		JsonObject json = JsonObject.readFrom(FileUtils.readFileToString(configJson));

		platform = Platform.byDesc(json.get("platform").asString());
		jdk = json.get("jdk").asString();
		executable = json.get("executable").asString();
		classpath = toStringArray(json.get("classpath").asArray());
		mainClass = json.get("mainclass").asString();
		if(json.get("vmargs") != null) {
			List<String> vmArgs = toStringArray(json.get("vmargs").asArray());
			this.vmArgs = new ArrayList<String>();
			for (String vmArg : vmArgs) {
				if (vmArg.startsWith("-")) {
					this.vmArgs.add(vmArg.substring(1));
				} else {
					this.vmArgs.add(vmArg);
				}
			}
		}
		if(json.get("minimizejre") != null) {
			minimizeJre = json.get("minimizejre").asString();
		}
		if(json.get("resources") != null) {
			resources = toFileArray(json.get("resources").asArray());
		}
		outDir = new File(json.get("output").asString());
		if(json.get("icon") != null) {
			iconResource = new File(json.get("icon").asString());
		}
		if(json.get("bundle") != null) {
			bundleIdentifier = json.get("bundle").asString();
		}
	}

	private <T> List<T> appendTo(List<T> list, List<T> append) {
		if (list == null) {
			return append;
		}

		for (T item : append) {
			boolean duplicate = false;
			for (T cmp : list) {
				if (cmp.equals(item)) {
					duplicate = true;
					break;
				}
			}
			if (!duplicate) {
				list.add(item);
			}
		}

		return list;
	}

	private List<String> toStringArray(JsonArray array) {
		List<String> result = new ArrayList<String>();
		for(JsonValue value: array) {
			result.add(value.asString());
		}
		return result;
	}

	private List<File> toFileArray(JsonArray array) {
		List<File> result = new ArrayList<File>();
		for(JsonValue value: array) {
			result.add(new File(value.asString()));
		}
		return result;
	}

	/**
	 * Sanity checks for configuration settings. Because users like to break stuff.
	 */
	void validate() throws IOException {
		if (outDir.exists()) {
			if (new File(".").equals(outDir)) {
				throw new IOException("Output directory equals working directory, aborting");
			}
			if(new File("/").equals(outDir)) {
				throw new IOException("Output directory equals root, aborting");
			}
		}
	}

}

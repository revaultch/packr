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

package com.badlogicgames.packr.windows;

import static com.badlogicgames.packr.windows.IcoResourceDir.ICON_GROUP;
import static com.badlogicgames.packr.windows.IcoResourceDirEntry.ICON;
import static com.badlogicgames.packr.windows.Kernel32.kernel32;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.badlogicgames.packr.windows.Kernel32.EnumResLangProc;
import com.badlogicgames.packr.windows.Kernel32.EnumResNameProc;
import com.badlogicgames.packr.windows.Kernel32.EnumResTypeProc;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class IconUtil {

	public static List<ResourceId> listResources(File exe, final Filter<ResourceId> filter) {
		final List<ResourceId> resources = new ArrayList<ResourceId>();
		HMODULE libHandle = null;
		try {
			libHandle = kernel32.LoadLibrary(exe.getAbsolutePath());
			if (libHandle == null) {
				throw new IllegalStateException("Cannot open file " + exe + " code " + kernel32.GetLastError());
			}

			final EnumResLangProc resourceLangCallback = new EnumResLangProc() {
				public boolean invoke(HMODULE module, Pointer type, Pointer name, short language, Pointer lParam) {
					ResourceId r = new ResourceId(type, name, language);
					if (filter == null || filter.test(r)) {
						resources.add(r);
					}
					return true;
				}
			};
			final EnumResNameProc resourceNameCallback = new EnumResNameProc() {
				public boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam) {
					return kernel32.EnumResourceLanguages(module, type, name, resourceLangCallback, null);
				}
			};
			EnumResTypeProc resourceTypeCallback = new EnumResTypeProc() {
				public boolean invoke(HMODULE module, Pointer type, Pointer lParam) {
					return kernel32.EnumResourceNames(module, type, resourceNameCallback, null);
				}
			};
			kernel32.EnumResourceTypes(libHandle, resourceTypeCallback, null);
			return resources;
		} finally {
			if (libHandle != null) {
				kernel32.FreeLibrary(libHandle);
			}
		}
	}

	public static void removeResources(File exe, Filter<ResourceId> filter) {
		List<ResourceId> resources = listResources(exe, filter);
		HANDLE handle = kernel32.BeginUpdateResource(exe.getAbsolutePath(), false);
		if (handle == null) {
			throw new IllegalStateException("BeginUpdateResource failed code " + kernel32.GetLastError());
		}
		for (ResourceId r : resources) {
			if (!kernel32.UpdateResource(handle, r.type, r.name, r.language, null, 0)) {
				throw new IllegalStateException("UpdateResource failed code " + kernel32.GetLastError());
			}
		}

		if (!kernel32.EndUpdateResource(handle, false)) {
			throw new IllegalStateException("EndUpdateResource failed code " + kernel32.GetLastError());
		}
	}

	public static void addResources(File exe, Resource... resources) {
		HANDLE handle = kernel32.BeginUpdateResource(exe.getAbsolutePath(), false);
		if (handle == null) {
			throw new IllegalStateException("BeginUpdateResource failed code " + kernel32.GetLastError());
		}
		for (Resource r : resources) {
			if (!kernel32.UpdateResource(handle, r.resourceId.type, r.resourceId.name, r.resourceId.language,
					r.buffer.data, r.buffer.length)) {
				throw new IllegalStateException("UpdateResource failed code " + kernel32.GetLastError());
			}
		}

		if (!kernel32.EndUpdateResource(handle, false)) {
			throw new IllegalStateException("EndUpdateResource failed code " + kernel32.GetLastError());
		}
	}

	public static IcoDir readIcon(File icons) {
		byte[] buf;
		try {
			buf = FileUtils.readFileToByteArray(icons);
		} catch (IOException e) {
			throw new IllegalArgumentException("icons file " + icons + " cannot be read", e);
		}
		Memory m = new Memory(buf.length);
		m.getByteBuffer(0, buf.length).put(buf);
		IcoDir d = new IcoDir(m);
		return d;
	}

	public static void replaceAllIcons(File exe, File icoFile) {
		removeResources(exe, new TypeFilter(ICON_GROUP, ICON));
		IcoDir dir = readIcon(icoFile);
		Buffer[] bitmaps = dir.icons();
		Resource[] resources = new Resource[dir.entries.length + 1];

		IcoResourceDir icoGroup = new IcoResourceDir(dir);

		resources[0] = icoGroup.toResource(0, 0);
		for (int i = 1; i < resources.length; i++) {
			resources[i] = icoGroup.entries[i - 1].toResource(0, bitmaps[i - 1]);

		}
		addResources(exe, resources);

	}
}

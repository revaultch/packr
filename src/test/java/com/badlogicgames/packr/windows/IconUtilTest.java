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
import static com.badlogicgames.packr.windows.IconUtil.listResources;
import static com.badlogicgames.packr.windows.IconUtil.replaceAllIcons;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.Test;

public class IconUtilTest {

	@Test
	public void test() throws IOException {
		if (!System.getProperty("os.name").matches("(?i).*windows.*")) {
			System.out.println("skiping test, not windows");
			return;
		}
		File exe = copyResourceToTemp("/packr-windows-x64.exe");
		File icons = copyResourceToTemp("/icons.ico");
		System.out.println(exe.getAbsolutePath());

		TypeFilter typeFilter = new TypeFilter(ICON_GROUP, ICON);

		replaceAllIcons(exe, icons);
		List<ResourceId> afterAddingIcons = listResources(exe, typeFilter);

		// icongroup + 6 icons
		assertEquals(7, afterAddingIcons.size());

	}

	private File copyResourceToTemp(String resourceName) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {

			File exe = File.createTempFile("copy_", resourceName.replaceAll(".*/", "_"));
			in = getClass().getResourceAsStream(resourceName);
			out = new FileOutputStream(exe);
			copy(in, out);
			return exe;
		} finally {
			closeQuietly(in);
			closeQuietly(out);

		}

	}

	@Test
	public void testIco() {
		IcoDir d = new IcoDir();
		d.entries = new IcoDirEntry[] { new IcoDirEntry() };
		assertEquals(22, d.size());

	}

	@Test
	public void testIcon() throws IOException {
		File icoFile = copyResourceToTemp("/icons.ico");
		System.out.println(icoFile);
		assertEquals(213987, icoFile.length());
		IcoDir d = IconUtil.readIcon(icoFile);
		assertEquals(6, d.header.idCount);
		Buffer[] bitmaps = d.icons();
		assertEquals(6, bitmaps.length);

		IcoResourceDir group = new IcoResourceDir(d);

		assertEquals(6, group.header.idCount);
		assertEquals(6, group.entries.length);

		assertEquals(90, group.size());
	}
}

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

import static com.badlogicgames.packr.windows.Kernel32Util.MAKEINTRESOURCE;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class IcoResourceDir extends Structure {
	public static final Pointer ICON_GROUP = Pointer.createConstant(14);
	public IcoDirHeader header;
	public IcoResourceDirEntry[] entries;

	public IcoResourceDir(IcoDir dir) {
		setAlignType(ALIGN_NONE);
		this.header = new IcoDirHeader();
		this.header.idReserved = dir.header.idReserved;
		this.header.idType = dir.header.idType;
		this.header.idCount = dir.header.idCount;
		this.entries = new IcoResourceDirEntry[header.idCount];
		for (short i = 0; i < header.idCount; i++) {

			entries[i] = new IcoResourceDirEntry();
			entries[i].bWidth = dir.entries[i].bWidth;
			entries[i].bHeight = dir.entries[i].bHeight;
			entries[i].bColorCount = dir.entries[i].bColorCount;
			entries[i].bReserved = dir.entries[i].bReserved;
			entries[i].wPlanes = dir.entries[i].wPlanes;
			entries[i].wBitCount = dir.entries[i].wBitCount;
			entries[i].dwbytesInRes = dir.entries[i].dwbytesInRes;
			entries[i].nID = (short) (i + 1);
		}
		autoWrite();

	}

	public Resource toResource(int name, int lang) {
		return new Resource(new ResourceId(ICON_GROUP, MAKEINTRESOURCE(name), (short) lang),
				new Buffer(getPointer(), size()));
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("header", "entries");
	}
}
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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class IcoDir extends Structure {
	public IcoDirHeader header;
	public IcoDirEntry[] entries = new IcoDirEntry[1];

	public IcoDir() {
		setAlignType(ALIGN_NONE);
	}

	public IcoDir(IcoDirHeader header) {
		this();
		useMemory(header.getPointer());
		this.header = header;
		entries = new IcoDirEntry[header.idCount];
		read();
	}

	public IcoDir(Pointer p) {
		this(new IcoDirHeader(p));
	}

	public Buffer[] icons() {
		Buffer[] r = new Buffer[entries.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = new Buffer(getPointer().share(entries[i].dwImageOffset), entries[i].dwbytesInRes);
		}
		return r;

	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("header", "entries");
	}
}
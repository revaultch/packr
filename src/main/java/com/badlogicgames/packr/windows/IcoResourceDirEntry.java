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

public class IcoResourceDirEntry extends Structure {
	public static final Pointer ICON = Pointer.createConstant(3);

	{
		setAlignType(ALIGN_NONE);
	}
	public byte bWidth;
	public byte bHeight;
	public byte bColorCount;
	public byte bReserved;
	public short wPlanes;
	public short wBitCount;
	public int dwbytesInRes;
	public short nID;

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("bWidth", "bHeight", "bColorCount", "bReserved", "wPlanes", "wBitCount", "dwbytesInRes",
				"nID");
	}

	public Resource toResource(int i, Buffer buffer) {
		return new Resource(new ResourceId(ICON, MAKEINTRESOURCE(nID), (short) 0), buffer);
	}

}
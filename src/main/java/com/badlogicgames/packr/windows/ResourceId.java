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

import static com.badlogicgames.packr.windows.Kernel32Util.LPCTSTR_VALUE;

import com.sun.jna.Pointer;

public class ResourceId {
	public final Pointer type;
	public final Pointer name;
	public final Short language;

	public ResourceId(Pointer type, Pointer name, short language) {
		this.type = type;
		this.name = name;
		this.language = language;
	}

	@Override
	public String toString() {
		return String.format("ResourceId(type=%s, name=%s, lange=%d)", LPCTSTR_VALUE(type), LPCTSTR_VALUE(name),
				language);
	}
}

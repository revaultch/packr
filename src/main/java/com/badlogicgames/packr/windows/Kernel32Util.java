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

import com.sun.jna.Pointer;

public class Kernel32Util {
	public static boolean IS_INTRESOURCE(Pointer p) {
		return Pointer.nativeValue(p) <= 65535;
	}

	public static Object LPCTSTR_VALUE(Pointer p) {
		if (IS_INTRESOURCE(p)) {
			return Pointer.nativeValue(p);
		}
		return p.getString(0);
	}

	public static Pointer MAKEINTRESOURCE(int i) {
		return Pointer.createConstant(i);
	}
}
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

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
	static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);

	HANDLE BeginUpdateResource(String fileName, boolean deleteExistingResources);

	boolean UpdateResource(HANDLE handle, Pointer type, Pointer name, short language, Pointer buffer, int length);

	boolean EndUpdateResource(HANDLE handle, boolean discard);

	HMODULE LoadLibrary(String paramString);

	void FreeLibrary(HMODULE handle);

	boolean EnumResourceTypes(HMODULE hModule, Kernel32.EnumResTypeProc proc, Pointer lParam);

	boolean EnumResourceNames(HMODULE hModule, Pointer type, Kernel32.EnumResNameProc proc, Pointer lParam);

	boolean EnumResourceLanguages(HMODULE hModule, Pointer type, Pointer name, Kernel32.EnumResLangProc proc,
			Pointer lParam);

	interface EnumResTypeProc extends Callback {
		boolean invoke(HMODULE module, Pointer type, Pointer lParam);
	}

	interface EnumResNameProc extends Callback {
		boolean invoke(HMODULE module, Pointer type, Pointer name, Pointer lParam);
	}

	public interface EnumResLangProc extends Callback {
		boolean invoke(HMODULE module, Pointer type, Pointer name, short language, Pointer lParam);
	}
}
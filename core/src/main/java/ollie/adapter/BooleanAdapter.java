/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie.adapter;

import ollie.TypeAdapter;

/**
 * <p>
 * Converts Java {@link java.lang.Boolean} values to SQLite INTEGER values.
 * </p>
 */
public class BooleanAdapter extends TypeAdapter<Boolean, Integer> {
	/**
	 * {@inheritDoc}
	 *
	 * @param value {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public Integer serialize(Boolean value) {
		return value ? 1 : 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param value {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public Boolean deserialize(Integer value) {
		return value != 0;
	}
}
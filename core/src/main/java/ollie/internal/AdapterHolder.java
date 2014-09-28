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

package ollie.internal;

import ollie.Migration;
import ollie.Model;
import ollie.TypeAdapter;

import java.util.List;


public interface AdapterHolder {
	public static final String IMPL_CLASS_PACKAGE = "ollie";
	public static final String IMPL_CLASS_NAME = "AdapterHolderImpl";
	public static final String IMPL_CLASS_FQCN = IMPL_CLASS_PACKAGE + "." + IMPL_CLASS_NAME;

	public List<? extends Migration> getMigrations();

	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls);

	public List<? extends ModelAdapter> getModelAdapters();

	public <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls);
}
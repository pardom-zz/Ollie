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

package ollie.test.model.migration;

import ollie.Migration;

public class AddDateColumnMigration extends Migration {
	@Override
	public int getVersion() {
		return 2;
	}

	@Override
	public String[] getStatements() {
		return new String[]{
				"ALTER TABLE notes ADD COLUMN date INTEGER"
		};
	}
}

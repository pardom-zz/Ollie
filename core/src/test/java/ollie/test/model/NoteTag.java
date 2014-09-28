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

package ollie.test.model;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.ForeignKey;
import ollie.annotation.Table;

import static ollie.annotation.ForeignKey.ReferentialAction.CASCADE;

@Table("noteTags")
public class NoteTag extends Model {
	public static final String Note = "note";
	public static final String Tag = "tag";

	@Column(Note)
	@ForeignKey(onDelete = CASCADE)
	public Note note;
	@Column(Tag)
	@ForeignKey(onDelete = CASCADE)
	public Tag tag;
}
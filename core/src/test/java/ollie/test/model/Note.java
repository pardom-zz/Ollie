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
import ollie.annotation.NotNull;
import ollie.annotation.Table;

import java.util.Date;

@Table("notes")
public class Note extends Model {
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String DATE = "date";

	@Column(TITLE)
	public String title;
	@Column(BODY)
	@NotNull
	public String body;
	@Column(DATE)
	public Date date;
}
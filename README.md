Ollie
=====

[![Build Status](https://travis-ci.org/pardom/ollie.svg?branch=master)](https://travis-ci.org/pardom/ollie)
[![Stories in Ready](https://badge.waffle.io/pardom/ollie.png)](http://waffle.io/pardom/ollie)  

Compile-time active record ORM for Android, with RxJava support.

```java
@Table("notes")
class Note extends Model {
	@Column("title")
	public String title;
	@Column("body")
	public String body;
}
```

```java
Note note = new Note();
note.title = "My note";
note.body = "This is my note."
note.save();
```

```java
Select.from(Note.class).fetch();
```

Download
--------

Grab via Maven:
```xml
<dependency>
  <groupId>com.michaelpardo</groupId>
  <artifactId>ollie</artifactId>
  <version>0.3.0</version>
</dependency>
<dependency>
  <groupId>com.michaelpardo</groupId>
  <artifactId>ollie-compiler</artifactId>
  <version>0.3.0</version>
  <optional>true</optional>
</dependency>
```
or Gradle:
```groovy
compile 'com.michaelpardo:ollie:0.3.0'
provided 'com.michaelpardo:ollie-compiler:0.3.0'
```

Build
-----

To build:

```
$ git clone git@github.com:pardom/ollie.git
$ cd ollie/
$ ./gradlew build
```

License
=======

    Copyright 2014 Michael Pardo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

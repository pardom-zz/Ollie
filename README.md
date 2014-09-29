Ollie
=====

Compile-time active record ORM for Android.

Ollie is in active development and is changing quite rapidly. Please look at the tests until proper documentation is written.

[![Build Status](https://travis-ci.org/pardom/ollie.svg?branch=master)](https://travis-ci.org/pardom/ollie)
[![Stories in Ready](https://badge.waffle.io/pardom/ollie.png)](http://waffle.io/pardom/ollie)  

Download
--------

Grab via Maven:

```xml
<dependency>
  <groupId>com.michaelpardo</groupId>
  <artifactId>ollie-compiler</artifactId>
  <version>0.2.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>com.michaelpardo</groupId>
  <artifactId>ollie</artifactId>
  <version>0.2.0-SNAPSHOT</version>
</dependency>
```

or Gradle:

```groovy
repositories {
    mavenCentral()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}

compile 'com.michaelpardo:ollie-compiler:0.2.0-SNAPSHOT'
compile 'com.michaelpardo:ollie:0.2.0-SNAPSHOT'
```

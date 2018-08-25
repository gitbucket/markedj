# markedj [![Build Status](https://travis-ci.org/gitbucket/markedj.svg?branch=master)](https://travis-ci.org/gitbucket/markedj) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.gitbucket/markedj/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.gitbucket/markedj) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/markedj/blob/master/LICENSE)

JVM port of graceful markdown processor [marked.js](https://github.com/chjj/marked).

## Usage

First, add following dependency into your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.gitbucket</groupId>
    <artifactId>markedj</artifactId>
    <version>1.0.15</version>
  </dependency>
</dependencies>
```

You can easily use markedj via `io.github.gitbucket.markedj.Marked`:

```java
import io.github.gitbucket.markedj.*;

String markdown = ...

// With default options
String html1 = Marked.marked(markdown);

// Specify options
Options options = new Options();
options.setSanitize(true);

String html2 = Marked.marked(markdown, options);
```

## Options

`io.github.gitbucket.markedj.Options` has following properties to control Markdown conversion:

Name         | Default | Description
:------------|:--------|:------------
gfm          | true    | Enable [GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown).
tables       | true    | Enable GFM [tables](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet#wiki-tables). This option requires the `gfm` option to be true.
breaks       | false   | Enable GFM [line breaks](https://help.github.com/articles/github-flavored-markdown#newlines). This option requires the `gfm` option to be true.
sanitize     | false   | Ignore any HTML that has been input.
langPrefix   | "lang-" | Prefix of class attribute of code block
headerPrefix | ""      | Prefix of id attribute of header
whitelist    | See [Options.java](https://github.com/gitbucket/markedj/blob/master/src/main/java/io/github/gitbucket/markedj/Options.java) | Whitelist of HTML tags.

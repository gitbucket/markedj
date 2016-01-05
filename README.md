# markedj [![Build Status](https://travis-ci.org/gitbucket/markedj.svg?branch=master)](https://travis-ci.org/gitbucket/markedj)

JVM port of graceful markdown processor [marked.js](https://github.com/chjj/marked).

## Usage

At first, add following dependency into your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>amateras</id>
    <name>Project Amateras Maven2 Repository</name>
    <url>http://amateras.sourceforge.jp/mvn/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>io.github.gitbucket</groupId>
    <artifactId>markedj</artifactId>
    <version>1.0.5</version>
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
Options options = new  Options();
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
pedantic     | false   | Conform to obscure parts of `markdown.pl` as much as possible.
sanitize     | false   | Ignore any HTML that has been input.
langPrefix   | "lang-" | Prefix of class attribute of code block
headerPrefix | ""      | Prefix of id attribute of header
xhtml        | false   | Generate XHTML rather than HTML


# markedj

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
    <version>1.0.4</version>
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

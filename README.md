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
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

You can easily use markedj in your program using a static method of `io.github.gitbucket.markedj.Marked`.

```java
import io.github.gitbucket.markedj.Marked;
import io.github.gitbucket.markedj.Options;

String markdown = ...
String html = Marked.marked(md, new Options());
```
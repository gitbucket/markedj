# markedj

JVM port of graceful markdown processor [marked.js](https://github.com/chjj/marked).

```java
import io.github.gitbucket.markedj.Marked;
import io.github.gitbucket.markedj.Options;

String markdown = ...
String html = Marked.marked(md, new Options());
```
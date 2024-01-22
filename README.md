# markedj [![build](https://github.com/gitbucket/markedj/workflows/build/badge.svg?branch=master)](https://github.com/gitbucket/markedj/actions?query=branch%3Amaster+workflow%3Abuild) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.gitbucket/markedj/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.gitbucket/markedj) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gitbucket/markedj/blob/master/LICENSE)

JVM port of graceful markdown processor [marked.js](https://github.com/chjj/marked).

## Usage

First, add following dependency into your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.gitbucket</groupId>
    <artifactId>markedj</artifactId>
    <version>1.0.19</version>
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
safelist     | See [Options.java](https://github.com/gitbucket/markedj/blob/master/src/main/java/io/github/gitbucket/markedj/Options.java) | Safelist of HTML tags.

By default, markedj uses Jsoup's safelist mechanism for HTML rendering. It restricts renderable tags, attributes and even protocols of attribute values. For example, the image url must be `http://` or `https://` by default. You can remove this restriction by customizing the safelist as follows:

```java
String html1 = Marked.marked("![alt text](/img/some-image.png \"title\")");
  // => <p><img alt=\"alt text\" title=\"title\"></p>

Options options = new Options();
options.getSafelist().removeProtocols("img", "src", "http", "https");

String html2 = Marked.marked("![alt text](/img/some-image.png \"title\")", options);
  // => <p><img src="/img/some-image.png" alt="alt text" title="title"></p>
```

## Extensions

Markedj can be extended by implementing [custom extensions](https://github.com/gitbucket/markedj/blob/master/src/main/java/io/github/gitbucket/markedj/extension/Extension.java).
Extensions can be used by adding them to the options.

```java
Options options = new Options();
options.addExtension(new GFMAlertExtension());
String html = Marked.marked("> [!NOTE]\n> This is a note!", options);
```

### GFMAlert extension

Support for github like [alerts](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax#alerts).

For styling, some project-specific CSS is required. SVG icons are embedded but can be replaced by configuration.

#### Usage

```java
Options options = new Options();
GFMAlertExtension gfmAlerts = new GFMAlertExtension();
// Override default title for note alert
gfmAlerts.addTitle(GFMAlerts.Alert.NOTE, "Notice");
options.addExtension(gfmAlerts);
String html = Marked.marked("> [!NOTE]\n> This is a note!", options);
```

#### Supported alert types

**Note**
```markdown
> [!NOTE]
> Useful information that users should know, even when skimming content.
```
**Note HTML**
```html
<div class="markdown-alert markdown-alert-note">
	<p class="markdown-alert-title">Note</p>
	<p>Useful information that users should know, even when skimming content.</p>
</div>
```

**Tip**
```markdown
> [!TIP]
> Helpful advice for doing things better or more easily.
```
**Note HTML**
```html
<div class="markdown-alert markdown-alert-tip">
	<p class="markdown-alert-title">Tip</p>
	<p>Helpful advice for doing things better or more easily.</p>
</div>
```

**Important**
```markdown
> [!IMPORTANT]
> Key information users need to know to achieve their goal.
```
**Important HTML**
```html
<div class="markdown-alert markdown-alert-important">
	<p class="markdown-alert-title">Important</p>
	<p>Key information users need to know to achieve their goal.</p>
</div>
```

**Warning**
```markdown
> [!WARNING]
> Urgent info that needs immediate user attention to avoid problems.
```
**Warning HTML**
```html
<div class="markdown-alert markdown-alert-warning">
	<p class="markdown-alert-title">Warning</p>
	<p>Urgent info that needs immediate user attention to avoid problems.</p>
</div>
```

**Caution**
```markdown
> [!CAUTION]
> Advises about risks or negative outcomes of certain actions.
```
**Caution HTML**
```html
<div class="markdown-alert markdown-alert-caution">
	<p class="markdown-alert-title">Caution</p>
	<p>Advises about risks or negative outcomes of certain actions.</p>
</div>
```

## for Developers

### Release

Run the following command to upload artifacts to sonatype:

```
mvn clean deploy -DperformRelease=true
```

Then, go to https://oss.sonatype.org/, close and release the staging repository. 


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
options.addExtension(new NotificationExtension());
String html = Marked.marked("! This is an info message", options);
  // => <div class="notification_info"><p>This is an info message</p></div>
```

### Notification extension

The notification extension helps you to add information messages to your markdown content.
Keep in mind, you still need the CSS to style the messages as desired.

#### Info message
```text
! This is an info message
```
```html
<div class="notification_info">
  <p>This is an info message</p>
</div>
```

#### Success message
```text
!v This is a success message
```
```html
<div class="notification_success">
  <p>This is a success message</p>
</div>
```

#### Warning message
```text
!! This is a warning message
```
```html
<div class="notification_warning">
  <p>This is a warning message</p>
</div>
```

#### Error message
```text
!x  This is an error message
```
```html
<div class="notification_error">
  <p>This is an error message</p>
</div>
```

#### Multiline notifications
Notifications can span multiple lines.

```text
! This is an info message
! That spans over several lines
```
```html
<div class="notification_info">
<p>This is an info message
That spans over several lines</p>
</div>
```

## for Developers

### Release

Run the following command to upload artifacts to sonatype:

```
mvn clean deploy -DperformRelease=true
```

Then, go to https://oss.sonatype.org/, close and release the staging repository. 


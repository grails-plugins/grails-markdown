package grails.plugins.markdown.example

class BootStrap {

    def init = { servletContext ->
        BlogPost.withTransaction {
            // Creates sample blog posts with markdown content
            if (BlogPost.count() == 0) {
                new BlogPost(
                        title: "Getting Started with Markdown",
                        author: "Rahul Shishodia",
                        markdownContent: '''# Welcome to Markdown Blog!

This is a sample blog post written in **Markdown**. Markdown is a lightweight markup language that you can use to add formatting elements to plaintext text documents.

## Why Use Markdown?

1. **Easy to learn** - Simple syntax that's readable
2. **Portable** - Plain text files work everywhere
3. **Platform independent** - Works on any device
4. **Future proof** - Always readable, even in plain text

## Code Examples

Here's a simple Groovy code example:

```groovy
def greet(name) {
    println "Hello, ${name}!"
}
greet("World")
```

## Links and Images

Check out the [Markdown Guide](https://www.markdownguide.org/) for more information.

---

*Happy blogging!*
''',
                        tags: "markdown, tutorial, getting-started"
                ).save(flush: true, failOnError: true)

                new BlogPost(
                        title: "Advanced Markdown Features",
                        author: "Rahul Shishodia",
                        markdownContent: '''# Advanced Markdown Features

Let's explore some advanced features available in this markdown editor.

## Tables

| Feature | Description | Supported |
|:--------|:------------|:---------:|
| Tables | Grid layouts | ✓ |
| Code Blocks | Syntax highlighting | ✓ |
| Links | Hyperlinks | ✓ |
| Images | Embedded pictures | ✓ |

## Definition Lists

Markdown
:   A lightweight markup language with plain text formatting syntax.

Grails
:   A powerful Groovy-based web application framework for the JVM.

## Abbreviations

This blog uses HTML and CSS for styling.

*[HTML]: Hyper Text Markup Language
*[CSS]: Cascading Style Sheets

## Task Lists

- [x] Create blog application
- [x] Add markdown support
- [ ] Add comment system
- [ ] Add user authentication

## Blockquotes

> "The best way to predict the future is to implement it."
> 
> — David Heinemeier Hansson

## Emphasis and Strong Text

Use *italics* for emphasis and **bold** for strong emphasis.

You can also use ~~strikethrough~~ text.
''',
                        tags: "markdown, advanced, features"
                ).save(flush: true, failOnError: true)

                new BlogPost(
                        title: "Building a Documentation Site",
                        author: "Rahul Shishodia",
                        markdownContent: '''# Building Documentation with Markdown

Markdown is perfect for creating technical documentation. Here's why:

## Advantages

### 1. Version Control Friendly

Since markdown files are plain text, they work great with Git:

```bash
git add docs/*.md
git commit -m "Update documentation"
git push origin main
```

### 2. Easy Collaboration

Multiple team members can edit documentation without conflicts.

### 3. Multiple Output Formats

Convert markdown to:
- HTML for web viewing
- PDF for distribution
- DOCX for editing in Word

## Best Practices

1. **Use clear headings** - Organize content hierarchically
2. **Add code examples** - Show, don't just tell
3. **Include links** - Reference related documentation
4. **Keep it updated** - Documentation should evolve with code

## Example API Documentation

### `POST /api/posts`

Create a new blog post.

**Request Body:**
```json
{
  "title": "Post Title",
  "content": "Post content in markdown",
  "author": "Author Name",
  "tags": "tag1, tag2"
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Post Title",
  "createdDate": "2024-02-13T10:30:00Z"
}
```

---

Happy documenting! 📚
''',
                        tags: "documentation, markdown, best-practices"
                ).save(flush: true, failOnError: true)
            }
        }
    }

    def destroy = {
    }
}

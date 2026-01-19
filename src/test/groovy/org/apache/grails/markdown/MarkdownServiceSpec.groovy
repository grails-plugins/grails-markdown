package org.apache.grails.markdown

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class MarkdownServiceSpec extends Specification implements ServiceUnitTest<MarkdownService> {

    def setup() {
        service.grailsApplication.config.clear()
        service.grailsApplication.config.markdown = [:]
        service.grailsApplication.config.grails.serverURL = 'http://localhost:8080'
        service.afterPropertiesSet()
    }

    void 'markdown to HTML - basic formatting'() {
        expect:
        '<p><a href="http://example.net/">This link</a> has no title attribute.</p>\n' ==
                service.markdown('[This link](http://example.net/) has no title attribute.')
    }

    void 'markdown to HTML - headings'() {
        expect:
        service.markdown('# Heading 1') == '<h1>Heading 1</h1>\n'
        service.markdown('## Heading 2') == '<h2>Heading 2</h2>\n'
        service.markdown('### Heading 3') == '<h3>Heading 3</h3>\n'
    }

    void 'markdown to HTML - emphasis'() {
        expect:
        service.markdown('**bold**') == '<p><strong>bold</strong></p>\n'
        service.markdown('*italic*') == '<p><em>italic</em></p>\n'
        service.markdown('***bold italic***') == '<p><strong><em>bold italic</em></strong></p>\n'
    }

    void 'markdown to HTML - lists'() {
        when:
        String markdown = '''
- Item 1
- Item 2
- Item 3
'''
        String result = service.markdown(markdown)

        then:
        result.contains('<ul>\n')
        result.contains('<li>Item 1</li>\n')
        result.contains('<li>Item 2</li>\n')
        result.contains('<li>Item 3</li>\n')
        result.contains('</ul>\n')
    }

    void 'markdown to HTML - code blocks'() {
        when:
        String markdown = '`inline code`'

        then:
        service.markdown(markdown) == '<p><code>inline code</code></p>\n'
    }

    void 'markdown to HTML - empty or null input'() {
        expect:
        service.markdown(null) == ''
        service.markdown('') == ''
        service.markdown('   ') == ''
    }

    void 'HTML to markdown - basic conversion'() {
        expect:
        service.htmlToMarkdown('<p><a href="http://example.net/">This link</a> has no title attribute.</p>') ==
                '[This link][] has no title attribute.\n\n[This link]: http://example.net/\n\n'
    }

    void 'HTML to markdown - code conversion'() {
        when:
        String htmlInput = "<pre><code class=\"language-java\">" +
                "System.out.println(\"Hello World\");" +
                "</code></pre>"

        String expectedMd = "~~~java\n" +
                "System.out.println(\"Hello World\");\n" +
                "~~~\n\n"
        then:
        service.htmlToMarkdown(htmlInput, '', [fencedCodeBlocks: true]) == expectedMd

    }

    void 'HTML to markdown - headings'() {
        expect:
        service.htmlToMarkdown('<h1>Heading 1</h1>\n') == 'Heading 1\n=========\n\n'
        service.htmlToMarkdown('<h2>Heading 2</h2>\n') == 'Heading 2\n---------\n\n'
        service.htmlToMarkdown('<h3>Heading 3</h3>\n') == '### Heading 3\n\n'
    }

    void 'HTML to markdown - in-word emphasis'() {
        expect:
        service.htmlToMarkdown('<em>emphasis</em>') == '*emphasis*\n'
        service.htmlToMarkdown('<i>italic</i>') == '*italic*\n'
        service.htmlToMarkdown('<strong>bold</strong>') == '**bold**\n'
        service.htmlToMarkdown('<b>bold</b>') == '**bold**\n'
    }

    void 'HTML to markdown - lists'() {
        when:
        String html = '<ul><li>Item 1</li><li>Item 2</li></ul>\n'
        String result = service.htmlToMarkdown(html)

        then:
        result.contains('* Item 1')
        result.contains('* Item 2')
    }

    void 'HTML to markdown - empty or null input'() {
        expect:
        service.htmlToMarkdown(null) == ''
        service.htmlToMarkdown('') == ''
        service.htmlToMarkdown('   ') == ''
    }

    void 'HTML to markdown - with custom base URI'() {
        when:
        String html = '<a href="/path/to/page">Link</a>'
        String result = service.htmlToMarkdown(html, 'http://example.com/')

        then:
        result.contains('http://example.com/path/to/page')
    }

    void 'HTML to markdown - base URI from config'() {
        given:
        service.grailsApplication.config.markdown = [baseUri: 'http://configured.com/']
        service.afterPropertiesSet()
        String html = '<a href="/path">Link</a>'

        when:
        String result = service.htmlToMarkdown(html)

        then:
        result == '[Link][]\n\n[Link]: http://configured.com/path\n\n'
    }

    void 'custom config - all enabled'() {
        when:
        String expected = '<p>Auto-linking Enabled: <a href="http://example.net/">http://example.net/</a></p>\n'
        String text = 'Auto-linking Enabled: http://example.net/'

        then:
        expected == service.markdown(text, [all: true])
        expected != service.markdown(text)
    }

    void 'custom config - autoLinks'() {
        when:
        String text = 'http://example.com'

        then:
        service.markdown(text, [autoLinks: true]).contains('<a href="http://example.com">')
        !service.markdown(text).contains('<a href="http://example.com">')
    }

    void 'custom config - tables'() {
        when:
        String markdown = '''
| Header 1 | Header 2 |
|----------|----------|
| Cell 1   | Cell 2   |
'''
        String withTables = service.markdown(markdown, [tables: true])
        String withoutTables = service.markdown(markdown)

        then:
        withTables.contains('<table>')
        withTables.contains('<th> Header 1 </th>')
        !withoutTables.contains('<table>')
    }

    void 'custom config - fenced code blocks'() {
        when:
        String markdown = '''
```java
public class Test {
}
```
'''
        String result = service.markdown(markdown, [fencedCodeBlocks: true])

        then:
        result.contains('<pre><code class="language-java">public class Test {\n}\n</code></pre>')
    }

    void 'custom config - smart quotes'() {
        when:
        String expected = '"Smart-Quotes" --- and em-dashes\n'
        String text = '&ldquo;Smart-Quotes&rdquo; &mdash; and em-dashes'

        then:
        expected == service.htmlToMarkdown(text, "", [all: true])
        expected != service.htmlToMarkdown(text)
    }

    void 'custom config - smartQuotes option'() {
        when:
        String html = '&ldquo;quotes&rdquo;'

        then:
        service.htmlToMarkdown(html, '', [smartQuotes: true]).contains('"')
    }

    void 'custom config - smart option (enables both quotes and punctuation)'() {
        when:
        String html = '&ldquo;quotes&rdquo; &mdash;'

        then:
        service.htmlToMarkdown(html, '', [smart: true]).contains('"')
        service.htmlToMarkdown(html, '', [smart: true]).contains('---')
    }

    void 'custom config - smartPunctuation'() {
        when:
        String html = '&mdash;'

        then:
        service.htmlToMarkdown(html, '', [smartPunctuation: true]).contains('---')
    }

    void 'custom config - hardwraps'() {
        when:
        String markdown = 'Line 1\nLine 2'
        String withHardwraps = service.markdown(markdown, [hardwraps: true])
        String withoutHardwraps = service.markdown(markdown)

        then:
        withHardwraps.contains('<br')
        !withoutHardwraps.contains('<br')
    }

    void 'custom config - abbreviations'() {
        when:
        String markdown = '''
HTML

*[HTML]: Hyper Text Markup Language
'''
        String result = service.markdown(markdown, [abbreviations: true])

        then:
        result.contains('<abbr')
    }

    void 'custom config - definition lists'() {
        when:
        String markdown = '''
Term
:   Definition
'''
        String result = service.markdown(markdown, [definitionLists: true])

        then:
        result.contains('<dl>')
        result.contains('<dt>')
        result.contains('<dd>')
    }

    void 'custom config - removeHtml'() {
        when:
        String markdown = '# Test\n<div>HTML content</div>'
        String withSuppress = service.markdown(markdown, [removeHtml: true])
        String withoutSuppress = service.markdown(markdown)

        then:
        !withSuppress.contains('<div>')
        withoutSuppress.contains('<div>')
    }

    void 'custom config - removeTables'() {
        when:
        String markdown = '''
        | Header 1 | Header 2 |
        |----------|----------|
        | Cell 1   | Cell 2   |
        '''
        String result = service.markdown(markdown, [tables: true, removeTables: true])

        then:
        !result.contains('<table>')
    }

    void 'sanitize - removes script tags'() {
        when:
        String unsafe = "# Safe Content\n<script>alert('XSS')</script>"
        String safe = service.sanitize(unsafe, [removeHtml: true])

        then:
        !safe.contains('<script>')
        safe.contains('Safe Content')
    }

    void 'sanitize - removes inline styles'() {
        when:
        String unsafe = '<p style="color: red;">Text</p>'
        String safe = service.sanitize(service.htmlToMarkdown(unsafe))

        then:
        !safe.contains('style')
    }

    void 'sanitize - preserves safe markdown'() {
        when:
        String safe = '# Heading\n**Bold** text'
        String result = service.sanitize(safe)

        then:
        result.contains('Heading')
        result.contains('Bold')
    }

    void 'sanitize - empty or null input'() {
        expect:
        service.sanitize(null) == ''
        service.sanitize('') == ''
    }

    void 'sanitize - round trip preserves content'() {
        when:
        String original = '# Test\n**Bold** and *italic*'
        String sanitized = service.sanitize(original)

        then:
        sanitized.contains('Test')
        sanitized.contains('Bold')
        sanitized.contains('italic')
    }

    void 'resolveBaseUri - uses provided custom URI'() {
        when:
        service.grailsApplication.config.markdown.baseUri = 'http://default.com/'
        String result = service.resolveBaseUri('http://custom.com/')

        then:
        result == 'http://custom.com/'
    }

    @Unroll
    void 'resolveBaseUri - uses default when custom is null or empty'() {
        given:
        service.grailsApplication.config.markdown.baseUri = 'http://default.com/'
        service.afterPropertiesSet()

        when:
        String result = service.resolveBaseUri(customBaseUri)

        then:
        result == 'http://default.com/'

        where:
        customBaseUri << ['', null]
    }

    void 'resolveBaseUri - adds trailing slash'() {
        expect:
        service.resolveBaseUri('http://example.com') == 'http://example.com/'
    }

    void 'resolveBaseUri - preserves existing trailing slash'() {
        expect:
        service.resolveBaseUri('http://example.com/') == 'http://example.com/'
    }

    void 'resolveBaseUri - handles null base URI'() {
        given:
        service.grailsApplication.config = null
        service.afterPropertiesSet()

        when:
        String result = service.resolveBaseUri(null)

        then:
        result == null
    }

    void 'parser is cached and reused across multiple calls'() {
        when:
        service.markdown('# Test 1')
        def parser1 = service.@parser

        then:
        100.times {
            service.markdown('# Test 2')
            assert parser1 != null

            def parser2 = service.@parser
            assert parser1.is(parser2)
        }
    }

    void 'renderer is cached and reused'() {
        when:
        def renderer1 = service.getRenderer()
        def renderer2 = service.getRenderer()

        then:
        renderer1.is(renderer2)
    }

    void 'htmlConverter is cached and reused'() {
        when:
        def converter1 = service.getHtmlConverter()
        def converter2 = service.getHtmlConverter()

        then:
        converter1.is(converter2)
    }

    void 'custom config creates new instances'() {
        when:
        def parser1 = service.getParser([tables: true])
        def parser2 = service.getParser([tables: true])

        then:
        !parser1.is(parser2)
    }

    void 'setupConfiguration - uses grails config'() {
        when:
        service.grailsApplication.config.markdown = [
                tables   : true,
                autoLinks: true
        ]
        service.afterPropertiesSet()

        then:
        service.markdown('http://example.com', null).contains('<a href')
    }

    void 'setupConfiguration - sets baseUri from config'() {
        given:
        service.grailsApplication.config.markdown = [baseUri: 'http://from-config.com/']
        service.afterPropertiesSet()

        when:
        service.htmlToMarkdown('<a href="/api/countries">Countries</a>')

        then:
        service.@baseUri == 'http://from-config.com/'
    }

    void 'setupConfiguration - uses grails.serverURL as fallback'() {
        given:
        service.grailsApplication.config.markdown = [:]
        service.grailsApplication.config.grails.serverURL = 'http://fallback.com'
        service.afterPropertiesSet()

        when:
        service.htmlToMarkdown('<a href="/api/countries">Countries</a>')

        then:
        service.@baseUri == 'http://fallback.com'
    }

    void 'setupConfiguration - baseUri false disables it'() {
        given:
        service.grailsApplication.config.markdown = [baseUri: false]
        service.afterPropertiesSet()

        when:
        service.htmlToMarkdown('<a href="/api/countries">Countries</a>')

        then:
        service.@baseUri == null
    }

    void 'customizeFlexmark closure is called'() {
        given:
        boolean closureCalled = false
        def config = [
                customizeFlexmark: { mutable ->
                    closureCalled = true
                    return mutable
                }
        ]

        when:
        service.markdown('# Test', config)

        then:
        closureCalled
    }

    void 'customizeFlexmark handles null return from closure'() {
        given: "customizeFlexmark returns accidently null"
        def config = [
                customizeFlexmark: { mutable ->
                    null
                }
        ]

        when:
        def result = service.markdown('# Test', config)

        then:
        result != null
    }

    void 'customizeFlexmark can modify settings'() {
        given:
        def config = [
                customizeFlexmark: { mutable ->
                    return mutable
                }
        ]

        when:
        def result = service.markdown('# Test', config)

        then:
        result != null
    }

    void 'markdown - handles very long content'() {
        when:
        String longContent = '# Heading\n\n' + ('Lorem ipsum dolor sit amet. ' * 1000)
        String result = service.markdown(longContent)

        then:
        result.contains('<h1>Heading</h1>')
        result.length() > 1000
    }

    void 'markdown - handles special characters'() {
        when:
        String special = '# Test & < > " \' characters'
        String result = service.markdown(special)

        then:
        result.contains('&amp;')
        result.contains('&lt;')
        result.contains('&gt;')
    }

    void 'markdown - handles unicode characters'() {
        when:
        String unicode = '# Test 你好 مرحبا नमस्ते 🎉'
        String result = service.markdown(unicode)

        then:
        result.contains('你好')
        result.contains('مرحبا')
        result.contains('नमस्ते')
        result.contains('🎉')
    }

    void 'htmlToMarkdown - handles malformed HTML'() {
        when:
        String malformed = '<p>Unclosed paragraph <strong>bold'
        String result = service.htmlToMarkdown(malformed)

        then:
        result != null
        result.length() > 0
    }

    void 'markdown - preserves whitespace in code blocks'() {
        when:
        String code = '    indented code\n    with spaces'
        String result = service.markdown(code)

        then:
        result.contains('<code>')
    }

    @Unroll
    void 'markdown heading level #level produces correct HTML'() {
        expect:
        service.markdown(markdown) == expectedHtml

        where:
        level | markdown    | expectedHtml
        1     | '# H1'      | '<h1>H1</h1>\n'
        2     | '## H2'     | '<h2>H2</h2>\n'
        3     | '### H3'    | '<h3>H3</h3>\n'
        4     | '#### H4'   | '<h4>H4</h4>\n'
        5     | '##### H5'  | '<h5>H5</h5>\n'
        6     | '###### H6' | '<h6>H6</h6>\n'
    }

    @Unroll
    void 'configuration option #option works correctly'() {
        when:
        def config = [(option): true]
        String result = service.markdown(markdown, config)

        then:
        result.contains(expectedContent)

        where:
        option             | markdown                          | expectedContent
        'autoLinks'        | 'http://example.com'              | '<a href='
        'tables'           | '| A | B |\n|---|---|\n| 1 | 2 |' | '<table>'
        'fencedCodeBlocks' | '```\ncode\n```'                  | '<code>'
    }

    @Unroll
    void 'HTML entity #entity converts correctly in htmlToMarkdown'() {
        expect:
        service.htmlToMarkdown(html, '', [all: true]) == expectedMarkdown

        where:
        entity  | html      | expectedMarkdown
        'ldquo' | '&ldquo;' | '"\n'
        'rdquo' | '&rdquo;' | '"\n'
        'mdash' | '&mdash;' | '---\n\n'
        'ndash' | '&ndash;' | '--\n'
    }

    void 'round trip - markdown to HTML to markdown preserves structure'() {
        when:
        String original = '''
        # Heading
        **Bold** and *italic* text.
        
        - List item 1
        - List item 2
        
        [Link](http://example.com)
        '''
        String html = service.markdown(original)
        String backToMarkdown = service.htmlToMarkdown(html)

        then:
        backToMarkdown.contains('Heading')
        backToMarkdown.contains('Bold')
        backToMarkdown.contains('italic')
        backToMarkdown.contains('example.com')
    }

    void 'round trip - HTML to markdown to HTML preserves content'() {
        when:
        String originalHtml = '<h1>Test</h1><p><strong>Bold</strong> text</p>'
        String markdown = service.htmlToMarkdown(originalHtml)
        String backToHtml = service.markdown(markdown)

        then:
        backToHtml.contains('<h1>')
        backToHtml.contains('<strong>')
        backToHtml.contains('Test')
        backToHtml.contains('Bold')
    }

    void 'service instances are immutable after initialization'() {
        when:
        service.markdown('# Test')
        service.htmlToMarkdown('<h1>Test</h1>')
        service.sanitize('# Test')

        def originalParser = service.@parser
        def originalRenderer = service.@renderer
        def originalFormatter = service.@formatter
        def originalConverter = service.@htmlConverter

        100.times {
            service.markdown('# Test')
            service.htmlToMarkdown('<h1>Test</h1>')
            service.sanitize('# Test')
        }

        then: 'cached instances have not changed'
        service.@parser.is(originalParser)
        service.@renderer.is(originalRenderer)
        service.@formatter.is(originalFormatter)
        service.@htmlConverter.is(originalConverter)
    }

    void 'custom config does not affect cached instances'() {
        when:
        def originalParser = service.@parser
        def originalRenderer = service.@renderer

        100.times {
            service.markdown('# Test', [tables: true])
            service.markdown('# Test', [autoLinks: true])
        }

        then: 'cached instances remain unchanged'
        service.@parser.is(originalParser)
        service.@renderer.is(originalRenderer)
    }
}
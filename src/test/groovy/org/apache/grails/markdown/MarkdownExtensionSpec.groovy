package org.apache.grails.markdown

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

/**
 * Tests the extension method.
 */
class MarkdownExtensionSpec extends Specification implements GrailsUnitTest {

	void setup() {
		defineBeans {
			markdownService(MarkdownService) {
				grailsApplication = ref('grailsApplication')
			}
		}
	}

	void cleanup() {
		MarkdownExtension.resetCache()
	}

	void 'string markdown to HTML'() {
		expect:
		'<p><a href="http://example.net/">This link</a> has no title attribute.</p>\n' ==
				  '[This link](http://example.net/) has no title attribute.'.markdownToHtml()
	}

	void 'string HTML to markdown'() {
		expect:
		'[This link][] has no title attribute.\n\n[This link]: http://example.net/\n\n' ==
				'<p><a href="http://example.net/">This link</a> has no title attribute.</p>'.htmlToMarkdown()
	}
}

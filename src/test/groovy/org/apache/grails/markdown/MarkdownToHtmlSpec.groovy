package org.apache.grails.markdown

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

/**
 * Tests the String.metaClass.htmlToMarkdown() method.
 */
class MarkdownToHtmlSpec extends Specification implements GrailsUnitTest {

	void setup() {
		defineBeans {
			markdownService(MarkdownService) {
				grailsApplication = ref('grailsApplication')
			}
		}

		MarkdownPluginSupport.doWithDynamicMethods applicationContext.markdownService
	}

	void 'string HTML to markdown'() {
		expect:
		'[This link][] has no title attribute.\n\n[This link]: http://example.net/\n\n' ==
				  '<p><a href="http://example.net/">This link</a> has no title attribute.</p>'.htmlToMarkdown()
	}
}

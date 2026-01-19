package org.apache.grails.markdown


import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

/**
 * Tests the String.metaClass.markdownToHtml() method.
 */
class MarkdownToMarkdownSpec extends Specification implements GrailsUnitTest {

	void setup() {
		defineBeans {
			markdownService(MarkdownService) {
				grailsApplication = ref('grailsApplication')
			}
		}

		MarkdownPluginSupport.doWithDynamicMethods applicationContext.markdownService
	}

	void 'string markdown to HTML'() {
		expect:
		'<p><a href="http://example.net/">This link</a> has no title attribute.</p>\n' ==
				  '[This link](http://example.net/) has no title attribute.'.markdownToHtml()
	}
}

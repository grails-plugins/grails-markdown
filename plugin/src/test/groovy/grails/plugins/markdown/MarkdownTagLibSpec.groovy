package grails.plugins.markdown

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class MarkdownTagLibSpec extends Specification implements TagLibUnitTest<MarkdownTagLib> {

    void setup() {
        defineBeans {
            markdownService(MarkdownService) {
                grailsApplication = ref('grailsApplication')
            }
        }
    }

    void "renderHtml using body"() {
        when:
        String actual = applyTemplate("<markdown:renderHtml>This is a *test* of markdown.</markdown:renderHtml>")

        then:
        actual == '<p>This is a <em>test</em> of markdown.</p>\n'
    }


    void "renderHtml using attribute"() {
        when:
        String actual = applyTemplate("<markdown:renderHtml text='Yet **another** markdown test.'></markdown:renderHtml>")

        then:
        actual == '<p>Yet <strong>another</strong> markdown test.</p>\n'
    }

}

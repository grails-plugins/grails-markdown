package grails.plugins.markdown.util

import groovy.transform.CompileStatic
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

@CompileStatic
final class JsoupUtils {

    private JsoupUtils() {}

    private static final List<String> URI_ATTRIBUTES = ['href', 'src'].asImmutable()
    private static final String URI_ATTRIBUTES_SELECTOR = '[href], [src]'

    /**
     * Parse HTML text and resolve relative URLs to absolute if base URI is provided
     *
     * @param text HTML text to parse
     * @param customBaseUri Custom base URI
     * @return Parsed Jsoup Document
     */
    static Document parse(String text, String customBaseUri) {
        def doc = Jsoup.parse(text, customBaseUri)
        doc.setBaseUri(customBaseUri)

        toAbsoluteURL(doc)

        return doc
    }

    private static void toAbsoluteURL(Document doc) {
        doc.select(URI_ATTRIBUTES_SELECTOR).each { element ->
            URI_ATTRIBUTES.each { attr ->
                if (element.hasAttr(attr)) {
                    String absUrl = element.absUrl(attr)
                    if (absUrl) {
                        element.attr(attr, absUrl)
                    }
                }
            }
        }
    }
}

package org.apache.grails.markdown

import grails.core.GrailsApplication
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.grails.markdown.util.JsoupUtils
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.DataHolder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean

import static grails.util.GrailsStringUtils.isBlank

/**
 * Service for converting between Markdown and HTML using flexmark-java with Pegdown compatibility
 * <p>This service is initialized once by Spring and is thread-safe for concurrent use.
 * The cached parser, renderer, formatter, and converter instances are immutable after
 * initialization. Custom configurations create new instances per call.</p>
 */
@Slf4j
@CompileStatic
class MarkdownService implements InitializingBean {

    GrailsApplication grailsApplication

    private Parser parser
    private HtmlRenderer renderer
    private Formatter formatter
    private FlexmarkHtmlConverter htmlConverter

    private String baseUri
    private DataHolder defaultOptions

    @Override
    void afterPropertiesSet() throws Exception {
        Map defaultConf = grailsApplication?.config?.markdown as Map
        this.defaultOptions = getOptions(defaultConf)
        this.baseUri = getDefaultBaseUri(defaultConf)

        this.parser = Parser.builder(defaultOptions).build()
        this.renderer = HtmlRenderer.builder(defaultOptions).build()
        this.htmlConverter = FlexmarkHtmlConverter.builder(defaultOptions).build()
        this.formatter = Formatter.builder(defaultOptions).build()
    }

    /**
     * Converts the provided Markdown into HTML
     *
     * <p>By default this method uses the shared configuration. However, the default configuration can
     * be overridden by passing in a map or map-like object as the second argument. With a custom
     * configuration, a new parser is created <strong>every call to this method!</strong></p>
     *
     * @param text Markdown-formatted text
     * @param conf If provided, creates a custom parser with unique settings just for this instance
     * @return HTML-formatted text
     */
    String markdown(String text, Map conf = null) {
        if (isBlank(text)) {
            return ''
        }

        Parser p = getParser(conf)
        HtmlRenderer r = getRenderer(conf)

        def doc = p.parse(text.toString())
        return r.render(doc)
    }

    /**
     * Converts the provided HTML back to Markdown
     *
     * <p>By default this method uses the shared configuration. However, the default configuration can
     * be overridden by passing in a map or map-like object as the second argument. With a custom
     * configuration, a new converter is created <strong>every call to this method!</strong></p>
     *
     * @param text HTML-formatted text
     * @param customBaseUri Override the default base URL
     * @param conf If provided, creates a custom converter with unique settings just for this instance
     * @return Markdown-formatted text
     */
    String htmlToMarkdown(String text, String customBaseUri = "", Map conf = null) {
        if (isBlank(text)) {
            return ''
        }

        FlexmarkHtmlConverter converter = getHtmlConverter(conf)
        String resolvedBaseUri = resolveBaseUri(customBaseUri)

        String markdown
        if (resolvedBaseUri) {
            //Rewrite absolute paths using Jsoup directly
            def doc = JsoupUtils.parse(text, resolvedBaseUri)
            markdown = converter.convert(doc)
            def mdDoc = getParser(conf).parse(markdown)
            return getFormatter(conf).render(mdDoc)
        } else {
            markdown = converter.convert(text)
            def mdDoc = getParser(conf).parse(markdown)
            return getFormatter(conf).render(mdDoc)
        }
    }

    /**
     * Utility method to strip untrusted HTML from markdown input.
     *
     * <p>Works by simply running the text through flexmark and back through html converter.</p>
     *
     * <p>By default this method uses the shared configuration. However, the default configuration can
     * be overridden by passing in a map or map-like object as the second argument. With a custom
     * configuration, new processing engines are created <strong>every call to this method!</strong></p>
     *
     * @param text Markdown-formatted text
     * @param conf If provided, creates custom converter and parser with unique settings for this instance
     * @return Sanitized Markdown-formatted text
     */
    String sanitize(String text, Map conf = null) {
        return htmlToMarkdown(markdown(text, conf), '', conf)
    }

    /**
     * Returns or creates the Parser instance used for conversion
     * @param conf Optional configuration Map to create a custom parser
     * @return Parser instance
     */
    @PackageScope
    Parser getParser(Map conf = null) {
        if (conf) {
            DataHolder options = getOptions(conf)
            return Parser.builder(options).build()
        }
        return parser
    }

    /**
     * Returns or creates the HtmlRenderer instance used for conversion
     * @param conf Optional configuration Map to create a custom renderer
     * @return HtmlRenderer instance
     */
    @PackageScope
    HtmlRenderer getRenderer(Map conf = null) {
        if (conf) {
            DataHolder options = getOptions(conf)
            return HtmlRenderer.builder(options).build()
        }
        return renderer
    }

    /**
     * Returns or creates the FlexmarkHtmlConverter instance used for HTML to Markdown conversion
     * @param conf Optional configuration Map to create a custom converter
     * @return FlexmarkHtmlConverter instance
     */
    @PackageScope
    FlexmarkHtmlConverter getHtmlConverter(Map conf = null) {
        if (conf) {
            DataHolder options = getOptions(conf)
            return FlexmarkHtmlConverter.builder(options).build()
        }
        return htmlConverter
    }

    /**
     * Returns or creates the Formatter instance used for formatting pegdown compatible markdown
     * @param conf Optional configuration Map to create a custom converter
     * @return Formatter instance
     */
    private Formatter getFormatter(Map conf = null) {
        if (conf) {
            DataHolder options = getOptions(conf)
            return Formatter.builder(options).build()
        }
        return formatter
    }

    /**
     * Builds flexmark options from configuration map using PegdownOptionsAdapter for compatibility
     * @param conf Configuration map
     * @return DataHolder with flexmark options configured for Pegdown compatibility
     */
    private DataHolder getOptions(Map conf) {
        return PegdownAdapter.flexmarkOptions(conf)
    }


    @PackageScope
    String resolveBaseUri(String customBaseUri) {
        String uri = (customBaseUri ?: baseUri)

        if (!uri) {
            return uri
        }

        return uri && !uri.endsWith('/') ? uri + '/' : uri
    }

    /**
     * Set base URI,
     * @param conf
     */
    private String getDefaultBaseUri(Map conf) {
        // only disable baseUri if it is explicitly set to false
        if (conf != null && conf.baseUri == false) {
            return null
        }

        return conf?.baseUri ?: grailsApplication?.config?.getProperty('grails.serverURL')
    }
}

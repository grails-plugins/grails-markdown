package grails.plugins.markdown

import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.LinkConversion
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.DataKey
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.format.options.CodeFenceMarker
import com.vladsch.flexmark.util.misc.Extension
import groovy.util.logging.Slf4j

@Slf4j
class FlexmarkConfiguration {

    static DataHolder getOptions(Map conf) {
        DataHolder options = getConfigurations(conf)
        return options.toImmutable()
    }

    private static MutableDataSet base() {
        MutableDataSet options = new MutableDataSet()

        // Added to support existing behaviour of reference definition links
        options.set(FlexmarkHtmlConverter.EXT_INLINE_LINK, LinkConversion.MARKDOWN_REFERENCE)
        options.set(FlexmarkHtmlConverter.EXT_INLINE_IMAGE, LinkConversion.MARKDOWN_REFERENCE)

        // Matches existing behaviour, Must NOT emit typographic HTML entities by defaults
        options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_QUOTES, false)
        options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_SMARTS, false)

        // Pegdown emulation profile for compatibility with existing format
        options.set(Parser.PARSER_EMULATION_PROFILE, ParserEmulationProfile.PEGDOWN)

        //Default extensions
        options.set(Parser.EXTENSIONS, Arrays.asList(StrikethroughExtension.create()))
        return options
    }

    // this is where the configuration actually happens
    // conf can be set via any map-like object
    private static DataHolder getConfigurations(Map conf) {
        DataHolder options = base()
        List<Extension> extensions = []

        if (!conf) {
            return options
        }

        boolean all = conf.all

        def addExtension = { test = true, Extension extension ->
            if (all || test) {
                extensions.add(extension)
            }
        }

        def addOption = { test = true, DataKey key, value ->
            if (all || test) {
                options.set(key, value)
            }
        }

        addExtension(conf.abbreviations, AbbreviationExtension.create())
        addOption(conf.hardwraps, HtmlRenderer.SOFT_BREAK, '<br />\n')
        addExtension(conf.definitionLists, DefinitionExtension.create())
        addExtension(conf.autoLinks, AutolinkExtension.create())
        addExtension(conf.smartQuotes, TypographicExtension.create())
        addOption(conf.smartPunctuation, TypographicExtension.ENABLE_SMARTS, true);
        addOption(conf.smartPunctuation, TypographicExtension.ENABLE_QUOTES, false);
        addExtension(conf.smart, TypographicExtension.create())

        boolean smart = all || conf.smart
        boolean enableSmartQuotes = smart || conf.smartQuotes
        boolean enableSmartPunctuation = smart || conf.smartPunctuation
        options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_QUOTES, enableSmartQuotes)
        options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_SMARTS, enableSmartPunctuation)

        if (all || conf.fencedCodeBlocks) {
            options.set(Formatter.FENCED_CODE_MARKER_TYPE, CodeFenceMarker.TILDE)
            addOption(Parser.FENCED_CODE_BLOCK_PARSER, true);
            addOption(Parser.MATCH_CLOSING_FENCE_CHARACTERS, true);
        }

        if (conf.removeHtml) {
            addOption(HtmlRenderer.ESCAPE_HTML, true)
        }

        if (!conf.removeHtml && !conf.removeTables && (all || conf.tables)) {
            options.set(TablesExtension.COLUMN_SPANS, true)
            options.set(TablesExtension.WITH_CAPTION, true)
            options.set(TablesExtension.MIN_HEADER_ROWS, 0)
            addExtension(TablesExtension.create())
        }

        options.set(Parser.EXTENSIONS, [*extensions, *Parser.EXTENSIONS.get(options)])

        if (conf.customizeFlexmark) {
            MutableDataHolder safeOptions = new MutableDataSet(options)
            def customizeFlexmark = conf.customizeFlexmark as Closure<DataHolder>
            def customOptions = customizeFlexmark(safeOptions)
            if (customOptions && customOptions instanceof DataHolder) {
                options.setAll(customOptions)
            } else {
                options.clear()
                options.setAll(safeOptions)
            }
        }

        return options
    }
}

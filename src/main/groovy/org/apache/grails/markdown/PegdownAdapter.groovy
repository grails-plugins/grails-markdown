package org.apache.grails.markdown

import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.LinkConversion
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.parser.PegdownExtensions
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.format.options.CodeFenceMarker
import groovy.util.logging.Slf4j

@Slf4j
class PegdownAdapter {

    static DataHolder flexmarkOptions(Map conf) {
        Map pegdown = getConfigurations(conf)
        DataHolder options = PegdownOptionsAdapter.flexmarkOptions(pegdown.extensions)
        MutableDataHolder mutable = new MutableDataSet(options)
        mutable.setAll(pegdown.options)
        return mutable.toImmutable()
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

        return options
    }

    // this is where the configuration actually happens
    // conf can be set via any map-like object
    private static Map getConfigurations(Map conf) {
        Map result = [options: base(), extensions: PegdownExtensions.NONE]

        if (!conf) {
            return result
        }

        boolean all = conf.all

        def addPegdownExtension = { int mask -> result.extensions = (int) (result.extensions | mask) }
        def enableIf = { test, int mask ->
            if (all || test) {
                addPegdownExtension(mask)
            }
        }
        enableIf(conf.abbreviations, PegdownExtensions.ABBREVIATIONS)
        enableIf(conf.hardwraps, PegdownExtensions.HARDWRAPS)
        enableIf(conf.definitionLists, PegdownExtensions.DEFINITIONS)
        enableIf(conf.autoLinks, PegdownExtensions.AUTOLINKS)
        enableIf(conf.smartQuotes, PegdownExtensions.QUOTES)
        enableIf(conf.smartPunctuation, PegdownExtensions.SMARTS)
        enableIf(conf.smart, PegdownExtensions.SMARTYPANTS)

        boolean smart = all || conf.smart
        boolean enableSmartQuotes = smart || conf.smartQuotes
        boolean enableSmartPunctuation = smart || conf.smartPunctuation
        result.options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_QUOTES, enableSmartQuotes)
        result.options.set(FlexmarkHtmlConverter.TYPOGRAPHIC_SMARTS, enableSmartPunctuation)

        if (all || conf.fencedCodeBlocks) {
            result.options.set(Formatter.FENCED_CODE_MARKER_TYPE, CodeFenceMarker.TILDE)
            addPegdownExtension(PegdownExtensions.FENCED_CODE_BLOCKS)
        }

        if (conf.removeHtml) {
            addPegdownExtension(PegdownExtensions.SUPPRESS_ALL_HTML)
        }

        if (!conf.removeHtml && !conf.removeTables && (all || conf.tables)) {
            result.options.set(TablesExtension.COLUMN_SPANS, true)
            result.options.set(TablesExtension.WITH_CAPTION, true)
            result.options.set(TablesExtension.MIN_HEADER_ROWS, 0)
            addPegdownExtension(PegdownExtensions.TABLES)
        }

        if (conf.customizeFlexmark) {
            MutableDataHolder safeOptions = new MutableDataSet(result.options)
            def customizeFlexmark = conf.customizeFlexmark as Closure<DataHolder>
            def customOptions = customizeFlexmark(safeOptions)
            if (customOptions && customOptions instanceof DataHolder) {
                result.options.setAll(customOptions)
            } else {
                result.options.clear()
                result.options.setAll(safeOptions)
            }
        }

        return result
    }
}

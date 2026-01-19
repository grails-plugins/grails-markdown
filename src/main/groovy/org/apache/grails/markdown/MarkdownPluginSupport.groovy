package org.apache.grails.markdown

class MarkdownPluginSupport {

    static void doWithDynamicMethods(MarkdownService markdownService) {
        // String extension for markdown to HTML
        String.metaClass.markdownToHtml = {
            markdownService.markdown(delegate)
        }

        // String extension for HTML to markdown
        String.metaClass.htmlToMarkdown = {
            markdownService.htmlToMarkdown(delegate)
        }

    }
}
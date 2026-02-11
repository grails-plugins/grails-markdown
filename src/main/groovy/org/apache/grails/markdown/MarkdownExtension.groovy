package org.apache.grails.markdown

import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@CompileStatic
class MarkdownExtension {

    private static volatile MarkdownService cachedMarkdownService = null

    @PackageScope
    static synchronized void resetCache() {
        cachedMarkdownService = null
    }

    private static MarkdownService getMarkdownService() {
        if (cachedMarkdownService == null) {
            synchronized (MarkdownExtension) {
                if (cachedMarkdownService == null) {
                    try {
                        cachedMarkdownService = Holders.applicationContext.getBean(MarkdownService)
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("MarkdownService is not available, Grails application context not initialized", e)
                    }
                }
            }
        }
        return cachedMarkdownService
    }

    static String markdownToHtml(String self) {
        return markdownService.markdown(self)
    }

    static String htmlToMarkdown(String self) {
        return markdownService.htmlToMarkdown(self)
    }
}

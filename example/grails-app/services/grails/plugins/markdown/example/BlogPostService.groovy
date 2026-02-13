package grails.plugins.markdown.example

import grails.gorm.transactions.Transactional

@Transactional
class BlogPostService {
    
    def markdownService
    
    def list(Map params) {
        BlogPost.list(params)
    }
    
    def count() {
        BlogPost.count()
    }
    
    def get(Serializable id) {
        BlogPost.get(id)
    }
    
    def save(BlogPost blogPost) {
        // Convert markdown to HTML and cache it
        if (blogPost.markdownContent) {
            blogPost.htmlContent = markdownService.markdown(blogPost.markdownContent, [
                hardwraps: true,
                autoLinks: true,
                fencedCodeBlocks: true,
                tables: true,
                abbreviations: true,
                definitionLists: true
            ])
        }
        blogPost.save(flush: true)
    }
    
    def update(BlogPost blogPost) {
        // Re-convert markdown to HTML when updating
        if (blogPost.markdownContent) {
            blogPost.htmlContent = markdownService.markdown(blogPost.markdownContent, [
                hardwraps: true,
                autoLinks: true,
                fencedCodeBlocks: true,
                tables: true,
                abbreviations: true,
                definitionLists: true
            ])
        }
        blogPost.save(flush: true)
    }
    
    def delete(Serializable id) {
        def blogPost = BlogPost.get(id)
        if (blogPost) {
            blogPost.delete(flush: true)
            return true
        }
        return false
    }
    
    def search(String query) {
        if (!query) {
            return []
        }
        
        BlogPost.findAll { 
            title =~ /%${query}%/ || 
            markdownContent =~ /%${query}%/ || 
            tags =~ /%${query}%/
        }
    }
    
    def convertHtmlToMarkdown(String html) {
        markdownService.htmlToMarkdown(html)
    }
}

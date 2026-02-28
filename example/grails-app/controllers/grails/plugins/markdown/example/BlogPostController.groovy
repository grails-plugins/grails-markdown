package grails.plugins.markdown.example

import grails.plugins.markdown.MarkdownService
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class BlogPostController {

    BlogPostService blogPostService
    MarkdownService markdownService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def posts = blogPostService.list(params)
        def postCount = blogPostService.count()
        
        respond posts, model: [blogPostCount: postCount]
    }

    def show(Long id) {
        def blogPost = blogPostService.get(id)
        if (!blogPost) {
            notFound()
            return
        }
        
        respond blogPost
    }

    def create() {
        respond new BlogPost(params)
    }

    def save(BlogPost blogPost) {
        if (blogPost == null) {
            notFound()
            return
        }

        try {
            blogPostService.save(blogPost)
        } catch (ValidationException e) {
            respond blogPost.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'blogPost.label', default: 'BlogPost'), blogPost.id])
                redirect blogPost
            }
            '*' { respond blogPost, [status: CREATED] }
        }
    }

    def edit(Long id) {
        def blogPost = blogPostService.get(id)
        if (!blogPost) {
            notFound()
            return
        }
        
        respond blogPost
    }

    def update(BlogPost blogPost) {
        if (blogPost == null) {
            notFound()
            return
        }

        try {
            blogPostService.update(blogPost)
        } catch (ValidationException e) {
            respond blogPost.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'blogPost.label', default: 'BlogPost'), blogPost.id])
                redirect blogPost
            }
            '*' { respond blogPost, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        blogPostService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'blogPost.label', default: 'BlogPost'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    def preview() {
        def markdown = params.markdown
        def html = markdownService.markdown(markdown)
        
        render html
    }
    
    def convertToMarkdown() {
        def html = params.html
        def markdown = markdownService.htmlToMarkdown(html)
        
        render markdown
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'blogPost.label', default: 'BlogPost'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

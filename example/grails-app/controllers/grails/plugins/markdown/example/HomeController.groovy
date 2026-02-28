package grails.plugins.markdown.example

class HomeController {

    BlogPostService blogPostService

    def index() {
        def recentPosts = blogPostService.list(max: 5, sort: 'dateCreated', order: 'desc')
        [recentPosts: recentPosts]
    }
}

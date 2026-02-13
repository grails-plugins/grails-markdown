package grails.plugins.markdown.example

class BlogPost {
    
    String title
    String author
    String markdownContent
    String htmlContent
    String tags
    Date dateCreated
    Date lastUpdated
    
    static constraints = {
        title blank: false, maxSize: 200
        author blank: false, maxSize: 100
        markdownContent blank: false, maxSize: 50000
        htmlContent nullable: true, maxSize: 100000
        tags nullable: true, maxSize: 500
    }
    
    static mapping = {
        markdownContent type: 'text'
        htmlContent type: 'text'
        sort dateCreated: 'desc'
    }

}

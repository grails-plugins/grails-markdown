package grails.plugins.markdown.example

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints { }
        }

        "/"(controller: 'home', action: 'index')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

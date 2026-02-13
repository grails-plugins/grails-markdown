package grails.plugins.markdown

import grails.plugins.Plugin

class MarkdownGrailsPlugin extends Plugin {

	def grailsVersion = '7.0.0 > *'
	def author = "Ted Naleid"
	def authorEmail = "contact@naleid.com"
	def title = "Grails Markdown Plugin"
	def developers = [
		[name: "Phil DeJarnett"], [name: "Rahul Shishodia"]
	]
	def description = 'Provides a tag library and service support for Markdown'
	def documentation = "https://github.com/grails-plugins/grails-markdown"
	def issueManagement = [url: 'https://github.com/grails-plugins/grails-markdown/issues']
	def scm = [url: "https://github.com/grails-plugins/grails-markdown"]
	def license = 'APACHE'

}

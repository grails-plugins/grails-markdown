package grails.plugins.markdown

import grails.artefact.TagLibrary

class MarkdownTagLib implements TagLibrary {

	static namespace = "markdown"

	MarkdownService markdownService

	def renderHtml = { attrs, body ->
		String text
		if(attrs.template) {
			text = g.render(template: attrs.template)
		} else {
			text = attrs.text ?: body()
		}
		out << markdownService.markdown(text)
	}
}

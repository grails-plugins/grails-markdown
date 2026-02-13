<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${blogPost?.title}</title>
</head>
<body>
    <g:if test="${blogPost}">
        <div class="card">
            <h1>${blogPost.title}</h1>
            <div class="meta">
                By <strong>${blogPost.author}</strong> • 
                <g:formatDate date="${blogPost.dateCreated}" format="MMMM dd, yyyy 'at' hh:mm a"/>
                <g:if test="${blogPost.lastUpdated != blogPost.dateCreated}">
                    • Last updated: <g:formatDate date="${blogPost.lastUpdated}" format="MMM dd, yyyy"/>
                </g:if>
            </div>
            
            <g:if test="${blogPost.tags}">
                <div class="tags">
                    <g:each in="${blogPost.tags.split(',')}" var="tag">
                        <span class="tag">${tag.trim()}</span>
                    </g:each>
                </div>
            </g:if>
            
            <hr style="margin: 1.5rem 0; border: none; border-top: 1px solid #e0e0e0;">
            
            <div class="markdown-content">
                <%-- Using the markdown tag library to convert markdown to HTML --%>
                <markdown:renderHtml text="${blogPost.markdownContent}" />
            </div>
            
            <hr style="margin: 1.5rem 0; border: none; border-top: 1px solid #e0e0e0;">
            
            <div style="margin-top: 2rem;">
                <g:link action="edit" id="${blogPost.id}" class="btn">Edit Post</g:link>
                <g:link action="index" class="btn btn-secondary">Back to List</g:link>
                <g:form method="DELETE" action="delete" id="${blogPost.id}" style="display: inline;">
                    <button type="submit" class="btn btn-danger" 
                            onclick="return confirm('Are you sure you want to delete this post?');">
                        Delete Post
                    </button>
                </g:form>
            </div>
        </div>
        
        <%-- Show the raw markdown source in a collapsible section --%>
        <div class="card">
            <h3>Markdown Source</h3>
            <details>
                <summary style="cursor: pointer; color: #3498db;">Click to view raw markdown</summary>
                <pre style="background: #f4f4f4; padding: 1rem; border-radius: 4px; overflow-x: auto; margin-top: 1rem;"><code>${blogPost.markdownContent}</code></pre>
            </details>
        </div>
    </g:if>
    <g:else>
        <div class="card">
            <h2>Post Not Found</h2>
            <p>The blog post you're looking for doesn't exist.</p>
            <g:link action="index" class="btn">Back to All Posts</g:link>
        </div>
    </g:else>
</body>
</html>

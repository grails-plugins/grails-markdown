<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Markdown Blog</title>
    <style>
        .card ul {
            margin-left: 2rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="card">
        <markdown:renderHtml template="welcome" />
        <div style="margin-top: 2rem;">
            <g:link controller="blogPost" action="create" class="btn btn-success">Create Your First Post</g:link>
            <g:link controller="blogPost" action="index" class="btn">View All Posts</g:link>
        </div>
    </div>

    <h2>Recent Posts</h2>
    <g:if test="${recentPosts}">
        <g:each in="${recentPosts}" var="post">
            <div class="card">
                <h3><g:link controller="blogPost" action="show" id="${post.id}">${post.title}</g:link></h3>
                <div class="meta">
                    By ${post.author} • <g:formatDate date="${post.dateCreated}" format="MMM dd, yyyy"/>
                </div>
                <div class="markdown-content">
                    <markdown:renderHtml text="${post.markdownContent.take(300)}..." />
                </div>
                <g:link controller="blogPost" action="show" id="${post.id}" class="btn btn-secondary">Read More →</g:link>

                <g:if test="${post.tags}">
                    <div class="tags">
                        <g:each in="${post.tags.split(',')}" var="tag">
                            <span class="tag">${tag.trim()}</span>
                        </g:each>
                    </div>
                </g:if>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="card">
            <p>No posts yet. <g:link controller="blogPost" action="create">Create the first one!</g:link></p>
        </div>
    </g:else>
</body>
</html>

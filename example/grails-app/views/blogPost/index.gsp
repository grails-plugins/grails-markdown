<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>All Blog Posts</title>
</head>
<body>
    <div class="card">
        <h1>All Blog Posts</h1>
        <div style="margin-top: 1rem;">
            <g:link action="create" class="btn btn-success">New Post</g:link>
        </div>
    </div>
    
    <g:if test="${blogPostList}">
        <g:each in="${blogPostList}" var="post">
            <div class="card">
                <h2><g:link action="show" id="${post.id}">${post.title}</g:link></h2>
                <div class="meta">
                    By ${post.author} • <g:formatDate date="${post.dateCreated}" format="MMMM dd, yyyy 'at' hh:mm a"/>
                    <g:if test="${post.lastUpdated != post.dateCreated}">
                        • Updated: <g:formatDate date="${post.lastUpdated}" format="MMM dd, yyyy"/>
                    </g:if>
                </div>
                
                <div class="markdown-content">
                    <markdown:renderHtml text="${post.markdownContent.take(400)}..." />
                </div>
                
                <div style="margin-top: 1rem;">
                    <g:link action="show" id="${post.id}" class="btn btn-secondary">Read Full Post</g:link>
                    <g:link action="edit" id="${post.id}" class="btn">Edit</g:link>
                </div>
                
                <g:if test="${post.tags}">
                    <div class="tags">
                        <g:each in="${post.tags.split(',')}" var="tag">
                            <span class="tag">${tag.trim()}</span>
                        </g:each>
                    </div>
                </g:if>
            </div>
        </g:each>
        
        <g:if test="${blogPostCount > 10}">
            <div class="card">
                <p>Showing ${blogPostList.size()} of ${blogPostCount} posts</p>
            </div>
        </g:if>
    </g:if>
    <g:else>
        <div class="card">
            <p>No blog posts found. <g:link action="create">Create the first one!</g:link></p>
        </div>
    </g:else>
</body>
</html>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Create New Blog Post</title>
    <asset:stylesheet src="blogPost.css"/>
</head>
<body>
    <div class="card">
        <h1>Create New Blog Post</h1>
        
        <g:hasErrors bean="${blogPost}">
            <div class="alert alert-error">
                <ul>
                    <g:eachError bean="${blogPost}" var="error">
                        <li><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </div>
        </g:hasErrors>
        
        <g:form action="save" method="POST">
            <div class="form-group">
                <label for="title">Title *</label>
                <input type="text" id="title" name="title" value="${blogPost?.title}" required>
            </div>
            
            <div class="form-group">
                <label for="author">Author *</label>
                <input type="text" id="author" name="author" value="${blogPost?.author}" required>
            </div>
            
            <div class="form-group">
                <label for="tags">Tags (comma-separated)</label>
                <input type="text" id="tags" name="tags" value="${blogPost?.tags}" 
                       placeholder="e.g., grails, markdown, tutorial">
            </div>

            <p class="help-text">
                Write your content using Markdown syntax.
                <a href="https://www.markdownguide.org/basic-syntax/" target="_blank">Markdown Guide</a>
            </p>

            <label for="markdownContent">Content (Markdown) *</label>

            <div class="editor-container">
                <div class="editor-pane">
                    <div class="form-group">
                        <textarea id="markdownContent" name="markdownContent" required>${blogPost?.markdownContent}</textarea>
                    </div>
                </div>

                <div>
                    <div class="preview-pane">
                        <h3>Live Preview</h3>
                        <div id="preview" class="preview-content markdown-content">
                            <p style="color: #95a5a6; font-style: italic;">Start typing to see preview...</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div style="margin-top: 2rem;">
                <button type="submit" class="btn btn-success">Create Post</button>
                <g:link action="index" class="btn btn-secondary">Cancel</g:link>
            </div>
        </g:form>
    </div>
    
    <div class="card">
        <h3>📘 Markdown Quick Reference</h3>
        <div class="editor-container" style="font-family: monospace; font-size: 0.9rem;">
            <div>
                <strong>Headers:</strong><br>
                # H1<br>
                ## H2<br>
                ### H3<br><br>
                
                <strong>Emphasis:</strong><br>
                *italic* or _italic_<br>
                **bold** or __bold__<br>
                ~~strikethrough~~<br><br>
                
                <strong>Lists:</strong><br>
                * Item 1<br>
                * Item 2<br>
                &nbsp;&nbsp;* Nested<br><br>
                
                1. First<br>
                2. Second
            </div>
            <div>
                <strong>Links:</strong><br>
                [Link text](https://url.com)<br><br>
                
                <strong>Code:</strong><br>
                `inline code`<br>
                ```<br>
                code block<br>
                ```<br><br>
                
                <strong>Blockquote:</strong><br>
                > Quote text<br><br>
                
                <strong>Table:</strong><br>
                | Col1 | Col2 |<br>
                |------|------|<br>
                | A    | B    |
            </div>
        </div>
    </div>
    
    <script>
        // Live preview functionality
        const markdownTextarea = document.getElementById('markdownContent');
        const previewDiv = document.getElementById('preview');
        
        let debounceTimer;
        
        markdownTextarea.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(updatePreview, 500);
        });
        
        function updatePreview() {
            const markdown = markdownTextarea.value;
            
            if (!markdown.trim()) {
                previewDiv.innerHTML = '<p style="color: #95a5a6; font-style: italic;">Start typing to see preview...</p>';
                return;
            }
            
            // Make AJAX call to convert markdown to HTML
            fetch('${createLink(action: "preview")}', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'markdown=' + encodeURIComponent(markdown)
            })
            .then(response => response.text())
            .then(html => {
                previewDiv.innerHTML = html;
            })
            .catch(error => {
                console.error('Error:', error);
                previewDiv.innerHTML = '<p style="color: #e74c3c;">Error rendering preview</p>';
            });
        }
        
        // Initial preview if editing
        if (markdownTextarea.value.trim()) {
            updatePreview();
        }
    </script>
</body>
</html>

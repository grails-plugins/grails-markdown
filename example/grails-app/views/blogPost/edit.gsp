<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Edit: ${blogPost?.title}</title>
    <style>
        .editor-container {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }
        
        @media (max-width: 768px) {
            .editor-container {
                grid-template-columns: 1fr;
            }
        }
        
        .editor-pane {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        .editor-pane .form-group {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .editor-pane textarea {
            flex: 1;
            width: 100%;
            box-sizing: border-box;
            min-height: 500px;
            height: 500px;
            resize: vertical;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Courier New', monospace;
            font-size: 14px;
            line-height: 1.6;
            padding: 1rem;
            border: 1px solid #ddd;
            border-radius: 8px;
            background: #fafafa;
        }
        
        .preview-pane {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            border: 1px solid #ddd;
            min-height: 500px;
            height: 500px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }
        
        .preview-pane h3 {
            margin-top: 0;
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
            margin-bottom: 1rem;
            flex-shrink: 0;
        }
        
        .preview-content {
            flex: 1;
            overflow-y: auto;
        }
        
        .help-text {
            font-size: 0.9rem;
            color: #7f8c8d;
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="card">
        <h1>Edit Blog Post</h1>
        
        <g:hasErrors bean="${blogPost}">
            <div class="alert alert-error">
                <ul>
                    <g:eachError bean="${blogPost}" var="error">
                        <li><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </div>
        </g:hasErrors>
        
        <g:form action="update" method="PUT">
            <g:hiddenField name="id" value="${blogPost?.id}"/>
            <g:hiddenField name="version" value="${blogPost?.version}"/>
            
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
                Edit your content using Markdown syntax.
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
                            <p style="color: #95a5a6; font-style: italic;">Loading preview...</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div style="margin-top: 2rem;">
                <button type="submit" class="btn btn-success">Update Post</button>
                <g:link action="show" id="${blogPost?.id}" class="btn btn-secondary">Cancel</g:link>
                <g:link action="index" class="btn btn-secondary">Back to List</g:link>
            </div>
        </g:form>
    </div>
    
    <div class="card">
        <h3>🔄 Convert HTML to Markdown</h3>
        <p>If you have HTML content you want to convert to Markdown, paste it here:</p>
        <div class="form-group">
            <textarea id="htmlInput" placeholder="Paste HTML here..." style="min-height: 150px;"></textarea>
        </div>
        <button type="button" class="btn" onclick="convertHtmlToMarkdown()">Convert to Markdown</button>
        <button type="button" class="btn btn-secondary" onclick="appendToEditor()">Append to Editor</button>
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
            
            // AJAX call to convert markdown to HTML
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
        
        // HTML to Markdown conversion
        function convertHtmlToMarkdown() {
            const html = document.getElementById('htmlInput').value;
            
            if (!html.trim()) {
                alert('Please paste some HTML content first');
                return;
            }
            
            fetch('${createLink(action: "convertToMarkdown")}', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'html=' + encodeURIComponent(html)
            })
            .then(response => response.text())
            .then(markdown => {
                document.getElementById('htmlInput').value = markdown;
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error converting HTML to Markdown');
            });
        }
        
        function appendToEditor() {
            const converted = document.getElementById('htmlInput').value;
            if (converted.trim()) {
                markdownTextarea.value += '\n\n' + converted;
                updatePreview();
                document.getElementById('htmlInput').value = '';
            }
        }
        
        // Initial preview
        updatePreview();
    </script>
</body>
</html>

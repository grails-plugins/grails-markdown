<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Markdown Blog"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background: #f5f5f5;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }
        
        header {
            background: #2c3e50;
            color: white;
            padding: 1rem 0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        header h1 {
            font-size: 1.8rem;
            font-weight: 600;
        }
        
        header h1 a {
            color: white;
            text-decoration: none;
        }
        
        nav {
            margin-top: 1rem;
        }
        
        nav a {
            color: #ecf0f1;
            text-decoration: none;
            margin-right: 1.5rem;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            transition: background 0.3s;
        }
        
        nav a:hover {
            background: rgba(255,255,255,0.1);
        }
        
        main {
            min-height: calc(100vh - 200px);
            padding: 2rem 0;
        }
        
        footer {
            background: #34495e;
            color: #ecf0f1;
            text-align: center;
            padding: 2rem 0;
            margin-top: 2rem;
        }
        
        .card {
            background: white;
            border-radius: 8px;
            padding: 2rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .btn {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            background: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 1rem;
            transition: background 0.3s;
        }
        
        .btn:hover {
            background: #2980b9;
        }
        
        .btn-success {
            background: #27ae60;
        }
        
        .btn-success:hover {
            background: #229954;
        }
        
        .btn-danger {
            background: #e74c3c;
        }
        
        .btn-danger:hover {
            background: #c0392b;
        }
        
        .btn-secondary {
            background: #95a5a6;
        }
        
        .btn-secondary:hover {
            background: #7f8c8d;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #2c3e50;
        }
        
        input[type="text"],
        textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: inherit;
            font-size: 1rem;
        }
        
        textarea {
            min-height: 300px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        }
        
        .alert {
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1.5rem;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        /* Markdown content styling */
        .markdown-content {
            line-height: 1.8;
        }
        
        .markdown-content h1,
        .markdown-content h2,
        .markdown-content h3,
        .markdown-content h4 {
            margin: 1.5rem 0 1rem;
            color: #2c3e50;
        }
        
        .markdown-content h1 {
            font-size: 2rem;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
        }
        
        .markdown-content h2 {
            font-size: 1.6rem;
        }
        
        .markdown-content h3 {
            font-size: 1.3rem;
        }
        
        .markdown-content p {
            margin-bottom: 1rem;
        }
        
        .markdown-content code {
            background: #f4f4f4;
            padding: 0.2rem 0.4rem;
            border-radius: 3px;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 0.9em;
        }
        
        .markdown-content pre {
            background: #2d2d2d;
            color: #f8f8f2;
            padding: 1rem;
            border-radius: 4px;
            overflow-x: auto;
            margin: 1rem 0;
        }
        
        .markdown-content pre code {
            background: none;
            color: inherit;
            padding: 0;
        }
        
        .markdown-content blockquote {
            border-left: 4px solid #3498db;
            padding-left: 1rem;
            margin: 1rem 0;
            color: #666;
            font-style: italic;
        }
        
        .markdown-content table {
            width: 100%;
            border-collapse: collapse;
            margin: 1rem 0;
        }
        
        .markdown-content table th,
        .markdown-content table td {
            padding: 0.75rem;
            border: 1px solid #ddd;
            text-align: left;
        }
        
        .markdown-content table th {
            background: #f8f9fa;
            font-weight: 600;
        }
        
        .markdown-content ul,
        .markdown-content ol {
            margin-left: 2rem;
            margin-bottom: 1rem;
        }
        
        .markdown-content a {
            color: #3498db;
            text-decoration: none;
        }
        
        .markdown-content a:hover {
            text-decoration: underline;
        }
        
        .meta {
            color: #7f8c8d;
            font-size: 0.9rem;
            margin-bottom: 1rem;
        }
        
        .tags {
            margin-top: 1rem;
        }
        
        .tag {
            display: inline-block;
            background: #ecf0f1;
            color: #2c3e50;
            padding: 0.25rem 0.75rem;
            border-radius: 12px;
            font-size: 0.85rem;
            margin-right: 0.5rem;
        }
    </style>
    
    <g:layoutHead/>
</head>
<body>
    <header>
        <div class="container">
            <h1><g:link uri="/">📝 Grails Markdown Blog</g:link></h1>
            <nav>
                <g:link uri="/">Home</g:link>
                <g:link controller="blogPost" action="index">All Posts</g:link>
                <g:link controller="blogPost" action="create">New Post</g:link>
            </nav>
        </div>
    </header>
    
    <main>
        <div class="container">
            <g:if test="${flash.message}">
                <div class="alert alert-success" role="alert">${flash.message}</div>
            </g:if>
            <g:layoutBody/>
        </div>
    </main>
    
    <footer>
        <div class="container">
            <p>&copy; 2026 Grails Markdown Blog - Powered by Grails & grails-markdown plugin</p>
            <p>Demo app to show and test usage of the Grails Markdown Plugin</p>
        </div>
    </footer>
</body>
</html>

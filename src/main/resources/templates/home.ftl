<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<#-- @ftlvariable name="currentUser" type="com.example.domain.CurrentUser" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Home page</title>
    <!-- jQuery -->
    <script src="webjars/jquery/jquery.min.js"></script>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="webjars/bootstrap/css/bootstrap.min.css">
    <script src="webjars/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<nav class="navbar navbar-light" style="background-color: #e3f2fd;">
    <a class="navbar-brand" href="/">Home</a>
    <ul class="navbar-nav">
    <#if !currentUser??>
        <li class="nav-item"><a class="nav-link" href="/login">Log in</a></li>
    </#if>
    <#if currentUser??>
        <li class="nav-item">
            <form action="/logout" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit">Log out</button>
            </form>
        </li>
        <li class="nav-item"><a class="nav-link" href="/user/${currentUser.id}">View myself</a></li>
    </#if>
    <#if currentUser?? && currentUser.role == "ADMIN">
        <li class="nav-item"><a class="nav-link" href="/user/create">Create a new user</a></li>
        <li class="nav-item"><a class="nav-link" href="/users">View all users</a></li>
    </#if>
    </ul>
</nav>
</body>
</html>
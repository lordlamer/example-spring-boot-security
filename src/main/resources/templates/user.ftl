<#-- @ftlvariable name="user" type="com.example.domain.User" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>User details</title>
    <!-- jQuery -->
    <script src="webjars/jquery/jquery.min.js"></script>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="webjars/bootstrap/css/bootstrap.min.css">
    <script src="webjars/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<nav role="navigation">
    <ul>
        <li><a href="/">Home</a></li>
    </ul>
</nav>

<h1>User details</h1>

<p>E-mail: ${user.email}</p>

<p>Role: ${user.role}</p>
</body>
</html>
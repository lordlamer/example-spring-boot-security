<#-- @ftlvariable name="_csrf" type="org.springframework.security.web.csrf.CsrfToken" -->
<#-- @ftlvariable name="error" type="java.util.Optional<String>" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Log in</title>

    <!-- jQuery -->
    <script src="webjars/jquery/jquery.min.js"></script>

    <!-- Bootstrap -->
    <link rel="stylesheet" href="webjars/bootstrap/css/bootstrap.min.css">
    <script src="webjars/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<!-- As a link -->
<nav class="navbar navbar-light" style="background-color: #e3f2fd;">
    <a class="navbar-brand" href="/">Home</a>
</nav>

<#if error.isPresent()>
<div class="alert alert-warning" role="alert">
    <p>The email or password you have entered is invalid, try again.</p>
</div>
</#if>

<div class="container" style="margin-top:40px;">
    <div class="row">

        <div class="card card-outline-info">
            <form class="form-signin" action="/login" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="card-block">
                    <h1 class="card-title">Log in</h1>

                    <p class="card-subtitle mb-2 text-muted">You can use: demo@localhost / demo</p>

                    <label for="inputEmail" class="sr-only">Email address</label>
                    <input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email address" required autofocus>
                    <label for="inputPassword" class="sr-only">Password</label>
                    <input type="password" id="inputPassword" name="password" class="form-control" placeholder="Password" required>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value="remember-me" name="remember-me"> Remember me
                        </label>
                    </div>
                    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>

                </div>
            </form>
        </div>

    </div>
</div> <!-- /container -->

</body>
</html>
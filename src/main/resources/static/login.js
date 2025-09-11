<!DOCTYPE html>
<html lang="es">
    <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema Contable - Login</title>
<link rel="stylesheet" href="/style.css">
</head>
<body>
<div class="login-container">
    <h2>Sistema Contable</h2>
    <form method="post" action="/login">
        <div class="input-group user-input">
            <input type="text" id="usuario" name="usuario" placeholder="Usuario" required/>
        </div>
        <div class="input-group password-input">
            <input type="password" id="password" name="password" placeholder="Password" required/>
        </div>
        <button type="submit" class="btn-login">Entrar</button>
    </form>
</div>
</body>
</html>

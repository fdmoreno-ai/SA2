document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");

    form.addEventListener("submit", async (e) => {
        e.preventDefault(); // Evita que la página se recargue

        const usuario = document.getElementById("usuario").value;
        const password = document.getElementById("password").value;

        try {
            const res = await fetch("http://localhost:8080/unnamed/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ usuario, password })
            });

            const data = await res.json();

            if (data.success) {
                alert("Login exitoso. Bienvenido " + usuario);
                // Podés redirigir a otra página después:
                // window.location.href = "dashboard.html";
            } else {
                alert("Usuario o contraseña incorrectos");
            }

        } catch (err) {
            console.error(err);
            alert("Error al conectar con el backend");
        }
    });
});
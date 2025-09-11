document.addEventListener("DOMContentLoaded", () => {

    // Función para mostrar mensajes
    function mostrarMensaje(texto, tipo) {
        const mensajeDiv = document.getElementById('mensaje');
        mensajeDiv.textContent = texto;

        if(tipo === 'exito') {
            mensajeDiv.style.backgroundColor = '#d4edda';
            mensajeDiv.style.color = '#155724';
            mensajeDiv.style.border = '1px solid #c3e6cb';
        } else {
            mensajeDiv.style.backgroundColor = '#f8d7da';
            mensajeDiv.style.color = '#721c24';
            mensajeDiv.style.border = '1px solid #f5c6cb';
        }

        // Mostrar mensaje con transición
        mensajeDiv.style.display = 'block';
        mensajeDiv.style.opacity = '1';

        setTimeout(() => {
            mensajeDiv.style.opacity = '0';
            setTimeout(() => { mensajeDiv.style.display = 'none'; }, 500); // espera que termine la transición
        }, 3000);
    }


    // Función para actualizar la tabla de cuentas
    async function actualizarTabla() {
        const res = await fetch("/plan-cuentas/lista");
        const cuentas = await res.json();
        const tbody = document.querySelector("table tbody");
        tbody.innerHTML = ""; // limpiar tabla

        cuentas.forEach(cuenta => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${cuenta.codigo}</td>
                <td>${cuenta.nombre}</td>
                <td>${cuenta.tipo}</td>
                <td>${cuenta.saldo}</td>
                <td>
                    <button class="btn-eliminar" data-codigo="${cuenta.codigo}">Eliminar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Agregar listeners a los botones eliminar
        document.querySelectorAll(".btn-eliminar").forEach(btn => {
            btn.addEventListener("click", async () => {
                const codigo = btn.getAttribute("data-codigo");

                const res = await fetch("/plan-cuentas/eliminar", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `codigo=${codigo}`
                });

                const result = await res.json();

                if(result.exito) {
                    mostrarMensaje(result.mensaje, "exito");
                    actualizarTabla(); // refresca la tabla automáticamente
                } else {
                    mostrarMensaje(result.mensaje, "error");
                }
            });
        });
    }

    // Capturar submit del formulario de agregar cuentas
    const form = document.getElementById("form-agregar-cuenta");
    form.addEventListener("submit", async (e) => {
        e.preventDefault(); // evita recargar la página

        const formData = new FormData(form);
        const codigo = formData.get("codigo");
        const nombre = formData.get("nombre");
        const tipo = formData.get("tipo");

        const res = await fetch("/plan-cuentas/agregar", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `codigo=${codigo}&nombre=${nombre}&tipo=${tipo}`
        });

        const result = await res.json();

        if(result.exito) {
            form.reset();        // limpiar campos
            mostrarMensaje(result.mensaje, "exito");
            actualizarTabla();   // actualizar tabla automáticamente
        } else {
            mostrarMensaje(result.mensaje, "error");
        }
    });

    // Carga inicial de la tabla
    actualizarTabla();
});

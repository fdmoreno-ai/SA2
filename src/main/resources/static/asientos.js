document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("form-agregar-asiento");
    const tablaAsientos = document.getElementById("tabla-asientos");
    const mensajeDiv = document.getElementById("mensaje");

    function mostrarMensaje(texto, tipo) {
        mensajeDiv.textContent = texto;
        mensajeDiv.style.display = "block";

        if(tipo === "exito") {
            mensajeDiv.style.backgroundColor = "#d4edda";
            mensajeDiv.style.color = "#155724";
            mensajeDiv.style.border = "1px solid #c3e6cb";
        } else {
            mensajeDiv.style.backgroundColor = "#f8d7da";
            mensajeDiv.style.color = "#721c24";
            mensajeDiv.style.border = "1px solid #f5c6cb";
        }

        setTimeout(() => {
            mensajeDiv.style.display = "none";
        }, 3000);
    }

    // Función para reconstruir la tabla de asientos desde JSON
    async function actualizarTabla() {
        const res = await fetch("/asientos/lista");
        const asientos = await res.json();

        let html = `
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Concepto</th>
                    <th>Cuenta</th>
                    <th>Debe</th>
                    <th>Haber</th>
                </tr>
            </thead>
            <tbody>
        `;

        asientos.forEach(asiento => {
            asiento.detalles.forEach(detalle => {
                html += `
                    <tr>
                        <td>${asiento.fecha}</td>
                        <td>${asiento.concepto}</td>
                        <td>${detalle.cuenta.nombre}</td>
                        <td>${detalle.debe}</td>
                        <td>${detalle.haber}</td>
                    </tr>
                `;
            });
        });

        html += "</tbody>";
        tablaAsientos.innerHTML = html;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const concepto = form.concepto.value;
        const cuenta = form.querySelector("select[name='cuentas']").value;
        const debe = parseFloat(form.querySelector("input[name='debe']").value || 0);
        const haber = parseFloat(form.querySelector("input[name='haber']").value || 0);

        const params = new URLSearchParams();
        params.append("concepto", concepto);
        params.append("cuentas", cuenta);
        params.append("debe", debe);
        params.append("haber", haber);

        const res = await fetch("/asientos/agregar", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString()
        });

        const result = await res.json(); // parsea la respuesta JSON

        mostrarMensaje(result.mensaje, result.exito ? "exito" : "error");

        if(result.exito) {
            form.reset();
        }

        actualizarTabla();
    });

    // Carga inicial de la tabla
    actualizarTabla();
});

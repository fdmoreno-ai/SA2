document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-agregar-asiento");
    const tablaAsientos = document.getElementById("tabla-asientos");
    const mensajeDiv = document.getElementById("mensaje");
    const btnAgregarLinea = document.getElementById("btn-agregar-linea");
    const contenedorLineas = document.getElementById("detalle-asientos");

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
        setTimeout(() => mensajeDiv.style.display = "none", 3000);
    }

    // Función para agregar una nueva línea al formulario
    function agregarLinea() {
        const linea = document.createElement("div");
        linea.className = "linea-detalle";
        linea.innerHTML = `
            <select name="cuentas" required>
                <option th:each="c : ${cuentas}" th:value="${c.codigo}" th:text="${c.nombre}"></option>
            </select>
            <input type="number" name="debe" placeholder="Debe" step="0.01" value="0">
            <input type="number" name="haber" placeholder="Haber" step="0.01" value="0">
            <button type="button" class="btn-eliminar-linea">X</button>
        `;
        contenedorLineas.appendChild(linea);

        // Botón eliminar línea
        linea.querySelector(".btn-eliminar-linea").addEventListener("click", () => linea.remove());
    }

    btnAgregarLinea.addEventListener("click", agregarLinea);

    // Actualizar tabla de asientos desde backend
    async function actualizarTabla() {
        const res = await fetch("/asientos/lista");
        const asientos = await res.json();
        let html = "";
        asientos.forEach(a => {
            a.detalles.forEach(d => {
                html += `<tr>
                    <td>${a.fecha}</td>
                    <td>${a.concepto}</td>
                    <td>${d.cuenta.nombre}</td>
                    <td>${d.debe}</td>
                    <td>${d.haber}</td>
                </tr>`;
            });
        });
        tablaAsientos.innerHTML = html;
    }

    // Validación que Debe = Haber
    function validarBalance(lineas) {
        let totalDebe = 0;
        let totalHaber = 0;
        lineas.forEach(l => {
            totalDebe += parseFloat(l.querySelector("input[name='debe']").value || 0);
            totalHaber += parseFloat(l.querySelector("input[name='haber']").value || 0);
        });
        return totalDebe === totalHaber;
    }

    // Enviar formulario
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const concepto = form.concepto.value;
        const lineas = Array.from(contenedorLineas.querySelectorAll(".linea-detalle"));

        if(!validarBalance(lineas)) {
            mostrarMensaje("Error: Debe y Haber no están balanceados", "error");
            return;
        }

        const detalles = lineas.map(l => ({
            cuenta: l.querySelector("select[name='cuentas']").value,
            debe: parseFloat(l.querySelector("input[name='debe']").value || 0),
            haber: parseFloat(l.querySelector("input[name='haber']").value || 0)
        }));

        const res = await fetch("/asientos/agregar", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ concepto, detalles })
        });

        const result = await res.json();
        mostrarMensaje(result.mensaje, result.exito ? "exito" : "error");
        if(result.exito) form.reset();
        actualizarTabla();
    });

    actualizarTabla(); // carga inicial
});

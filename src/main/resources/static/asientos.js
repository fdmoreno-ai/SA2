document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("form-agregar-asiento");
    const tablaAsientos = document.getElementById("tabla-asientos");
    const mensajeDiv = document.getElementById("mensaje");
    const btnAgregarLinea = document.getElementById("btn-agregar-linea");
    const contenedorLineas = document.getElementById("detalle-asientos");

    // Parseamos las cuentas desde el data-attribute
    const cuentas = JSON.parse(form.dataset.cuentas || "[]");

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

    function agregarLinea(cuentaCodigo = "", debe = 0, haber = 0) {
        const linea = document.createElement("div");
        linea.className = "linea-detalle";

        // Select con todas las cuentas
        const select = document.createElement("select");
        select.name = "cuentas";
        select.required = true;

        cuentas.forEach(c => {
            const option = document.createElement("option");
            option.value = c.codigo;
            option.textContent = c.nombre;
            if(c.codigo === cuentaCodigo) option.selected = true;
            select.appendChild(option);
        });

        const inputDebe = document.createElement("input");
        inputDebe.type = "number";
        inputDebe.name = "debe";
        inputDebe.placeholder = "Debe";
        inputDebe.step = "0.01";
        inputDebe.value = debe;

        const inputHaber = document.createElement("input");
        inputHaber.type = "number";
        inputHaber.name = "haber";
        inputHaber.placeholder = "Haber";
        inputHaber.step = "0.01";
        inputHaber.value = haber;

        const btnEliminar = document.createElement("button");
        btnEliminar.type = "button";
        btnEliminar.className = "btn-eliminar-linea";
        btnEliminar.textContent = "X";
        btnEliminar.addEventListener("click", () => linea.remove());

        linea.appendChild(select);
        linea.appendChild(inputDebe);
        linea.appendChild(inputHaber);
        linea.appendChild(btnEliminar);
        contenedorLineas.appendChild(linea);
    }

    btnAgregarLinea.addEventListener("click", () => agregarLinea());

    function validarBalance(lineas) {
        let totalDebe = 0;
        let totalHaber = 0;
        lineas.forEach(l => {
            totalDebe += parseFloat(l.querySelector("input[name='debe']").value || 0);
            totalHaber += parseFloat(l.querySelector("input[name='haber']").value || 0);
        });
        return totalDebe === totalHaber;
    }

    // Función actualizada para que la tabla se muestre correctamente
    async function actualizarTabla() {
        const res = await fetch("/asientos/lista");
        const asientos = await res.json();
        let html = "";

        asientos.forEach(a => {
            const fecha = new Date(a.fecha).toLocaleDateString();
            a.detalles.forEach(d => {
                const nombreCuenta = d.cuenta.nombre || d.cuenta;
                html += `<tr>
                    <td>${fecha}</td>
                    <td>${a.concepto}</td>
                    <td>${nombreCuenta}</td>
                    <td>${d.debe}</td>
                    <td>${d.haber}</td>
                </tr>`;
            });
        });

        tablaAsientos.innerHTML = html;
    }

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

        if(result.exito) {
            form.reset();
            contenedorLineas.innerHTML = "";
            agregarLinea();
            await actualizarTabla(); // Esperamos que la tabla se actualice
        }
    });

    // Inicializamos con una línea vacía y cargamos la tabla
    agregarLinea();
    actualizarTabla();
});

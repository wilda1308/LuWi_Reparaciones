
const BADGES = {
    PUBLICADA: "bg-warning text-dark",
    ASIGNADA: "bg-success",
    CANCELADA: "bg-secondary",
    FINALIZADA: "bg-dark",
    PENDIENTE: "bg-warning text-dark",
    ACEPTADA: "bg-success",
    RECHAZADA: "bg-danger",
    RETIRADA: "bg-secondary"
}

const usuario = sessionStorage.exigirRol("CLIENTE");

const formReparacion = document.getElementById("formReparacion");
const alertaForm = document.getElementById("alertaForm");
const alertaLista = document.getElementById("alertaLista");
const listaReparaciones = document.getElementById("listaReparaciones");
const botonPublicar = formReparacion.querySelector("button[type=submit]");

function iniciar(){
    document.getElementById("nombreUsuario").textContent = usuario.nombre;
    document.getElementById("ciudad").value = usuario.ciudad;

    document.getElementById("btnSalir").addEventListener("click", () => Sesion.cerrar());
    document.getElementById("btnActualizar").addEventListener("click", cargarReparaciones);
    formReparacion.addEventListener("submit", publicarReparacion);
    listaReparaciones.addEventListener("click", manejarClicLista);

    cargarReparaciones();
}

function numeroOpcional(valor){
    return valor === "" ? null : Number(valor);
}

async function publicarReparacion(evento) {
    evento.preventDefault();
    alertaForm.innerHTML = "";

    if(!formReparacion.checkValidity()){
        formReparacion.classList.add("was-validated");
        return;
    }

    const dataF = new FormData(formReparacion);
    const minimo = numeroOpcional(dataF.get("presupuestoMinimo"));
    const maximo = numeroOpcional(dataF.get("presupuestoMaximo"));

    if(minimo != null && maximo !== null && minimo > maximo){
        mostrarAlerta(alertaForm, "El presupuesto mínimo no puede superar al máximo");
        return;
    }

    const datos = {
        titulo: dataF.get("titulo").trim(),
        descripcion: dataF.get("descripcion").trim(),
        categoria: dataF.get("categoria").trim(),
        marcaObjeto: dataF.get("marcaObjeto").trim() || null,
        presupuestoMinimo: minimo,
        presupuestoMaximo: maximo,
        ciudad: dataF.get("ciudad").trim(),
        zona: dataF.get("zona").trim(),
    };

    botonPublicar.ariaDisabled = true;
    botonPublicar.textContent = "Publicando...";
    try{
        await apiFetch("/api/reparaciones",{
            method: "POST",
            body: JSON.stringify(datos)
        });

        formReparacion.reset();
        formReparacion.classList.remove("was-validated");

        document.getElementById("ciudad").value = usuario.ciudad;

        mostrarAlerta(alertaForm, "Reparación publicada correctamente", "success");
        cargarReparaciones();
    } catch (Error){
        mostrarAlerta(alertaForm, error.message);
    } finally{
        botonPublicar.disabled = false;
        botonPublicar.textContent = "Publicar reparación";
    }
}

function textoPresupuesto(min, max){
    if(min != null && max != null) return `${formatearPesos(min)} a ${formatearPesos(max)}`;

    if(min != null) return `Desde $`
}
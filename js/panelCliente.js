
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

const usuario = Sesion.exigirRol("CLIENTE");

const formReparacion = document.getElementById("formReparacion");
const alertaForm = document.getElementById("alertaForm");
const alertaLista = document.getElementById("alertaLista");
const listaReparaciones = document.getElementById("listaReparaciones");
const botonPublicar = formReparacion.querySelector("button[type=submit]");
const modalReparacion = new bootstrap.Modal(document.getElementById("modalReparacion"));

function iniciar(){
    document.getElementById("nombreUsuario").textContent = usuario.nombre;

    document.getElementById("btnSalir").addEventListener("click", () => Sesion.cerrar());
    document.getElementById("btnActualizar").addEventListener("click", cargarReparaciones);
    document.getElementById("btnNuevaReparacion").addEventListener("click", abrirModalReparacion);
    formReparacion.addEventListener("submit", publicarReparacion);
    listaReparaciones.addEventListener("click", manejarClicLista);

    cargarReparaciones();
}

/* ----------------- Modal: Publicar una nueva reparación ---------------- */
function abrirModalReparacion(){
    //Cada vez que se abre, el formulario empieza limpio
    formReparacion.reset();
    formReparacion.classList.remove("was-validated");
    alertaForm.innerHTML = "";
    document.getElementById("ciudad").value = usuario.ciudad;

    modalReparacion.show();
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

    if(minimo != null && maximo != null && minimo > maximo){
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

    botonPublicar.disabled = true;
    botonPublicar.textContent = "Publicando...";
    try{
        await apiFetch("/api/reparaciones",{
            method: "POST",
            body: JSON.stringify(datos)
        });

        modalReparacion.hide();

        //Primero cargamos la lista y al final mostramos el aviso
        //(cargarReparaciones() limpia la alerta al empezar)
        await cargarReparaciones();
        mostrarAlerta(alertaLista, "Reparación publicada correctamente", "success");
    } catch (error){
        mostrarAlerta(alertaForm, error.message);
    } finally{
        botonPublicar.disabled = false;
        botonPublicar.textContent = "Publicar reparación";
    }
}

function textoPresupuesto(min, max){
    if(min != null && max != null) return `${formatearPesos(min)} a ${formatearPesos(max)}`;

    if(min != null) return `Desde ${formatearPesos(min)}`;
    if(max != null) return `Hasta ${formatearPesos(max)}`;
    return "No definido";
}

function crearTarjetaReparacion(r){
    const marca = r.marcaObjeto ? ` · ${escaparHtml(r.marcaObjeto)}` : "";

    return `
    <div class = "card shadow-sm border-0 mb-3">
        <div class = "card-body">
            <div class = "d-flex justify-content-between align-items-start gap-2">
                <h3 class = "h5 mb-1">${escaparHtml(r.titulo)}</h3>
                <span class = "badge ${BADGES[r.estado]}">${r.estado}</span>
            </div>
            <p class = "text-secondary small mb-2">
                ${escaparHtml(r.categoria)}${marca} · ${escaparHtml(r.zona)}, ${escaparHtml(r.ciudad)} · ${formatearFecha(r.fechaPublicacion)}
            </p>
            <p class = "mb-2">${escaparHtml(r.descripcion)}</p>
            <p class = "mb-3"><strong>Presupuesto:</strong> ${textoPresupuesto(r.presupuestoMinimo, r.presupuestoMaximo)}</p>

            <button class = "btn btn-outline-dark btn-sm"
                data-accion = "ver-ofertas" 
                data-id="${r.idReparacion}"
                data-estado="${r.estado}">
                <i class = "bi bi-chat-dots"></i> Ver ofertas
            </button>

            <div class = "mt-3 d-none" id = "ofertas-${r.idReparacion}"></div>
        </div>
    </div>`;
}

async function cargarReparaciones() {
    alertaLista.innerHTML = "";
    listaReparaciones.innerHTML = `<p class = "text-secondary">Cargando...</p>`;

    try{
        const reparaciones = await apiFetch("/api/reparaciones/mis-reparaciones");

        if(reparaciones.length === 0){
            listaReparaciones.innerHTML = `<div class = "text-center text-secondary py-5">Aún no has publicado ninguna reparación. Usa el botón <strong>Publicar reparación</strong> para crear una reparación.</div>`;
            return;
        }

        reparaciones.sort((a, b) => new Date(b.fechaPublicacion) - new Date(a.fechaPublicacion));
        listaReparaciones.innerHTML = reparaciones.map(crearTarjetaReparacion).join("");
    } catch (error){
        listaReparaciones.innerHTML = "";
        mostrarAlerta(alertaLista, error.message);
    }
}

function crearTarjetaOferta(o, estadoReparacion){
    const puedeAceptar = estadoReparacion === "PUBLICADA" && o.estado === "PENDIENTE";

    return `
    <div class = "border rounded p-3 mb-2 bg-white">
        <div class = "d-flex justify-content-between align-items-start gap-2">
            <div>
                <strong>${escaparHtml(o.nombreReparador)}</strong>
                <span class = "badge ${BADGES[o.estado]} ms-1">${o.estado}</span>
            </div>
            <span class = "fw-bold textNaranjas"> ${formatearPesos(o.precioPropuesto)}</span>
        </div>
        <p class = "mb-1 mt-2">${escaparHtml(o.descripcion)}</p>
        <p class = "text-secondary small mb-2">
            <i class = "bi bi-clock"></i> ${escaparHtml(o.tiempoEstimado)} · ${formatearFecha(o.fechaOferta)}
        </p>
        ${puedeAceptar ? `<button class = "btn btn-primary btn-sm" data-accion = "aceptar" data-id = "${o.idOferta}">Aceptar oferta</button>` : ""}
    </div>`
}

async function alternarOfertas(idReparacion, estadoReparacion) {
    const contenedor = document.getElementById("ofertas-" + idReparacion);

    //Si ya está abierto, se cierra
    if(!contenedor.classList.contains("d-none")){
        contenedor.classList.add("d-none");
        return;
    }

    contenedor.classList.remove("d-none");
    contenedor.innerHTML = `<p class = "text-secondary small mb-0">Cargando ofertas...</p>`;

    try{
        const ofertas = await apiFetch(`/api/reparaciones/${idReparacion}/ofertas`);

        if(ofertas.length === 0){
            contenedor.innerHTML = `<p class = "text-secondary small mb-0">Todavía no hay ofertas.</p>`;
            return;
        }

        contenedor.innerHTML = ofertas.map(oferta => crearTarjetaOferta(oferta, estadoReparacion)).join("");
    } catch (error){
        contenedor.innerHTML = "";
        mostrarAlerta(contenedor, error.message);
    }
}

async function aceptarOferta(idOferta, boton) {
    if(!confirm("¿Aceptar esta oferta? Las demás ofertas serán rechazadas")) return;

    boton.disabled = true;

    try{
        await apiFetch(`/api/ofertas/${idOferta}/aceptar`, {method: "PUT"});
        await cargarReparaciones();
        mostrarAlerta(alertaLista, "Oferta aceptada. La reparación quedó asignada.", "success");
    } catch (error){
        boton.disabled = false;
        mostrarAlerta(alertaLista, error.message);
    }
}

function manejarClicLista(evento){
    const boton = evento.target.closest("button[data-accion]");
    if(!boton) return;

    const id = boton.dataset.id;

    if(boton.dataset.accion === "ver-ofertas"){
        alternarOfertas(id, boton.dataset.estado);
    } else if(boton.dataset.accion === "aceptar"){
        aceptarOferta(id, boton);
    }
}

if(usuario){
    iniciar();
}
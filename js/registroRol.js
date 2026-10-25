// Lee el párametro "...?rol=CLIENTE" o "...?rol=REPARADOR" de la dirección y deja marcada la opción correspondiente en el formulario de registro

const RADIOS_POR_ROL = {
    CLIENTE: "rolCliente",
    REPARADOR: "rolReparador"
};

const rolPedido = (new URLSearchParams(window.location.search).get("rol") || "").toUpperCase();

//Solo se aceptan los roles de la lista, cualquier otro valor se ignora.
if(RADIOS_POR_ROL[rolPedido]){
    document.getElementById(RADIOS_POR_ROL[rolPedido]).checked = true;
z}
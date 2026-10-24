const form = document.getElementById("formRegistro");
const alerta = document.getElementById("alerta");
const boton = form.querySelector("button[type=submit]");

form.addEventListener("submit", async(evento) =>{
    //Quitamos las advertencias por defecto de los input
    evento.preventDefault();
    //Seteamos o actualizamos el mensaje de error a vacío
    alerta.innerHTML = "";

    if(!form.checkValidity()){
        form.classList.add("was-validated");
        return;
    }

    const f = new FormData(form);
    const datos = {
        nombre: f.get("nombre").trim(),
        correo: f.get("correo").trim(),
        contrasena: f.get("contrasena"),
        telefono: f.get("telefono").trim(),
        rol: f.get("rol"),
        ciudad: f.get("ciudad").trim(),
        direccionReferencia: f.get("direccionReferencia").trim()
    };

    boton.disabled = true;
    boton.textContent = "Registrando...";

    try{
        await apiFetch("/api/usuarios/registro",{
            method: "POST",
            body: JSON.stringify(datos),
            auth: false
        });
        window.location.href = "/pages/login.html?registrado=1";
    } catch(error){
        mostrarAlerta(alerta, error.message);
        boton.disabled = false;
        boton.textContent = "Registrarme";
    }
});
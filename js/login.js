const form = document.getElementById("formLogin");
const alerta = document.getElementById("alerta");
const boton = document.querySelector("button[type=submit]");

// Si ya hay una sesión iniciada, ir directo al panel
if(Sesion.token() && Sesion.usuario()){
    Sesion.irAlPanel();
}

// Mensaje cuando viene desde el registro
if(new URLSearchParams(window.location.search).get("registrado") === "1"){
    mostrarAlerta(alerta, "Cuenta creada. Ahora puedes iniciar sesión.", "success");
}

form.addEventListener("submit", async(evento) => {
    evento.preventDefault();

    if(!form.checkValidity()){
        form.classList.add("was-validated");
        return;
    }

    const dataForm = new FormData(form)

    boton.disabled = true;
    boton.textContent = "Entrando...";

    try{
        const respuesta = await apiFetch("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({
                correo: dataForm.get("correo").trim(),
                contrasena: dataForm.get("contrasena")
            }),
            auth: false
        });

        console.log("Respuesta del login: ", respuesta);
        
        Sesion.guardar(respuesta);
        Sesion.irAlPanel();
    } catch (error){
        mostrarAlerta(alerta, error.message);
        boton.disabled = false;
        boton.textContent = "Entrar";
    }
});
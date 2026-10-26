//Cada botón con la clase "btn-ver-password" controla el campo cuyo id está en su atributo data-objetivo.
document.querySelectorAll(".btn-ver-password").forEach((boton) => {
    const campo = document.getElementById(boton.dataset.objetivo);
    if(!campo) return;

    const icono = boton.querySelector("i");

    boton.addEventListener("click", () =>{
        const mostrar = campo.type === "password";

        campo.type = mostrar ? "text" : "password";
        icono.className = mostrar ? "bi bi-eye-slash" : "bi bi-eye";

        const texto = mostrar ? "Ocultar contraseña" : "Mostrar contraseña";

        boton.setAttribute("aria-label", texto);
        boton.setAttribute("title", texto);

        campo.focus(); //el cursor vuelve al campo para seguir escribiendo
    });
});
package com.proyecto.sa2.controllers;

import com.proyecto.sa2.models.Usuario;
import com.proyecto.sa2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        Usuario u = usuarioService.autenticar(usuario, password);

        if (u != null) {
            session.setAttribute("usuarioLogueado", u);
            return "redirect:/dashboard"; // redirige al dashboard
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login"; // vuelve a login con mensaje
        }
    }

    // Mapeo para el dashboard
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Object usuarioLogueado = session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            // Si no hay usuario en sesión, volver al login
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuarioLogueado);
        return "dashboard"; // busca templates/dashboard.html
    }

    // Mapeo para logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // invalida la sesión actual
        session.invalidate();

        // redirige de nuevo al login
        return "redirect:/login";
    }

}

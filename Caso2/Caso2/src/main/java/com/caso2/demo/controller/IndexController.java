package com.caso2.demo.controller;

import com.caso2.demo.domain.Usuario;
import com.caso2.demo.services.UsuarioServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final UsuarioServices usuarioService;

    public IndexController(UsuarioServices usuarioService) {
        this.usuarioService = usuarioService;
    }

    /* ============================================================
       INICIO DEL SISTEMA: SI NO ESTÁ LOGUEADO → LOGIN
       SI ESTÁ LOGUEADO → INDEX
    ============================================================ */
    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Usuario NO autenticado → enviar a login
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "login";
        }

        // Usuario autenticado → cargar info en sesión
        Usuario usuario = usuarioService.getUsuarioByUsername(auth.getName()).orElse(null);

        if (usuario != null) {
            session.setAttribute("idUsuario", usuario.getId());
            session.setAttribute("nombreUsuario", usuario.getNombre());
            session.setAttribute("rolUsuario", usuario.getRol().getNombre());
            session.setAttribute("emailUsuario", usuario.getEmail());
        }

        return "index"; // Vista index.html
    }


    /* ============================================================
       RUTA AL DASHBOARD /index (Evita loops)
    ============================================================ */
    @GetMapping("/index")
    public String dashboard(HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {

            Usuario usuario = usuarioService.getUsuarioByUsername(auth.getName()).orElse(null);

            if (usuario != null) {
                session.setAttribute("idUsuario", usuario.getId());
                session.setAttribute("nombreUsuario", usuario.getNombre());
                session.setAttribute("rolUsuario", usuario.getRol().getNombre());
                session.setAttribute("emailUsuario", usuario.getEmail());
            }
        }

        return "index";
    }
}

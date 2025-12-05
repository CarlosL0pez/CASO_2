package com.caso2.demo.controller;

import com.caso2.demo.domain.Usuario;
import com.caso2.demo.services.UsuarioServices;
import com.caso2.demo.services.RolServices;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServices usuarioService;

    @Autowired
    private RolServices rolService;

    @Autowired
    private MessageSource messageSource;


    /* ============================
            LISTADO DE USUARIOS
       ============================ */
    @GetMapping("/listado")
    public String listado(Model model) {

        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());

        // Necesitamos los roles para el modal de agregar
        var roles = rolService.getRoles();
        model.addAttribute("roles", roles);

        return "/usuario/listado";
    }


    /* ============================
            GUARDAR USUARIO
       ============================ */
    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario,
                          RedirectAttributes redirectAttributes) {

        usuarioService.save(usuario, true);

        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
        );

        return "redirect:/usuario/listado";
    }


    /* ============================
              ELIMINAR USUARIO
       ============================ */
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long id,
                           RedirectAttributes redirectAttributes) {

        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";

        try {
            usuarioService.delete(id);

        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "usuario.error01"; // usuario no existe

        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "usuario.error02"; // datos asociados

        } catch (Exception e) {
            titulo = "error";
            detalle = "usuario.error03"; // error inesperado
        }

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault())
        );

        return "redirect:/usuario/listado";
    }


    /* ============================
            MODIFICAR USUARIO
       ============================ */
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Long id,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(id);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("usuario.error01", null, Locale.getDefault())
            );
            return "redirect:/usuario/listado";
        }

        model.addAttribute("usuario", usuarioOpt.get());

        // lista de roles para el combo en modificar
        var roles = rolService.getRoles();
        model.addAttribute("roles", roles);

        return "/usuario/modifica";
    }
}

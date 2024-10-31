package br.com.breshop.controller.mvc.usuario;

import br.com.breshop.dto.LoginUserDto;
import br.com.breshop.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.service.UsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class MvcUserController {

    private final UsuarioService usuarioService;

    public MvcUserController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login/login";
    }

    @PostMapping("/logar")
    public ResponseEntity<AuthResponseDTO> logarVendedorFromMvc(
            @RequestBody LoginUserDto loginUsuarioDto) {

        if (loginUsuarioDto.senha() == null || loginUsuarioDto.senha().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Senha é obrigatória."));
        }

        try {
            AuthResponseDTO authResponse = usuarioService.loginUsuario(loginUsuarioDto);
            return ResponseEntity.ok(authResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(e.getMessage()));
        }
    }




}

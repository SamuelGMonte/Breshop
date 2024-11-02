package br.com.breshop.controller.mvc.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.breshop.dto.CreateUsuarioDto;
import br.com.breshop.dto.LoginUserDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Usuario;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.service.UsuarioService;

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
        return "login-usuario/login-usuario";
    }

    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro/cadastro";
    }

    @PostMapping("/logarUsuario")
    public ResponseEntity<?> logarUsuarioMvc(
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

    @PostMapping("/cadastrarUsuario")
    public ResponseEntity<String> cadastrarUsuarioFromMvc(
            @ModelAttribute CreateUsuarioDto createUsuarioDto,
            @RequestParam String confirmaSenha,
            BindingResult result) {

        // Verifica se as senhas coincidem
        if (!createUsuarioDto.senha().equals(confirmaSenha)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As senhas não coincidem.");
        }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos.");
        }

        try {
            usuarioService.createUsuario(createUsuarioDto);
            return ResponseEntity.ok("Email enviado com sucesso!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


}

package br.com.breshop.controller.mvc.cadastro;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.breshop.dto.CreateUsuarioDto;
import br.com.breshop.entity.Usuario;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.service.UsuarioService;

@Controller
public class CadastroMvcController {
    private final UsuarioService usuarioService;

    public CadastroMvcController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro/cadastro";
    }

    @PostMapping("/cadastrar") 
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

package br.com.breshop.controller.mvc.vendedor;

import br.com.breshop.dto.*;
import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.BrechoRepository;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.service.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import br.com.breshop.dto.jwt.AuthResponseDTO;

@Controller
@RequestMapping("/vendedores")
public class MvcVendedorController {

    private final VendedorService vendedorService;
    private final UsuarioRepository usuarioRepository;
    private final BrechoRepository brechoRepository;

    @Autowired
    public MvcVendedorController(VendedorService vendedorService, UsuarioRepository usuarioRepository, BrechoRepository brechoRepository) {
        this.vendedorService = vendedorService;
        this.usuarioRepository = usuarioRepository;
        this.brechoRepository = brechoRepository;
    }
    
    @GetMapping("/login")
    public String showLoginVendedor(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "login-vendedor/login-vendedor";
    }

    @PostMapping("/logarVendedor")
    public ResponseEntity<?> logarVendedorFromMvc(
            @RequestBody LoginVendedorDto loginVendedorDto) {

        if (loginVendedorDto.senha() == null || loginVendedorDto.senha().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO("Senha é obrigatória."));
        }

        try {
            AuthResponseDTO authResponse = vendedorService.loginVendedor(loginVendedorDto);
            return ResponseEntity.ok(authResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(e.getMessage()));
        }
    }


    @GetMapping("/cadastro-brecho")
    public String cadastroPage(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        model.addAttribute("brecho", new Brecho());
        return "cadastro-brecho/cadastro-brecho";
    }

    @PostMapping("/cadastrarVendedor")
    public ResponseEntity<String> cadastrarVendedorFromMvc(
            @ModelAttribute CreateVendedorDto createVendedorDto,
            @ModelAttribute CreateBrechoDto createBrechoDto,
            @RequestParam String confirmaSenha,
            BindingResult result) {

        // Verifica se as senhas coincidem
        if (!createVendedorDto.senha().equals(confirmaSenha)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As senhas não coincidem.");
        }

//       Verifica se vendedor/user ja existe
        if(usuarioRepository.findByEmail(createVendedorDto.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email de vendedor já cadastrado como usuário, efetue o login.");
        }


        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos.");
        }

        try {
            vendedorService.createVendedor(createVendedorDto, createBrechoDto);
            return ResponseEntity.ok("Email enviado com sucesso!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


}

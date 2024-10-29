package br.com.breshop.controller.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.service.VendedorService;

@Controller
public class LoginMvcController {

    private final VendedorService vendedorService;

    public LoginMvcController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "login/login"; 
    }

    @PostMapping("/logar")
    public ResponseEntity<AuthResponseDTO> logarVendedorFromMvc(
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

    


}

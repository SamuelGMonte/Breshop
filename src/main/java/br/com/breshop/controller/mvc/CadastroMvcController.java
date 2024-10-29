package br.com.breshop.controller.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.service.VendedorService;

@Controller
public class CadastroMvcController {
    private final VendedorService vendedorService;

    public CadastroMvcController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }


    @GetMapping("/cadastro")
    public String cadastroPage(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "cadastro/cadastro";
    }

    @PostMapping("/cadastrar") 
    public ResponseEntity<String> cadastrarVendedorFromMvc(
            @ModelAttribute CreateVendedorDto createVendedorDto,
            @RequestParam String confirmaSenha,
            BindingResult result) {

        // Verifica se as senhas coincidem
        if (!createVendedorDto.senha().equals(confirmaSenha)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As senhas não coincidem.");
        }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos.");
        }

        try {
            vendedorService.createVendedor(createVendedorDto);
            return ResponseEntity.ok("Cadastro realizado com sucesso!");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

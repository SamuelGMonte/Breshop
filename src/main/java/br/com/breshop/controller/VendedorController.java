package br.com.breshop.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.service.VendedorService;

@RestController
@RequestMapping("/v1/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }


    @PostMapping("/addVendedor")
    public ResponseEntity<String> cadastrarVendedor(
            @RequestBody CreateVendedorDto createVendedorDto,
            BindingResult result) {

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
    

    @GetMapping("/{vendedorId}")
    public ResponseEntity<Vendedor> getVendedorById(@PathVariable("vendedorId") String vendedorId) {
        var vendedor = vendedorService.getVendedorById(UUID.fromString(vendedorId));
        if (vendedor.isPresent()) {
            return ResponseEntity.ok(vendedor.get());
        }
        return ResponseEntity.notFound().build();
    }
}

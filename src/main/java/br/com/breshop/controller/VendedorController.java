package br.com.breshop.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.security.jwt.JWTGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.service.VendedorService;

@RestController
@RequestMapping("api/v1/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public VendedorController(VendedorService vendedorService,
                              AuthenticationManager authenticationManager,
                              JWTGenerator jwtGenerator) {
        this.vendedorService = vendedorService;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginVendedorDto LoginVendedorDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(LoginVendedorDto.email(), LoginVendedorDto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO("success", token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, String>> cadastrarVendedor(
            @RequestBody CreateVendedorDto createVendedorDto,
            BindingResult result) {

        Map<String, String> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Dados inválidos.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ResponseEntity<?> emailValidated = vendedorService.createVendedor(createVendedorDto);
            if (emailValidated.getStatusCode() == HttpStatus.OK) {
                response.put("status", "success");
                response.put("message", "Verifique o email pelo link enviado ao seu endereço de email");
                return ResponseEntity.ok(response);
            }
        } catch (UserAlreadyExistsException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/confirmar-conta")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ResponseEntity<?> response = vendedorService.confirmEmail(UUID.fromString(confirmationToken));

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/cadastro-sucesso"))
                    .build();
        } else {
            return ResponseEntity.badRequest().body("Token inválido.");
        }
    }
}


    // @GetMapping("/{vendedorId}")
    // public ResponseEntity<Vendedor> getVendedorById(@PathVariable("vendedorId") String vendedorId) {
    //     var vendedor = vendedorService.getVendedorById(UUID.fromString(vendedorId));
    //     if (vendedor.isPresent()) {
    //         return ResponseEntity.ok(vendedor.get());
    //     }
    //     return ResponseEntity.notFound().build();
    // }


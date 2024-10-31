package br.com.breshop.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.breshop.dto.CreateUsuarioDto;
import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.security.jwt.JWTGenerator;
import br.com.breshop.service.UsuarioDetailsService;
import br.com.breshop.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.breshop.exception.UserAlreadyExistsException;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService, AuthenticationManager authenticationManager
    ,JWTGenerator jwtGenerator) {
        this.usuarioService = usuarioService;
        this.jwtGenerator = jwtGenerator;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/registrar")
    public ResponseEntity<Map<String, String>> cadastrarUsuario(
            @RequestBody CreateUsuarioDto createUsuarioDto,
            BindingResult result) {

        Map<String, String> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Dados inválidos.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ResponseEntity<?> emailValidated = usuarioService.createUsuario(createUsuarioDto);
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginVendedorDto LoginVendedorDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(LoginVendedorDto.email(), LoginVendedorDto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO("success", token));
    }

    @GetMapping("/confirmar-conta")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ResponseEntity<?> response = usuarioService.confirmEmail(UUID.fromString(confirmationToken));

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/cadastro-sucesso"))
                    .build();
        } else {
            return ResponseEntity.badRequest().body("Token inválido.");
        }
    }
}
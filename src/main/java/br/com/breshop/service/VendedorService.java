package br.com.breshop.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.security.jwt.JWTGenerator;

@Service
public class VendedorService {

    @Autowired
    private final VendedorRepository vendedorRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoderService passwordEncoderService;

    @Autowired
    private JWTGenerator jwtg;
    
    public VendedorService(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }


    public UUID createVendedor(CreateVendedorDto createVendedorDto) {
        if (vendedorRepository.findByEmail(createVendedorDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("O usuário já existe com o email: " + createVendedorDto.email());
        }
        if(createVendedorDto.senha() == null || createVendedorDto.senha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
        var vendedor = new Vendedor(UUID.randomUUID(),
                createVendedorDto.username(),
                createVendedorDto.email(),
                passwordEncoderService.encode(createVendedorDto.senha()),
                LocalDateTime.now(),
                null);


        return vendedorRepository.save(vendedor).getVendedorID();
    }

    public AuthResponseDTO loginVendedor(LoginVendedorDto loginVendedorDto) {
        if (loginVendedorDto.email() == null || loginVendedorDto.email().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }

        Optional<Vendedor> optionalVendedor = vendedorRepository.findByEmail(loginVendedorDto.email());
        Vendedor vendedor = optionalVendedor.orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));

        // Verifica se a senha informada corresponde à senha armazenada
        if (!passwordEncoderService.matches(loginVendedorDto.senha(), vendedor.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            vendedor.getEmail(),
            loginVendedorDto.senha()
        );

        // Autentica o usuário
        authenticationManager.authenticate(authentication);

        // Gera o token
        String token = jwtg.generateToken(authentication);

        return new AuthResponseDTO("Login realizado com sucesso!", token);
    }
    


    public Optional<Vendedor> getVendedorById(UUID vendedorId) {
        return vendedorRepository.findById(vendedorId);
    }
}

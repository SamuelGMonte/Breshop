package br.com.breshop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.ConfirmationTokenVendedor;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyReceivedException;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.ConfirmationTokenRepository;
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
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JWTGenerator jwtg;

    @Autowired
    EmailService emailService;

    public VendedorService(VendedorRepository vendedorRepository, AuthenticationManager authenticationManager, PasswordEncoderService passwordEncoderService, ConfirmationTokenRepository confirmationTokenRepository, JWTGenerator jwtg, EmailService emailService) {
        this.vendedorRepository = vendedorRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoderService = passwordEncoderService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.jwtg = jwtg;
        this.emailService = emailService;
    }

    public Vendedor loadVendedor(CreateVendedorDto createVendedorDto) {
        Optional<Vendedor> vendedorOptional = vendedorRepository.findByEmail(createVendedorDto.email());
        return vendedorOptional.orElse(null);
    }

    public ConfirmationTokenVendedor checkLastToken(Vendedor vendedor) {
        List<ConfirmationTokenVendedor> tokens = confirmationTokenRepository.findAllByVendedor(vendedor);

        ConfirmationTokenVendedor lastToken = tokens.stream()
                .max(Comparator.comparing(ConfirmationTokenVendedor::getCreatedDate))
                .orElse(null);

        LocalDateTime currentTime = LocalDateTime.now();

        if (lastToken != null && lastToken.getCreatedDate().isAfter(currentTime.minusMinutes(5))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedExpirationTime = lastToken.getDateExpiration().format(formatter);
            throw new UserAlreadyReceivedException("O e-mail de confirmação já foi enviado. Tente novamente às: " + formattedExpirationTime);
        }

        // Cria um novo token
        ConfirmationTokenVendedor token = new ConfirmationTokenVendedor();
        token.setVendedor(vendedor);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        return token;
    }

    public ResponseEntity<?> createVendedor(CreateVendedorDto createVendedorDto) {

        if (createVendedorDto.senha() == null || createVendedorDto.senha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }

        Optional<Vendedor> vendedorOptional = vendedorRepository.findByEmail(createVendedorDto.email());

        ConfirmationTokenVendedor token;
        if (vendedorOptional.isPresent()) {
            Vendedor existingVendedor = vendedorOptional.get();

            if (!existingVendedor.getIsEnabled()) {
                token = this.checkLastToken(existingVendedor);
                // Envio de e-mail
                return sendConfirmationEmail(createVendedorDto, token);
            } else {
                throw new UserAlreadyExistsException("Usuário já existe");
            }
        }

        // Caso vendedor não exista, cria um novo
        Vendedor newVendedor = new Vendedor(
                createVendedorDto.username(),
                createVendedorDto.email(),
                passwordEncoderService.encode(createVendedorDto.senha()),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,  // isEnabled
                false   // received
        );

        vendedorRepository.save(newVendedor);

        // Criação do token
        token = new ConfirmationTokenVendedor();
        token.setVendedor(newVendedor);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        // Envio de e-mail para novo vendedor
        return sendConfirmationEmail(createVendedorDto, token);
    }



    public void sendEmail(CreateVendedorDto createVendedorDto, ConfirmationTokenVendedor token) {
        Vendedor newVendedor = new Vendedor(createVendedorDto.username(), createVendedorDto.email(), createVendedorDto.senha(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                false
        );

        confirmationTokenRepository.save(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newVendedor.getEmail());
        mailMessage.setSubject("Complete seu cadastro no BreShop!");
        mailMessage.setText("Para confirmar sua conta, clique aqui : "
                + "http://localhost:8080/api/v1/vendedores/confirmar-conta?token=" + token.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        ResponseEntity.ok("Verifique o email pelo link enviado ao seu endereço de email em até 5 minutos");
    }

    public AuthResponseDTO loginVendedor(LoginVendedorDto loginVendedorDto) {
        if (loginVendedorDto.email() == null || loginVendedorDto.email().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }

        Optional<Vendedor> vendedorOptional = vendedorRepository.findByEmail(loginVendedorDto.email());

        // Verifica se o vendedor foi encontrado
        if (vendedorOptional.isEmpty()) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        Vendedor vendedor = vendedorOptional.get();


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

    public ResponseEntity<Map<String, String>> confirmEmail(UUID token) {
        Map<String, String> response = new HashMap<>();

        ConfirmationTokenVendedor confirmationTokenVendedor = confirmationTokenRepository.findByConfirmationToken(token);

        if (confirmationTokenVendedor == null) {
            response.put("error", "Token inválido ou expirado");
            return ResponseEntity.badRequest().body(response);
        }

        Vendedor vendedor = confirmationTokenVendedor.getVendedor();

        if (vendedor == null) {
            response.put("error", "Vendedor não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        if (confirmationTokenVendedor.getDateExpiration().isBefore(LocalDateTime.now())) {
            response.put("error", "Token expirado");
            return ResponseEntity.badRequest().body(response);
        }

        if (vendedor.getIsEnabled()) {
            response.put("error", "Conta já confirmada");
            return ResponseEntity.badRequest().body(response);
        }

        vendedor.setIsEnabled(true);
        vendedorRepository.save(vendedor);

        response.put("success", "Email verificado com sucesso!");
        return ResponseEntity.ok(response);
    }


    private ResponseEntity<?> sendConfirmationEmail(CreateVendedorDto createVendedorDto, ConfirmationTokenVendedor token) {
        try {
            this.sendEmail(createVendedorDto, token);
            return ResponseEntity.ok("E-mail de confirmação enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o e-mail de confirmação");
        }
    }



}

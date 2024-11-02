package br.com.breshop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import br.com.breshop.dto.CreateBrechoDto;
import br.com.breshop.entity.Brecho;
import br.com.breshop.repository.BrechoRepository;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.security.CustomAuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final VendedorRepository vendedorRepository;

    private final UsuarioRepository usuarioRepository;

    private final BrechoRepository brechoRepository;

    private CustomAuthManager authenticationManager;

    BCryptPasswordEncoder passwordEncoder;

    ConfirmationTokenRepository confirmationTokenRepository;

    private JWTGenerator jwtg;

    EmailService emailService;

    @Autowired
    public VendedorService(VendedorRepository vendedorRepository, UsuarioRepository usuarioRepository, CustomAuthManager authenticationManager, BCryptPasswordEncoder passwordEncoder, ConfirmationTokenRepository confirmationTokenRepository, JWTGenerator jwtg, EmailService emailService, BrechoRepository brechoRepository) {
        this.vendedorRepository = vendedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.jwtg = jwtg;
        this.emailService = emailService;
        this.brechoRepository = brechoRepository;
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

    public ResponseEntity<?> createVendedor(CreateVendedorDto createVendedorDto, CreateBrechoDto createBrechoDto) {

        if (createVendedorDto.senha() == null || createVendedorDto.senha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }

        if(usuarioRepository.findByEmail(createVendedorDto.email()).isPresent()) {
            throw new IllegalArgumentException("Vendedor já esta cadastrado como usuário");
        }

        Optional<Vendedor> vendedorOptional = vendedorRepository.findByEmail(createVendedorDto.email());
        List<Brecho> brechos = brechoRepository.findByBrechoSite(createBrechoDto.brechoSite());

        if (brechos.contains(createBrechoDto.brechoSite())) {
            throw new UserAlreadyExistsException("Este site já está associado a um brechó existente.");
        }

        ConfirmationTokenVendedor token;
        if (vendedorOptional.isPresent()) {
            Vendedor existingVendedor = vendedorOptional.get();

            if (!existingVendedor.getIsEnabled()) {
                token = this.checkLastToken(existingVendedor);
                // Envio de e-mail
                return sendConfirmationEmail(createVendedorDto, token);
            } else {
                throw new UserAlreadyExistsException("Vendedor/Site já existe");
            }
        }

        System.out.println(createBrechoDto.brechoSite());
        // Caso vendedor não exista, cria um novo
        Vendedor newVendedor = new Vendedor(
                createVendedorDto.username(),
                createVendedorDto.email(),
                passwordEncoder.encode(createVendedorDto.senha()),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,  // isEnabled
                false,   // received
                new ArrayList<>()
        );
        vendedorRepository.save(newVendedor);

//       Cria o brecho associado aquele vendedor
        Brecho newBrecho = new Brecho(
                createBrechoDto.brechoNome(),
                createBrechoDto.brechoSite(),
                createBrechoDto.brechoEndereco(),
                newVendedor,
                LocalDateTime.now(),
                LocalDateTime.now()
        );


        brechoRepository.save(newBrecho);

        newVendedor.getBrechos().add(newBrecho);

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

        confirmationTokenRepository.save(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(createVendedorDto.email());
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

        if(usuarioRepository.findByEmail(loginVendedorDto.email()).isPresent()) {
            throw new IllegalArgumentException("Faça login como usuário.");
        }

        Optional<Vendedor> vendedorOptional = vendedorRepository.findByEmail(loginVendedorDto.email());

        // Verifica se o vendedor foi encontrado
        if (vendedorOptional.isEmpty()) {
            throw new IllegalArgumentException("Vendedor não encontrado");
        }

        Vendedor vendedor = vendedorOptional.get();

        // Verifica se a senha informada corresponde à senha armazenada
        if (!passwordEncoder.matches(loginVendedorDto.senha(), vendedor.getSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            vendedor.getEmail(),
            loginVendedorDto.senha()
        );

        // Autentica o vendedor
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

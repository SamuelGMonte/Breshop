package br.com.breshop.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import br.com.breshop.dto.CreateUsuarioDto;
import br.com.breshop.repository.ConfirmationTokenUserRepository;
import br.com.breshop.repository.VendedorRepository;
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

import br.com.breshop.dto.CreateUsuarioDto;
import br.com.breshop.dto.LoginUserDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.ConfirmationTokenUser;
import br.com.breshop.entity.Usuario;
import br.com.breshop.exception.UserAlreadyReceivedException;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.security.jwt.JWTGenerator;

@Service
public class UsuarioService{

    private final UsuarioRepository usuarioRepository;

    private final VendedorRepository vendedorRepository;

    private CustomAuthManager authenticationManager;

    PasswordEncoder passwordEncoderService;

    ConfirmationTokenUserRepository confirmationTokenUserRepository;

    private JWTGenerator jwtg;

    EmailService emailService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, VendedorRepository vendedorRepository, CustomAuthManager authenticationManager, PasswordEncoder passwordEncoderService, ConfirmationTokenUserRepository confirmationTokenUserRepository, JWTGenerator jwtg, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.vendedorRepository = vendedorRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoderService = passwordEncoderService;
        this.confirmationTokenUserRepository = confirmationTokenUserRepository;
        this.jwtg = jwtg;
        this.emailService = emailService;
    }

    public Usuario loadUsuario(CreateUsuarioDto createUsuarioDto) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(createUsuarioDto.email());
        return usuarioOptional.orElse(null);
    }

    public ConfirmationTokenUser checkLastToken(Usuario usuario) {
        List<ConfirmationTokenUser> tokens = confirmationTokenUserRepository.findAllByUsuario(usuario);

        ConfirmationTokenUser lastToken = tokens.stream()
                .max(Comparator.comparing(ConfirmationTokenUser::getCreatedDate))
                .orElse(null);

        LocalDateTime currentTime = LocalDateTime.now();

        if (lastToken != null && lastToken.getCreatedDate().isAfter(currentTime.minusMinutes(5))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedExpirationTime = lastToken.getDateExpiration().format(formatter);
            throw new UserAlreadyReceivedException("O e-mail de confirmação já foi enviado. Tente novamente às: " + formattedExpirationTime);
        }

        // Cria um novo token
        ConfirmationTokenUser token = new ConfirmationTokenUser();
        token.setUser(usuario);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        return token;
    }

    public ResponseEntity<?> createUsuario(CreateUsuarioDto createUsuarioDto) {

        if (createUsuarioDto.senha() == null || createUsuarioDto.senha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }

        if(vendedorRepository.findByEmail(createUsuarioDto.email()).isPresent()) {
            throw new IllegalArgumentException("Usuário já esta cadastrado como vendedor");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(createUsuarioDto.email());

        ConfirmationTokenUser token;
        if (usuarioOptional.isPresent()) {
            Usuario existingUsuario = usuarioOptional.get();

            if (!existingUsuario.getIsEnabled()) {
                token = this.checkLastToken(existingUsuario);
                // Envio de e-mail
                return sendConfirmationEmail(createUsuarioDto, token);
            } else {
                throw new UserAlreadyExistsException("Usuário já existe");
            }
        }

        // Caso usuario não exista, cria um novo
        Usuario newUsuario = new Usuario(
                createUsuarioDto.username(),
                createUsuarioDto.email(),
                passwordEncoderService.encode(createUsuarioDto.senha()),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,  // isEnabled
                false   // received
        );

        usuarioRepository.save(newUsuario);

        // Criação do token
        token = new ConfirmationTokenUser();
        token.setUser(newUsuario);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        // Envio de e-mail para novo usuario
        return sendConfirmationEmail(createUsuarioDto, token);
    }



    public void sendEmail(CreateUsuarioDto createUsuarioDto, ConfirmationTokenUser token) {
        Usuario newUsuario = new Usuario(createUsuarioDto.username(), createUsuarioDto.email(), createUsuarioDto.senha(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                false
        );

        confirmationTokenUserRepository.save(token);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newUsuario.getEmail());
        mailMessage.setSubject("Complete seu cadastro no BreShop!");
        mailMessage.setText("Para confirmar sua conta, clique aqui : "
                + "http://localhost:8080/api/v1/usuarios/confirmar-conta?token=" + token.getConfirmationToken());
        emailService.sendEmail(mailMessage);

        ResponseEntity.ok("Verifique o email pelo link enviado ao seu endereço de email em até 5 minutos");
    }

    public AuthResponseDTO loginUsuario(LoginUserDto loginUsuarioDto) {
        if (loginUsuarioDto.email() == null || loginUsuarioDto.email().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginUsuarioDto.email());

        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        if(vendedorRepository.findByEmail(loginUsuarioDto.email()).isPresent()) {
            throw new IllegalArgumentException("Faça login como vendedor.");
        }

        // Verifica se o usuario foi encontrado

        Usuario usuario  = usuarioOptional.get();


        // Verifica se a senha informada corresponde à senha armazenada
        if (!passwordEncoderService.matches(loginUsuarioDto.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            usuario.getEmail(),
            loginUsuarioDto.senha()
        );

        // Autentica o usuário
        authenticationManager.authenticate(authentication);

        // Gera o token
        String token = jwtg.generateToken(authentication);

        return new AuthResponseDTO("Login realizado com sucesso!", token);
    }

    public ResponseEntity<Map<String, String>> confirmEmail(UUID token) {
        Map<String, String> response = new HashMap<>();

        ConfirmationTokenUser confirmationTokenUsuario = confirmationTokenUserRepository.findByConfirmationToken(token);

        if (confirmationTokenUsuario == null) {
            response.put("error", "Token inválido ou expirado");
            return ResponseEntity.badRequest().body(response);
        }

        Usuario usuario = confirmationTokenUsuario.getUser();

        if (usuario == null) {
            response.put("error", "Usuario não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        if (confirmationTokenUsuario.getDateExpiration().isBefore(LocalDateTime.now())) {
            response.put("error", "Token expirado");
            return ResponseEntity.badRequest().body(response);
        }

        if (usuario.getIsEnabled()) {
            response.put("error", "Conta já confirmada");
            return ResponseEntity.badRequest().body(response);
        }

        usuario.setIsEnabled(true);
        usuarioRepository.save(usuario);

        response.put("success", "Email verificado com sucesso!");
        return ResponseEntity.ok(response);
    }


    private ResponseEntity<?> sendConfirmationEmail(CreateUsuarioDto createUsuarioDto, ConfirmationTokenUser token) {
        try {
            this.sendEmail(createUsuarioDto, token);
            return ResponseEntity.ok("E-mail de confirmação enviado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o e-mail de confirmação");
        }
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElse(null);
    }

}

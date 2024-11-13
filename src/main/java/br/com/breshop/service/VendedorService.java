package br.com.breshop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.breshop.entity.VendedorImages;
import br.com.breshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.breshop.dto.CreateBrechoDto;
import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.ConfirmationTokenVendedor;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.exception.UserAlreadyReceivedException;
import br.com.breshop.security.CustomAuthManager;
import br.com.breshop.security.jwt.JWTGenerator;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VendedorService {

    @Value("{temp.dir}")
    private String uploadDir;

    private final VendedorRepository vendedorRepository;

    private final UsuarioRepository usuarioRepository;

    private final BrechoRepository brechoRepository;

    private final VendedorImagesRepository vendedorImagesRepository;

    private CustomAuthManager authenticationManager;

    BCryptPasswordEncoder passwordEncoder;

    ConfirmationTokenRepository confirmationTokenRepository;

    private JWTGenerator jwtg;

    EmailService emailService;

    @Autowired
    public VendedorService(VendedorRepository vendedorRepository, UsuarioRepository usuarioRepository, CustomAuthManager authenticationManager, BCryptPasswordEncoder passwordEncoder, ConfirmationTokenRepository confirmationTokenRepository, JWTGenerator jwtg, EmailService emailService, BrechoRepository brechoRepository, VendedorImagesRepository vendedorImagesRepository) {
        this.vendedorRepository = vendedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.jwtg = jwtg;
        this.emailService = emailService;
        this.brechoRepository = brechoRepository;
        this.vendedorImagesRepository = vendedorImagesRepository;
    }

    public Vendedor loadVendedor(CreateVendedorDto createVendedorDto) {
        List<Vendedor> vendedorList = vendedorRepository.findByEmail(createVendedorDto.email());
        return vendedorList.isEmpty() ? null : vendedorList.get(0);
    }

    public void checkTokenExpiration(Vendedor vendedor) {
        // Retrieve the latest token for the vendedor, if any
        ConfirmationTokenVendedor lastToken = confirmationTokenRepository.findTopByVendedorOrderByCreatedDateDesc(vendedor);

        if (lastToken != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            // Check if the last token was created less than 5 minutes ago
            if (lastToken.getCreatedDate().isAfter(currentTime.minusMinutes(5))) {
                // Format the expiration time if it's available
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedExpirationTime = lastToken.getDateExpiration() != null ?
                        lastToken.getDateExpiration().format(formatter) : "Unknown time";

                // Throw an exception with the formatted expiration time
                throw new UserAlreadyReceivedException("O e-mail de confirmação já foi enviado. Tente novamente às: " + formattedExpirationTime);
            }
        }
    }

    public ConfirmationTokenVendedor checkLastToken(Vendedor vendedor) {
        // Retrieve the latest token, if any, using a more efficient query
        ConfirmationTokenVendedor lastToken = confirmationTokenRepository.findTopByVendedorOrderByCreatedDateDesc(vendedor);

        if (lastToken != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            // If the token is recent, call tryAgainTime to handle exception
            if (lastToken.getCreatedDate().isAfter(currentTime.minusMinutes(5))) {
                checkTokenExpiration(vendedor);  // This will throw an exception if needed
            }
        }

        // Create a new token
        ConfirmationTokenVendedor token = new ConfirmationTokenVendedor();
        token.setVendedor(vendedor);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        return token;
    }


    public ResponseEntity<?> createVendedor(CreateVendedorDto createVendedorDto, CreateBrechoDto createBrechoDto, MultipartFile file) {

        // Validação da senha
        if (createVendedorDto.senha() == null || createVendedorDto.senha().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }

        // Verifica se o e-mail já está registrado
        if(usuarioRepository.findByEmail(createVendedorDto.email()).isPresent()) {
            throw new IllegalArgumentException("Vendedor já está cadastrado como usuário");
        }

        if(!vendedorRepository.findByEmail(createVendedorDto.email()).isEmpty()) {
            throw new IllegalArgumentException("Email de vendedor já existe");
        }

        List<Vendedor> vendedorOptional = vendedorRepository.findByEmail(createVendedorDto.email());
        List<Brecho> brechos = brechoRepository.findByBrechoSite(createBrechoDto.brechoSite());

        // Verifica se o site e brecho já está associado a um brechó
        if (!vendedorOptional.isEmpty()) {
            Vendedor vendedor = vendedorOptional.get(0); // Access the first element in the list

            if (vendedor.isIsEnabled()) {
                // If the vendedor is enabled, throw an exception
                throw new UserAlreadyExistsException("Este email já está associado a um vendedor existente.");
            } else {
                // If the vendedor is not enabled, check token expiration
                checkTokenExpiration(vendedor);
            }
        }

        if (!brechos.isEmpty()) {
            Brecho brecho = brechos.get(0); // Access the first element in the list

            if (brechoRepository.findByBrechoNome(brecho.getBrechoNome()) != null) {
                throw new UserAlreadyExistsException("Este nome de loja já existe.");
            }

            if (brechoRepository.findByBrechoSite(brecho.getBrechoSite()) != null) {
                throw new UserAlreadyExistsException("Este site já está associado a um brechó existente.");
            }
        }


        // Cria o Vendedor e o Brecho
        Vendedor newVendedor = new Vendedor(
                createVendedorDto.username(),
                createVendedorDto.email(),
                passwordEncoder.encode(createVendedorDto.senha()),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                false,
                new ArrayList<>()
        );
        vendedorRepository.save(newVendedor);

        // Lógica de arquivo (imagem)
        if (file != null && !file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadDir + file.getOriginalFilename());
                Files.write(path, bytes);

                // Criação da imagem e do Brecho
                VendedorImages vendedorImage = new VendedorImages();
                vendedorImage.setImgNome(file.getOriginalFilename());
                vendedorImage.setImgTipo(file.getContentType());
                vendedorImage.setImgData(bytes);
                vendedorImage.setVendedor(newVendedor);

                vendedorImagesRepository.save(vendedorImage);

                Optional<VendedorImages> vendedorImagemOptional  = vendedorImagesRepository.findByVendedor(newVendedor);
                VendedorImages vendedorImagem = vendedorImagemOptional.orElse(null);

                // Criação do Brecho
                Brecho brecho = new Brecho(
                        createBrechoDto.brechoNome(),
                        createBrechoDto.brechoSite(),
                        createBrechoDto.brechoEndereco(),
                        newVendedor,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        vendedorImagem
                );

                brechoRepository.save(brecho);
                
                vendedorImage.setBrechoImg(brecho);
                vendedorImagesRepository.save(vendedorImage);



                newVendedor.getBrechos().add(brecho); // Associando ao Vendedor
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar o arquivo.", e);
            }
        } else {
            // Cria o Brecho sem imagem
            Brecho brecho = new Brecho(
                    createBrechoDto.brechoNome(),
                    createBrechoDto.brechoSite(),
                    createBrechoDto.brechoEndereco(),
                    newVendedor,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );
            brechoRepository.save(brecho);
            newVendedor.getBrechos().add(brecho);
        }

        vendedorRepository.save(newVendedor); // Salva o Vendedor com o Brecho

        // Criação do token
        ConfirmationTokenVendedor token = new ConfirmationTokenVendedor();
        token.setVendedor(newVendedor);
        token.setConfirmationToken(UUID.randomUUID());
        token.setCreatedDate(LocalDateTime.now());
        token.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        // Envio de e-mail para o novo vendedor
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

        List<Vendedor> vendedorOptional = vendedorRepository.findByEmail(loginVendedorDto.email());

        // Verifica se o vendedor foi encontrado
        if (vendedorOptional.isEmpty()) {
            throw new IllegalArgumentException("Vendedor não encontrado");
        }

        Vendedor vendedor = vendedorOptional.get(0);

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

        //Deleta o token após verificado
        confirmationTokenRepository.delete(confirmationTokenVendedor);

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

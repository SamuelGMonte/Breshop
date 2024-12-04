package br.com.breshop.controller.mvc.vendedor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.breshop.controller.MailService;
import br.com.breshop.dto.CreateBrechoDto;
import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.BrechoAlreadyExistsException;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.UsuarioRepository;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.service.VendedorService;

@Controller
@RequestMapping("/vendedores")
public class MvcVendedorController {

    private final VendedorService vendedorService;
    private final UsuarioRepository usuarioRepository;
    private final MailService mailSender;
    private final VendedorRepository vendedorRepository;


    @Autowired
    public MvcVendedorController(VendedorService vendedorService, UsuarioRepository usuarioRepository, MailService mailSender, VendedorRepository vendedorRepository) {
        this.vendedorService = vendedorService;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.vendedorRepository = vendedorRepository;
    }

    @GetMapping("/login")
    public String showLoginVendedor(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "login-vendedor/login-vendedor";
    }

    @PostMapping("/logarVendedor")
    public ResponseEntity<?> logarVendedorFromMvc(
            @RequestBody  LoginVendedorDto loginVendedorDto) {
        System.out.println(loginVendedorDto);
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


    @GetMapping("/cadastro-brecho")
    public String cadastroPage(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        model.addAttribute("brecho", new Brecho());
        return "cadastro-brecho/cadastro-brecho";
    }


    @PostMapping("/cadastrarVendedor")
    public ResponseEntity<?> cadastrarVendedorFromMvc(
            @ModelAttribute CreateVendedorDto createVendedorDto,
            @ModelAttribute CreateBrechoDto createBrechoDto,
            @RequestParam String confirmaSenha,
            @RequestParam("filename") MultipartFile file,
            BindingResult result) {



        // Verifica se as senhas coincidem
        if (!createVendedorDto.senha().equals(confirmaSenha)) {
            return ResponseEntity.badRequest()
                    .body("As senhas não coincidem.");
        }

//       Verifica se vendedor/user ja existe
        if(usuarioRepository.findByEmail(createVendedorDto.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email de vendedor já cadastrado como usuário, efetue o login.");
        }


        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos.");
        }

        try {
            if(file.isEmpty()) {
                vendedorService.createVendedor(createVendedorDto, createBrechoDto, null);
            } else {
                vendedorService.createVendedor(createVendedorDto, createBrechoDto, file);
                Optional<Integer> vendedorOptional =  vendedorRepository.findIdByEmail(createVendedorDto.email());

                Integer vendedorId = vendedorOptional.get();

                String verificationUrl = "http://localhost:8080/api/v1/vendedores/confirmarFoto?vendedorId=" + vendedorId;

                mailSender.mailWithAttachment(
                        "Verifique se a imagem do vendedor " + createVendedorDto.email() + " é apropriada, se for clique aqui: " + verificationUrl,
                        "Verifique a imagem",
                        file
                );
            }
            return ResponseEntity.ok("Email enviado com sucesso!");
        } catch (UserAlreadyExistsException | BrechoAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

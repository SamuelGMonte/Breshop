package br.com.breshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.breshop.entity.VendedorImages;
import br.com.breshop.repository.VendedorImagesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.breshop.dto.CreateBrechoDto;
import br.com.breshop.dto.CreateVendedorDto;
import br.com.breshop.dto.LoginVendedorDto;
import br.com.breshop.dto.jwt.AuthResponseDTO;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.exception.BrechoAlreadyExistsException;
import br.com.breshop.exception.UserAlreadyExistsException;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.service.VendedorService;

@RestController
@RequestMapping("api/v1/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorRepository vendedorRepository;
    private final VendedorImagesRepository vendedorImagesRepository;

    public VendedorController(VendedorService vendedorService, VendedorRepository vendedorRepository, VendedorImagesRepository vendedorImagesRepository) {
        this.vendedorService = vendedorService;
        this.vendedorRepository = vendedorRepository;
        this.vendedorImagesRepository = vendedorImagesRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginVendedorDto LoginVendedorDto,
                                                     BindingResult result) {
        Map<String, String> response = new HashMap<>();

        if (LoginVendedorDto.senha() == null || LoginVendedorDto.senha().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Senha vazia.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            AuthResponseDTO authResponse = vendedorService.loginVendedor(LoginVendedorDto);
            response.put("status", "success");
            response.put("message", "Usuário criado com sucesso, token: " + authResponse.getToken());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping(value = "/registrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> cadastrarVendedor(
            @RequestPart("vendedor") CreateVendedorDto createVendedorDto,
            @RequestPart("brecho") CreateBrechoDto createBrechoDto,
            @RequestPart("file") MultipartFile file,
            BindingResult result) {

        Map<String, String> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("status", "error");
            response.put("message", "Dados inválidos.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ResponseEntity<?> emailValidated = vendedorService.createVendedor(createVendedorDto, createBrechoDto, file);
            if (emailValidated.getStatusCode() == HttpStatus.OK) {
                response.put("status", "success");
                response.put("message", "Verifique o email pelo link enviado ao seu endereço de email");
                return ResponseEntity.ok(response);
            }
        } catch (UserAlreadyExistsException | BrechoAlreadyExistsException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/confirmarFoto")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("vendedorId") Integer vendedorId) {
        Map<String, String> response = new HashMap<>();

        Optional<Vendedor> vendedor = vendedorRepository.findById(vendedorId);

        List<Integer> vendedorIdJoin = vendedorImagesRepository.findJoinVendedorImage();


        if(vendedor.isPresent()) {
            Optional<VendedorImages> vendedorImages = vendedorImagesRepository.findByVendedor(vendedor.get());

            if (vendedorImages.get().isVerified()) {
                response.put("status", "error");
                response.put("message", "Foto já foi verificada anteriormente.");
                return ResponseEntity.ok(response);
            }

            vendedorImages.get().setVerified(true);

            vendedorImagesRepository.save(vendedorImages.get());

            response.put("status", "success");
            response.put("message", "Foto do vendedor verificada com sucesso.");
            response.put("vendedorId", String.valueOf(vendedorId));

            return ResponseEntity.ok(response);
        }
        if(vendedorIdJoin.contains(vendedor.get().getVendedorId())) {
            response.put("status", "error");
            response.put("message", "Vendedor não tem foto de brechó.");
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Erro na verificação da foto: vendedor não encontrado.");
        return ResponseEntity.badRequest().body(response);

        }
    }


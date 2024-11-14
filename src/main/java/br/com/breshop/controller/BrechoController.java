package br.com.breshop.controller;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.entity.VendedorImages;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.service.VendedorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import br.com.breshop.BrechoService;
import br.com.breshop.entity.Brecho;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/brecho")
public class BrechoController {
    private final BrechoService brechoService;
    private final VendedorRepository vendedorRepository;

    public BrechoController(BrechoService brechoService, VendedorRepository vendedorRepository) {
        this.brechoService = brechoService;
        this.vendedorRepository = vendedorRepository;
    }

    @GetMapping("/{vendedorId}/meus-brechos")
    public ResponseEntity<?> getBrechosByVendedorId(@PathVariable Integer vendedorId) {
        List<Brecho> brechos = brechoService.getBrechosByVendedorId(vendedorId);
        Map<String, Object> response = new HashMap<>();

        if (!brechos.isEmpty()) {
            List<String> brechoNames = brechos.stream()
                                            .map(Brecho::getBrechoNome) 
                                            .collect(Collectors.toList());

            response.put("status", "success");
            response.put("nomes", brechoNames);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum brechó encontrado para o vendedor");
        return ResponseEntity.badRequest().body(response);
    }


    @GetMapping("/nomes")
    public ResponseEntity<?> getNomes() {
        List<String> enderecos = brechoService.getAllBrechosNomes(); 
        Map<String, Object> response = new HashMap<>();

        if (!enderecos.isEmpty()) {
            response.put("status", "success");
            response.put("nomes", enderecos);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum endereço encontrado");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/enderecos")
    public ResponseEntity<?> getEnderecos() {
        List<String> enderecos = brechoService.getAllBrechoEnderecos(); 
        Map<String, Object> response = new HashMap<>();

        if (!enderecos.isEmpty()) {
            response.put("status", "success");
            response.put("enderecos", enderecos);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum endereço encontrado");
        return ResponseEntity.badRequest().body(response);
    }


    @GetMapping("/sites")
    public ResponseEntity<?> getSites() {
        List<String> enderecos = brechoService.getAllBrechoSites(); 
        Map<String, Object> response = new HashMap<>();

        if (!enderecos.isEmpty()) {
            response.put("status", "success");
            response.put("websites", enderecos);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum endereço encontrado");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/imagens/{vendedorId}")
    public ResponseEntity<?> getBrechosImageByVendedorId(@PathVariable Integer vendedorId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Vendedor> vendedorOptional = vendedorRepository.findById(vendedorId);

        if (vendedorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendedor não encontrado");
        }

        Vendedor vendedor = vendedorOptional.get();
        List<Brecho> brechos = brechoService.getBrechosByVendedorId(vendedorId);
        byte[] imagemBrecho = brechoService.getBrechoImg(vendedor);

        if (!brechos.isEmpty()) {
            List<String> brechoNames = brechos.stream()
                    .map(Brecho::getBrechoNome)
                    .collect(Collectors.toList());

            response.put("status", "success");
            response.put("imagem", imagemBrecho);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhuma imagem do brechó encontrada");

        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/imagens")
    public ResponseEntity<?> getAllBrechosImage() {
        Map<String, Object> response = new HashMap<>();
        List<String> enderecos = brechoService.getAllBrechoSites();
        List<byte[]> brechosImages = brechoService.getAllBrechoImgs();

        response.put("status", "success");

        if (!enderecos.isEmpty()) {
            response.put("websites", enderecos);
        }

        if (!brechosImages.isEmpty()) {
            List<Map<String, Object>> brechosInfo = new ArrayList<>();

            for (byte[] image : brechosImages) {
                Map<String, Object> brechoInfo = new HashMap<>();
                if(brechoService.checkVerifiedImage(image)) {
                    brechoInfo.put("imagem", Base64.getEncoder().encodeToString(image));
                    brechosInfo.add(brechoInfo);
                } else {
                    System.out.println("Imagem não é verificada.");
                }
            }

            response.put("brechos", brechosInfo);
        } else {
            response.put("status", "error");
            response.put("message", "Nenhuma imagem encontrada");
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }





}

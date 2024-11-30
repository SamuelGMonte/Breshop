package br.com.breshop.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.breshop.dto.AllBrechosDto;
import br.com.breshop.dto.BrechoDescricaoDto;
import br.com.breshop.dto.CreateBrechoDto;
import br.com.breshop.repository.BrechoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import br.com.breshop.BrechoService;
import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.repository.VendedorRepository;
import br.com.breshop.security.jwt.JWTGenerator;


@RestController
@RequestMapping("/api/v1/brecho")
public class BrechoController {
    private final BrechoService brechoService;
    private final VendedorRepository vendedorRepository;
    private final JWTGenerator jwtg;
    private final BrechoRepository brechoRepository;

    @Autowired
    public BrechoController(BrechoService brechoService, VendedorRepository vendedorRepository, JWTGenerator jwtg, BrechoRepository brechoRepository) {
        this.brechoService = brechoService;
        this.vendedorRepository = vendedorRepository;
        this.jwtg = jwtg;
        this.brechoRepository = brechoRepository;
    }

    @GetMapping("/meus-brechos")
    public ResponseEntity<?> getBrechosByVendedorId(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        String actualToken = token.replace("Bearer", "");

        Integer userId = jwtg.getUserIdFromJWT(actualToken);
        List<Brecho> brechos = brechoService.getBrechosByVendedorId(userId);

        String userRole = jwtg.getUserRoleFromJWT(actualToken);
        if(!userRole.equalsIgnoreCase("Vendedor")) {
            response.put("status", "error");
            response.put("message", "Cadastrado como usuário e não vendedor");
            return ResponseEntity.badRequest().body("");
        }

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

    @GetMapping("/envio-token")
    public ResponseEntity<String> handleData(@RequestHeader("Authorization") String token) {
        System.out.println("Received data: " + token);

        return ResponseEntity.ok("Data received successfully");
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

    @GetMapping("/endereco")
    public ResponseEntity<?> getEnderecosById(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        String actualToken = token.replace("Bearer", "");

        Integer userId = jwtg.getUserIdFromJWT(actualToken);

        List<String> enderecos = brechoService.getBrechoEndereco(userId);

        if (!enderecos.isEmpty()) {
            if(enderecos.size() > 1) {
                for(String e: enderecos) {
                    response.put("enderecos", e);
                }
            } else {
                response.put("status", "success");
                response.put("enderecos", enderecos);
                return ResponseEntity.ok(response);
            }
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

    @GetMapping("/site")
    public ResponseEntity<?> getSite(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        String actualToken = token.replace("Bearer", "");

        Integer userId = jwtg.getUserIdFromJWT(actualToken);

        List<String> enderecos = brechoService.getBrechoSite(userId);

        if (!enderecos.isEmpty()) {
            response.put("status", "success");
            response.put("websites", enderecos);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum endereço encontrado");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/imagem")
    public ResponseEntity<?> getBrechosImageByVendedorId(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        String actualToken = token.replace("Bearer", "");

        Integer userId = jwtg.getUserIdFromJWT(actualToken);
        List<Brecho> brechos = brechoService.getBrechosByVendedorId(userId);

        String userRole = jwtg.getUserRoleFromJWT(actualToken);

        Optional<Vendedor> vendedorOptional = vendedorRepository.findById(userId);

        if (vendedorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendedor não encontrado");
        }

        if(!userRole.equalsIgnoreCase("Vendedor")) {
            response.put("status", "error");
            response.put("message", "Cadastrado como usuário e não vendedor");
            return ResponseEntity.badRequest().body("");
        }


        Vendedor vendedor = vendedorOptional.get();
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
            response.put("status", "success");
            response.put("message", "Nenhuma imagem encontrada");
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/salvar-descricao")
    public ResponseEntity<?> saveText(@RequestHeader("Authorization") String token, @RequestBody() BrechoDescricaoDto brechoDescricaoDto) {
        System.out.println("Texto recebido: " + brechoDescricaoDto.descricao());

        String actualToken = token.replace("Bearer", "");

        Integer userId = jwtg.getUserIdFromJWT(actualToken);

        brechoService.updateBrechoDescricao(userId, brechoDescricaoDto);

        return ResponseEntity.ok("Texto salvo!");
    }

    @GetMapping("/descricao/{brechoId}")
    public ResponseEntity<?> getText(@PathVariable Integer brechoId) {
        Map<String, Object> response = new HashMap<>();

        String brechoDescricao = brechoService.getBrechoDescricao(brechoId);

        if(brechoDescricao.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Descrição vazia");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("status", "success");
        response.put("message", brechoDescricao);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brechos")
    public ResponseEntity<List<AllBrechosDto>> getAllBrechos() {
        List<Brecho> brechos = brechoRepository.findAll();
        List<AllBrechosDto> brechoDTOs = brechos.stream()
                .map(brecho -> new AllBrechosDto(brecho.getBrechoId(), brecho.getBrechoNome()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(brechoDTOs);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        Brecho brechos = brechoService.getBrechoNome(keyword);
        if(brechos == null) {
            response.put("status", "error");
            response.put("message", "Nenhum brechó encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("status", "success");
        response.put("message", brechos);
        return ResponseEntity.ok(response);
    }


}

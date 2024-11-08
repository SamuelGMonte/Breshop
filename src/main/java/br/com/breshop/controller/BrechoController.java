package br.com.breshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.breshop.BrechoService;
import br.com.breshop.entity.Brecho;


@RestController
@RequestMapping("/api/v1/brecho")
public class BrechoController {
    private final BrechoService brechoService;

    public BrechoController(BrechoService brechoService) {
        this.brechoService = brechoService;
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
    
}

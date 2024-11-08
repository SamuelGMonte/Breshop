package br.com.breshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.breshop.BrechoService;


@RestController
@RequestMapping("/api/v1/brecho")
public class BrechoController {
    private final BrechoService brechoService;

    public BrechoController(BrechoService brechoService) {
        this.brechoService = brechoService;
    }

    @GetMapping("/nomes")
    public ResponseEntity<?> getNomes() {
        List<String> enderecos = brechoService.getAllBrechosNomes(); 
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
            response.put("enderecos", enderecos);
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Nenhum endereço encontrado");
        return ResponseEntity.badRequest().body(response);
    }
    
}

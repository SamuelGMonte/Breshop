package br.com.breshop.controller.mvc.brecho;

import br.com.breshop.controller.BrechoController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.breshop.repository.BrechoRepository;

import java.util.Base64;

@Controller
public class MvcBrechoController {
    private final BrechoRepository brechoRepository;
    private final BrechoController brechoController;
    private final ObjectMapper objectMapper;

    public MvcBrechoController(BrechoRepository brechoRepository, BrechoController brechoController, BrechoController brechoController1, ObjectMapper objectMapper) {
        this.brechoRepository = brechoRepository;
        this.brechoController = brechoController1;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/pesquisa-brecho")
    public String cadastroPage() {
        return "pesquisa-brecho/pesquisa-brecho";
    }

    @GetMapping("vendedor/meu-brecho")
    public String brechosPage( @CookieValue("jwtToken") String token) {
        String[] chunks = token.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        String sub = "";

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            sub = jsonNode.get("sub").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String actualToken = token.replace("Bearer ", "").trim();

        ResponseEntity<?> response = brechoController.getBrechosByVendedorId(actualToken);

        if (response.getStatusCode().is4xxClientError()) {
            return "404";
        }

        return "meus-brechos/meus-brechos";
    }



}
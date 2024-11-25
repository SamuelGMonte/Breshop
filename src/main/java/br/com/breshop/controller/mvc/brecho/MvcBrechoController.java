package br.com.breshop.controller.mvc.brecho;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.breshop.entity.Brecho;
import br.com.breshop.repository.BrechoRepository;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MvcBrechoController {
    private final BrechoRepository brechoRepository;

    public MvcBrechoController(BrechoRepository brechoRepository) {
        this.brechoRepository = brechoRepository;
    }


    @GetMapping("/pesquisa-brecho")
    public String cadastroPage(Model model) {
        model.addAttribute("brecho", new Brecho());
        return "pesquisa-brecho/pesquisa-brecho";
    }

    @GetMapping("vendedor/{vendedorId}/brecho")
    public String brechosPage(Model model, @PathVariable Integer vendedorId) {
        if(brechoRepository.findByVendedorId(vendedorId).isEmpty()) {
            return "404";
        }
        model.addAttribute("brecho", new Brecho());
        return "meus-brechos/meus-brechos";
    }


}
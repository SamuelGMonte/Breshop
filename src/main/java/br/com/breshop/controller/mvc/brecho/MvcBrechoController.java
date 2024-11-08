package br.com.breshop.controller.mvc.brecho;

import br.com.breshop.entity.Brecho;
import br.com.breshop.entity.Vendedor;
import br.com.breshop.repository.BrechoRepository;
import br.com.breshop.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
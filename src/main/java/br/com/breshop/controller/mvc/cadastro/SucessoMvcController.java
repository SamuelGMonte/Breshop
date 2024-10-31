package br.com.breshop.controller.mvc.cadastro;

import br.com.breshop.entity.Vendedor;
import br.com.breshop.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SucessoMvcController {
    private final UsuarioService usuarioService;

    public SucessoMvcController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping("/cadastro-sucesso")
    public String cadastroSucesso(Model model) {
        model.addAttribute("vendedor", new Vendedor());
        return "cadastro-sucesso/cadastro-sucesso";
    }
}

package br.com.breshop.controller.mvc;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class MvcNotFoundPage implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "404";
    }
}
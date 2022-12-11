package searchengine.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Метод формирует страницу из HTML-файла index.html, который находится в папке resources/templates.
 */
@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}

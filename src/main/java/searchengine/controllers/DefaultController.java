package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import searchengine.repository.SitesRepository;

/**
 * Метод формирует страницу из HTML-файла index.html, который находится в папке resources/templates.
 */
@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index() { return "index"; }

}

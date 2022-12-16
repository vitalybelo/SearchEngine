package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.config.Site;
import searchengine.dto.api.RequestResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;
import searchengine.services.IndexingService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SiteEntityRepository siteRepository;
    @Autowired
    private PageEntityRepository pageRepository;

    private final StatisticsService statisticsService;
    private IndexingService indexingService;
    private StatisticsData statisticData;

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics()
    {
        StatisticsResponse response = statisticsService.getStatistics();
        statisticData = response.getStatistics();
        indexingService = new IndexingService(statisticData, siteRepository, pageRepository);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<RequestResponse> startIndexing()
    {
        // если индексация запущена выходим с ошибкой
        if (statisticData.getTotal().isIndexing()) {
            return ResponseEntity.ok(new RequestResponse(false, "Индексация уже запущена"));
        }
        // если индексация не запущена - запускаем сервис и выходим
        //new Thread(() -> indexingService.startIndexingAll()).start();
        indexingService.startIndexingAll();
        return ResponseEntity.ok(new RequestResponse(true, ""));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<RequestResponse> stopIndexing()
    {
        // индексация не запущена, останавливать нечего - на выход
        if (!statisticData.getTotal().isIndexing()) {
            return ResponseEntity.ok(new RequestResponse(false, "индексация не запущена"));
        }
        // останавливаем сервис индексации
        indexingService.stopIndexing();
        return ResponseEntity.ok(new RequestResponse(true, ""));
    }

    @PostMapping("/indexPage")
    public void indexPage(Site site)
    {
        System.out.println(site.getName());
        System.out.println(site.getUrl());
    }


}

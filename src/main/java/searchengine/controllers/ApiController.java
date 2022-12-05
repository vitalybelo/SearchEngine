package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.config.Site;
import searchengine.dto.statistics.RequestResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.StatisticsService;


@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private StatisticsResponse response = new StatisticsResponse();
    private StatisticsData statisticData = new StatisticsData();

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics()
    {
        response = statisticsService.getStatistics();
        statisticData = response.getStatistics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<RequestResponse> startIndexing()
    {
        // если индексация запущена выходим с ошибкой
        if (statisticData.getTotal().isIndexing()) {
            return ResponseEntity.ok(new RequestResponse(false, "Индексация уже запущена"));
        }
        // если индексация не запущена - запускаем и выходим
        statisticData.getTotal().setIndexing(true);
        return ResponseEntity.ok(new RequestResponse(true, ""));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<RequestResponse> stopIndexing()
    {

        // индексация не запущена, останавливать нечего
        if (!statisticData.getTotal().isIndexing()) {
            return ResponseEntity.ok(new RequestResponse(false, "индексация не запущена"));
        }
        // останавливаем индексацию
        statisticData.getTotal().setIndexing(false);
        return ResponseEntity.ok(new RequestResponse(true, ""));
    }

    @PostMapping("/indexPage")
    public void indexPage(Site site) {

        System.out.println(site.getName());
        System.out.println(site.getUrl());
    }


}

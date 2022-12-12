package searchengine.services;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.model.SiteEntity;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;
import searchengine.services.indexing.RecursiveLinkParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Setter
public class IndexingService {

    private SiteEntityRepository siteRepository;
    private PageEntityRepository pageRepository;
    private List<DetailedStatisticsItem> searchItems;
    private StatisticsData statisticsData;
    List<Thread> threads = new ArrayList<>();

    public IndexingService(@NotNull StatisticsData statisticsData, SiteEntityRepository site, PageEntityRepository page)
    {
        this.statisticsData = statisticsData;
        this.searchItems = statisticsData.getDetailed();
        this.siteRepository = site;
        this.pageRepository = page;
    }

    public void startIndexingAll()
    {
        statisticsData.getTotal().setIndexing(true);
        for (DetailedStatisticsItem item : searchItems)
        {
            System.out.println("Start indexing for: " + item.getName() + " :: " + item.getUrl());
            Thread thread = new Thread(() -> { startIndexing(item); });
            threads.add(thread);
            thread.start();
        }
    }

    public void startIndexing(@NotNull DetailedStatisticsItem item)
    {
        // Ищем совпадения по названию для записи в таблице site и удаляем если находим
        Iterable<SiteEntity> siteEntities = siteRepository.findAll();
        for (SiteEntity s : siteEntities) {
            if (s.getName().equals(item.getName())) {
                siteRepository.delete(s);
            }
        }
        // Cоздаем новую запись по имени и адресу сайта
        String s = RecursiveLinkParser.smartUrl(item.getUrl());
        if (s != null) item.setUrl(s);
        SiteEntity site = new SiteEntity(item.getName(), item.getUrl());
        siteRepository.save(site);

        // Поиск ссылок по выбранному URL
        RecursiveLinkParser parser = new RecursiveLinkParser(site.getUrl(), statisticsData);
        parser.setRepositoryData(siteRepository, pageRepository);
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.invoke(parser);
        System.out.println("Scanning for: " + site.getUrl() + " stopped");
    }

    public void stopIndexing()
    {
        statisticsData.getTotal().setIndexing(false);
        for (Thread t : threads)
        {
            if (t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
            System.out.println(t.getName() + " :: " + t.getState());
        }
        threads.clear();
    }


}

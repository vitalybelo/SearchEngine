package searchengine.services;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;
import searchengine.services.indexing.DataPackage;
import searchengine.services.indexing.RecursiveLinkParser;

import java.util.*;
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
        if (!threads.isEmpty()) threads.clear();
        statisticsData.getTotal().setIndexing(true);
        // запуск индексирования по разным потокам
        for (DetailedStatisticsItem item : searchItems)
        {
            Thread thread = new Thread(() -> { startIndexing(item); });
            threads.add(thread);
            thread.start();
        }
    }

    public synchronized void startIndexing(@NotNull DetailedStatisticsItem item)
    {
        // создаем умный url который возвращает сайт
        String smartUrl = RecursiveLinkParser.smartUrl(item.getUrl());
        if (smartUrl != null) item.setUrl(smartUrl);
        System.out.println("\n\nSTART indexing for: " + item.getUrl() + "\n");

        // Ищем совпадения по названию для записи в таблице site и удаляем если находим
        Iterable<SiteEntity> siteEntities = siteRepository.findAll();
        for (SiteEntity site : siteEntities) {
            if (site.getName().equals(item.getName())) {
                siteRepository.delete(site);
            }
        }
        // Создаем новую запись по имени и адресу сайта
        SiteEntity site = new SiteEntity(item.getName(), item.getUrl());
        site.setStatus(Status.INDEXING);
        siteRepository.save(site);

        // Поиск ссылок по выбранному URL
        DataPackage data = new DataPackage(statisticsData, site, siteRepository, pageRepository);
        RecursiveLinkParser parser = new RecursiveLinkParser(item.getUrl(), data);
        RecursiveLinkParser.uniqueURL.clear(); // dont remove
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.invoke(parser);

        // сохраняем результаты индексирования в базе (каскадом в таблицах site и page)
        site = parser.getResult();
        siteRepository.save(site);
        System.out.println("\n" + site);
    }

    public void stopIndexing()
    {
        statisticsData.getTotal().setIndexing(false);
        for (Thread t : threads)
        {
            if (t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace(); }
            }
            System.out.println(t.getName() + " :: " + t.getState());
        }
    }


}

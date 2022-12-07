package searchengine.services;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.model.SiteEntity;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;
import searchengine.services.indexing.RecursiveLinkParser;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;

@Setter
@Service
public class IndexingService {

    @Autowired
    private SiteEntityRepository siteRepository;
    private PageEntityRepository pageRepository;
    private List<DetailedStatisticsItem> searchItems;

    public IndexingService(@NotNull List<DetailedStatisticsItem> searchItems) {
        this.searchItems = searchItems;
    }

    public void startIndexingAll()
    {
        int index = 2;
        System.out.println("Start indexing for:");
        System.out.println(searchItems.get(index).getName() + " :: " + searchItems.get(index).getUrl());
        startIndexing(searchItems.get(index));

        //        for (DetailedStatisticsItem item : searchItems) {
//
//            System.out.println(item.getName() + " :: " + item.getUrl());
//            startIndexing(item);
//
//        }
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
        SiteEntity site = new SiteEntity(item.getName(), item.getUrl());
        siteRepository.save(site);

        // Поиск ссылок по выбранному URL
        RecursiveLinkParser parser = new RecursiveLinkParser(site.getUrl());
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        TreeSet<String> uniqueURL = commonPool.invoke(parser);

        // Создаем записи в таблице page
        for (String s : uniqueURL) {
        }

    }

    public static String spaceTab(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '/') count++;
        }
        StringBuilder sb = new StringBuilder();
        if (count > 3) {
            sb.append("\t".repeat(count - 3));
        }
        sb.append(s);
        return sb.toString();
    }


}

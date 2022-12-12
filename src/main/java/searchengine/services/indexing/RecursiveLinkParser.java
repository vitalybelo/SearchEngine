package searchengine.services.indexing;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.dto.statistics.StatisticsData;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;

import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import static java.lang.Thread.sleep;

public class RecursiveLinkParser extends RecursiveAction {

    public final static int TIME_OUT = 5000;
    public static TreeSet<String> uniqueURL;

    private final UserAgent userAgent = new UserAgent();
    private final String urlSite;
    private StatisticsData data;
    private SiteEntityRepository site;
    private PageEntityRepository page;

    public RecursiveLinkParser(@NotNull String urlSite, StatisticsData data)
    {
        this.urlSite = urlSite;
        this.data = data;
        uniqueURL = new TreeSet<>();
    }

    public void setRepositoryData(SiteEntityRepository site, PageEntityRepository page) {
        this.site = site;
        this.page = page;
    }

    @Override
    protected void compute()
    {
        if (!data.getTotal().isIndexing()) return;
        List<RecursiveLinkParser> parserTasks = new ArrayList<>();
        try {
            // пауза частоты индексирования
            sleep(200);
            // создаем запрос к странице
            Connection connection = Jsoup
                    .connect(urlSite)
                    .userAgent(userAgent.get())
                    .referrer("https://google.com")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(TIME_OUT)
                    .newRequest();
            // парсим в документ страницу, собираем все теги со ссылками
            Document doc = connection.execute().parse();
            Elements links = doc.select("a[href]");

            for (Element link : links)
            {
                if (!data.getTotal().isIndexing()) break;
                String url = link.attr("abs:href");
                if (isLinkIgnore(url)) continue;
                if (uniqueURL.add(url))
                {
                    System.out.println(url);
                    synchronized (parserTasks) {
                        RecursiveLinkParser task = new RecursiveLinkParser(url, data);
                        parserTasks.add(task);
                        task.fork();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (RecursiveLinkParser parserTask : parserTasks) {
            parserTask.join();
        }
    }

    public boolean isLinkIgnore(String url)
    {
        if (url.isEmpty()) return true;
        if (!url.startsWith(urlSite)) return true;
        if (url.endsWith(".pdf")) return true;
        if (url.contains("#")) return true;
        if (url.contains("\\")) return true;
        return url.contains(" ");
    }

    public static String smartUrl(String site) {
        String urlPattern = null;
        try {
            Document doc = Jsoup.connect(site).get();
            Element link = doc.select("a[href]").first();
            if (link != null)
                urlPattern = link.attr("abs:href");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlPattern;
    }


}

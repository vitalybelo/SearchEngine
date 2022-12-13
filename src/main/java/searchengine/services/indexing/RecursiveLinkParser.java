package searchengine.services.indexing;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.model.Status;

import java.util.*;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class RecursiveLinkParser extends RecursiveAction {

    public final static int TIME_OUT = 5000;
    public final static int MAX_URLS = 10;
    public static TreeSet<String> uniqueURL = new TreeSet<>();;

    private final UserAgent userAgent = new UserAgent();
    private final DataPackage data;
    private final String urlSite;

    public RecursiveLinkParser(@NotNull String urlSite, DataPackage dataPackage)
    {
        this.data = dataPackage;
        this.urlSite = urlSite;
    }

    @Override
    protected void compute()
    {
        if (!data.isIndexing()) return;
        //if (data.getSiteEntity().getPages().size() >= MAX_URLS) return;
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
            String content = doc.body().toString();

            for (Element link : links)
            {
                if (!data.isIndexing()) break;
                //if (data.getSiteEntity().getPages().size() >= MAX_URLS) break;
                String url = link.attr("abs:href");
                if (isLinkIgnore(url)) continue;
                PageEntity page = new PageEntity(url, 200, content);
                //if (uniqueURL.add(url))
                if (data.getSiteEntity().getPages().add(page))
                {
                    // запись PAGE в таблицу SITE
                    data.getSiteEntity().setStatus_time(new Date(System.currentTimeMillis()));
                    data.getSiteEntity().addPage(new PageEntity(url, 200, content));
                    //data.getSiteEntityRepository().save(data.getSiteEntity());
                    // переходим по ссылке и делаем тоже самое там
                    synchronized (parserTasks) {
                        RecursiveLinkParser task = new RecursiveLinkParser(url, data);
                        parserTasks.add(task);
                        task.fork();
                    }
                    System.out.println(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (RecursiveLinkParser parserTask : parserTasks) {
            parserTask.join();
        }
    }

    public SiteEntity getResult()
    {
        if (data.isIndexing()) {
            data.getSiteEntity().setStatus(Status.INDEXED);
        } else {
            data.getSiteEntity().setStatus(Status.FAILED);
        }
        return data.getSiteEntity();
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

    public static String smartUrl(String site)
    {
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

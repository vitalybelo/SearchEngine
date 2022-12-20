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

import java.net.URL;
import java.util.*;
import java.util.concurrent.RecursiveAction;

import static java.lang.Thread.sleep;

public class RecursiveLinkParser extends RecursiveAction {

    public final static int TIME_OUT = 60_000;
    public final static int MAX_URLS = 100;
    public static final TreeSet<String> uniqueURL = new TreeSet<>(); // should be static !!!

    private final UserAgent userAgent = new UserAgent();
    private final DataPackage data;
    private final String urlSite;

    public RecursiveLinkParser(@NotNull String urlSite, DataPackage dataPackage) {
        this.data = dataPackage;
        this.urlSite = urlSite;
    }

    @Override
    protected void compute()
    {
        if (!data.isIndexing()) return;
        if (uniqueURL.size() >= MAX_URLS) return;
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
            // парсим в документ страницу, получаем список всех тегов со ссылками
            Document document = connection.execute().parse();
            Elements links = document.select("a[href]");

            // для каждого тега ссылок
            for (Element link : links)
            {
                if (!data.isIndexing()) break;
                if (uniqueURL.size() >= MAX_URLS) break;
                String url = link.attr("abs:href");
                if (isLinkIgnore(url)) continue;
                if (!url.endsWith("/")) url += "/";
                // добавляем только уникальные ссылки
                if (uniqueURL.add(url))
                {
                    System.out.println(data.getSiteEntity().getName() + " ---> " + url);

                    // читаем текс и код ответа с выбранного url
                    Document doc = Jsoup.connect(url).ignoreHttpErrors(true).get();
                    int code = doc.connection().response().statusCode();
                    String content = doc.body().text(); //.replaceAll("[\\p{Cntrl}^\r\n\t]+", "");
                    PageEntity page = new PageEntity(data.getSiteEntity(), url, code, content);
                    data.getSiteEntity().setStatus_time(new Date(System.currentTimeMillis()));

                    // запись в таблицу PAGE в таблицу SITE
                    data.getSiteEntity().addPage(page);
                    // рекурсивно переходим по ссылке
                    RecursiveLinkParser task = new RecursiveLinkParser(url, data);
                    // добавляем задачу и ставим в очередь в pool
                    synchronized (parserTasks)
                    {// ----------------------------- SYNCHRO
                        parserTasks.add(task);
                        task.fork();
                    }// ----------------------------- SYNCHRO
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // запускаем задачи
        synchronized (parserTasks)
        {// ----------------------------- SYNCHRO
            for (RecursiveLinkParser parserTask : parserTasks)
                parserTask.join();
        }// ----------------------------- SYNCHRO
    }

    public SiteEntity getResult()
    {
        if (data.isIndexing()) {
            data.getSiteEntity().setStatus(Status.INDEXED);
            data.getSiteEntity().setLast_error("Завершено без ошибок");
        } else {
            data.getSiteEntity().setStatus(Status.FAILED);
            data.getSiteEntity().setLast_error("Прервано пользователем");
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
        URL url = null;
        try {
            Connection.Response response = Jsoup.connect(site).execute();
            url = response.url();
        } catch (Exception e) { e.printStackTrace(); }
        assert url != null;
        return url.toString();
    }


}

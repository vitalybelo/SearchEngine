package searchengine.services.indexing;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RecursiveLinkParser extends RecursiveTask<TreeSet<String>> {

    public final static int TIME_OUT = 5000;
    public final static int MAX_URLS = 5000;
    public static TreeSet<String> uniqueURL = new TreeSet<>();
    public static AtomicInteger urlCounter = new AtomicInteger();
    public static AtomicBoolean indexing = new AtomicBoolean();

    private final UserAgent userAgent = new UserAgent();
    private final String urlSite;

    public RecursiveLinkParser(@NotNull String urlSite) {
        this.urlSite = urlSite;
    }

    @Override
    protected TreeSet<String> compute()
    {
        if (!indexing.get()) return uniqueURL;
        List<RecursiveLinkParser> parserTasks = new ArrayList<>();

        try {
            // пауза частоты индексирования
            Thread.sleep(200);
            // подкулючаемся к странице
            Connection connection = Jsoup
                    .connect(urlSite)
                    .userAgent(userAgent.get())
                    .referrer("https://google.com")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(TIME_OUT)
                    .newRequest();
            // парсим в документ страницу сайта и выбираем тэги ссылок
            Document doc = connection.execute().parse();
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                if (urlCounter.get() >= MAX_URLS) break;
                String url = link.attr("abs:href");
                if (isLinkIgnore(url)) continue;
                if (uniqueURL.add(url)) {
                    urlCounter.incrementAndGet();
                        System.out.println(url);
                    RecursiveLinkParser task = new RecursiveLinkParser(url);
                    task.fork();
                    parserTasks.add(task);
                }
            }
        } catch (InterruptedException ignored) {
            System.out.println("Recursive thread stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (RecursiveLinkParser parserTask : parserTasks) {
            parserTask.join();
        }
        return uniqueURL;
    }

    public boolean isLinkIgnore(String url)
    {
        if (url == null) return true;
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

package searchengine.services.indexing;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class RecursiveLinkParser extends RecursiveTask<TreeSet<String>> {

    static int TIME_OUT = 3000;
    static int MAX_URLS = 50;
    private final AtomicInteger urlCounter = new AtomicInteger();
    private final TreeSet<String> uniqueURL = new TreeSet<>();

    private final String site;

    public RecursiveLinkParser(String site) {
        this.site = site;
    }

    @Override
    protected TreeSet<String> compute() {

        UserAgent userAgent = new UserAgent();

        List<RecursiveLinkParser> parserTasks = new ArrayList<>();
        try {
            Thread.sleep(150);
            Connection connection = Jsoup
                    .connect(site)
                    .userAgent(userAgent.get())
                    .referrer("https://www.google.com")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .timeout(TIME_OUT)
                    .newRequest();
            Document doc = connection.execute().parse();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                if (urlCounter.get() >= MAX_URLS) break;
                String url = link.attr("abs:href");
                if (isLinkIgnore(url)) continue;
                if (!url.endsWith("/")) url += "/";
                if (uniqueURL.add(url)) {
                    urlCounter.incrementAndGet();
                        System.out.println(url);
                    RecursiveLinkParser task = new RecursiveLinkParser(url);
                    task.fork();
                    parserTasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (RecursiveLinkParser parserTask : parserTasks) {
            parserTask.join();
        }
        return uniqueURL;
    }

    public boolean isLinkIgnore(String url) {
        if (url.isEmpty()) return true;
        if (!url.startsWith(site)) return true;
        if (url.endsWith(".pdf")) return true;
        if (url.contains("#")) return true;
        return url.contains(" ");
    }

    public int getUrlCounter() {
        return urlCounter.get();
    }
}

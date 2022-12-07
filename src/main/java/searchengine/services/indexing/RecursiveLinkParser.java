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

    private final static String USER_AGENT1 = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.5249.119 Safari/537.36";
    private final static String USER_AGENT2 = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:103.0) Gecko/20100101 Firefox/103.0";
    private final static String USER_AGENT3 = "Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.43 (KHTML, like Gecko) Chrome/102.0.5005.115 Safari/537.36 OPR/88.0.4412.74";
    private final static String REFERRER = "https://www.google.com";
    private final static int TIME_OUT = 3000;
    private final static int MAX_URLS = 50;
    public static AtomicInteger urlCounter = new AtomicInteger();
    public static TreeSet<String> uniqueURL = new TreeSet<>();
    private final String site;

    public RecursiveLinkParser(String site) {
        this.site = site;
    }

    @Override
    protected TreeSet<String> compute() {

        List<RecursiveLinkParser> parserTasks = new ArrayList<>();
        try {
            Thread.sleep(150);
            Connection connection = Jsoup
                    .connect(site)
                    .userAgent(USER_AGENT2)
                    .referrer(REFERRER)
                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
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

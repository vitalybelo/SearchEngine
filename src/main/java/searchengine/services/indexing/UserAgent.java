package searchengine.services.indexing;

import java.util.concurrent.ThreadLocalRandom;

public class UserAgent {

    private final String[] userAgentList =
    {
            "Googlebot-News",
            "Googlebot-Image/1.0",
            "AdsBot-Google-Mobile-Apps",
            "Googlebot/2.1 (+http://www.google.com/bot.html)",
            "Mediapartners-Google/2.1; +http://www.google.com/bot.html",
            "Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (Linux; Android 5.0; SM-G920A) AppleWebKit (KHTML, like Gecko) Chrome Mobile Safari (compatible; AdsBot-Google-Mobile; +http://www.google.com/mobile/adsbot.html)",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (compatible; YandexAccessibilityBot/3.0; +http://yandex.com/bots",
            "Mozilla/5.0 (compatible; YandexDirectDyn/1.0; +http://yandex.com/bots",
            "Mozilla/5.0 (compatible; YandexImages/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexVideo/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexVideoParser/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexMedia/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexBlogs/0.99; robot; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexFavicons/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexWebmaster/2.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexPagechecker/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexImageResizer/2.0; +http://yandex.com/bots",
            "Mozilla/5.0 (compatible; YandexAdNet/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexDirect/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YaDirectFetcher/1.0; Dyatel; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexCalendar/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexSitelinks; Dyatel; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexMetrika/2.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexNews/4.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexCatalog/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexMarket/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexVertis/3.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexForDomain/1.0; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; YandexBot/3.0; MirrorDetector; +http://yandex.com/bots)",
            "Mozilla/5.0 (compatible; Mail.RU_Bot/Fast/2.0)",
            "StackRambler/2.0 (MSIE incompatible",
            "Mozilla/5.0 (compatible; Yahoo! Slurp; http://help.yahoo.com/help/us/ysearch/slurp)",
            "Mozilla/5.0 (compatible; Yahoo! Slurp/3.0; http://help.yahoo.com/help/us/ysearch/slurp)",
            "msnbot/1.1 (+http://search.msn.com/msnbot.htm)",
            "msnbot-media/1.0 (+http://search.msn.com/msnbot.htm)",
            "msnbot-media/1.1 (+http://search.msn.com/msnbot.htm)",
            "msnbot-news (+http://search.msn.com/msnbot.htm)",
            "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)",
            ""
    };

    public String get()
    {
        int bound = userAgentList.length - 1;
        int index = ThreadLocalRandom.current().nextInt(0, bound);
        if (index >= 0 && index < bound)
            return userAgentList[index];
        return userAgentList[5];
    }

    public String getAt(int index)
    {
        if (index < 0) index = 0;
        int last = userAgentList.length - 1;
        if (index > last) index = last;
        return userAgentList[index];
    }
}

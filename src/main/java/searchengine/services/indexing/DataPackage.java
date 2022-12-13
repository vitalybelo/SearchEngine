package searchengine.services.indexing;

import lombok.Getter;
import lombok.Setter;
import searchengine.dto.statistics.StatisticsData;
import searchengine.model.SiteEntity;
import searchengine.repository.PageEntityRepository;
import searchengine.repository.SiteEntityRepository;

@Getter
@Setter
public class DataPackage {

    private StatisticsData statisticsData;
    private SiteEntity siteEntity;
    private SiteEntityRepository siteEntityRepository;
    private PageEntityRepository pageEntityRepository;

    public DataPackage(StatisticsData statisticsData,
                       SiteEntity siteEntity,
                       SiteEntityRepository siteEntityRepository,
                       PageEntityRepository pageEntityRepository)
    {
        this.siteEntity = siteEntity;
        this.statisticsData = statisticsData;
        this.siteEntityRepository = siteEntityRepository;
        this.pageEntityRepository = pageEntityRepository;
    }

    public boolean isIndexing() {
        return statisticsData.getTotal().isIndexing();
    }
}

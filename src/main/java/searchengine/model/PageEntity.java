package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Getter
@Setter
@Entity(name = "page")
@Table(indexes = {@Index(name = "idx_path", columnList = "path", unique = true)})
/**
 * !!! ВНИМАНИЕ !!! база данных search_engine должна иметь
 *      character-set-server=utf8mb4
 *      collation-server=utf8mb4_unicode_ci
 *  для корректной работы при сохранении в базе текстов страниц
 */
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ID веб-сайта из таблицы site
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    @JsonIgnore
    private SiteEntity site;

    // адрес страницы от корня сайта (должен начинаться со слэша, например: /news/372189/
    @Column(columnDefinition = "TEXT", nullable = false, length = 255)
    private String path;

    // код HTTP-ответа, полученный при запросе страницы (например, 200, 404, 500 или другие)
    @Column(nullable = true)
    private int code;

    // контент страницы (HTML-код)
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @JsonIgnore
    public SiteEntity getSite() {
        return site;
    }

    public PageEntity(SiteEntity site, String path, int code, String content) {
        this.site = site;
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public PageEntity(String path, int code, String content) {
        this.path = path;
        this.code = code;
        this.content = content;
    }

    public PageEntity() {
        this(null, "", 200, "");
    }

    @Override
    public String toString() {
        return path;
    }
}

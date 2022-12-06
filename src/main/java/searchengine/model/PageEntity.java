package searchengine.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "page")
@Getter
@Setter
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ID веб-сайта из таблицы site
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "site_id", nullable = false)
    private SiteEntity site;

    // адрес страницы от корня сайта (должен начинаться со слэша, например: /news/372189/
    @Column(columnDefinition = "TEXT", nullable = false)
    private String path;

    // код HTTP-ответа, полученный при запросе страницы (например, 200, 404, 500 или другие
    @NonNull
    private int code;

    // контент страницы (HTML-код)
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;



}

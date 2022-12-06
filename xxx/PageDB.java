package searchengine.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;
import searchengine.xxx.Site;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
public class PageDB {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Site site_id;     // ID веб-сайта из таблицы site

    @Column(columnDefinition = "TEXT", nullable = false)
    private String path;        // адрес страницы от корня (должен начинаться слэшем, например: /news/372189/)

    @NonNull
    private int code;           // код HTTP-ответа, полученный при запросе страницы (например, 200, 305, 404 ...)

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;     // контент страницы (HTML-код)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PageDB pageDB = (PageDB) o;
        return Objects.equals(id, pageDB.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

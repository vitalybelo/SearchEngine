package searchengine.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class SiteDB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, nullable = false)
    private int id;

    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private Indexing status;            // статус состояние: Индексируется, Индексация выполнена, Индексация провалена

    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime status_time;  // дата и время получения статуса (обновляется при добавлении новой страницы в PageDB

    @Column(columnDefinition = "TEXT", nullable = true)
    private String last_error;          // текст ошибки индексации или NULL, если её не было

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;                 // адрес главной страницы сайта

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;                // имя сайта

    public SiteDB() {}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SiteDB siteDB = (SiteDB) o;
        return Objects.equals(id, siteDB.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

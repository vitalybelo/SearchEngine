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
@RequiredArgsConstructor
@Entity
public class SiteDB {

    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    private Indexing status;            // статус состояние: Индексируется, Индексация выполнена, Индексация провалена

    @NonNull
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime status_time;  // дата и время получения статуса (обновляется при добавлении новой страницы в PageDB

    @Nullable
    @Column(columnDefinition = "TEXT")
    private String last_error;          // текст ошибки индексации или NULL, если её не было

    @NonNull
    @Column(columnDefinition = "VARCHAR(255)")
    private String url;                 // адрес главной страницы сайта

    @NonNull
    @Column(columnDefinition = "VARCHAR(255)")
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

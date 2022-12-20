package searchengine.model;

import lombok.*;
import javax.persistence.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "site")
/**
 * !!! ВНИМАНИЕ !!! база данных search_engine должна иметь
 *      character-set-server=utf8mb4,
 *      collation-server=utf8mb4_unicode_ci
 *  для корректной работы при сохранении в базе текстов страниц
 */
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // адрес главной страницы сайта
    @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String url;

    // имя сайта - возможно заголовок
    @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String name;

    // текущий статус полной индексации сайта (в процессе - закончена - провалена)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private Status status;

    // дата и время статуса (в случае статуса INDEXING дата и время должны обновляться
    // регулярно при добавлении каждой новой страницы в индекс)
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date status_time;

    // TEXT — текст ошибки индексации или NULL, если её не было
    @Column(columnDefinition = "TEXT", nullable = true)
    private String last_error;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PageEntity> pages = new HashSet<>();

    public void addPage(PageEntity page) {
        this.pages.add(page);
        page.setSite(this);
    }

    public Set<PageEntity> getPages() {
        return pages;
    }

    public SiteEntity(String name, String url) {
        this.name = name;
        this.url = url;
        status = Status.INDEXING;
        status_time = new Date(System.currentTimeMillis());
        last_error = null;
        pages.clear();
    }

    @Override
    public String toString() {
        return "\tURL   = " + url + '\n' +
               "\tname  = " + name + '\n' +
               "\tdate  = " + status_time + '\n' +
               "\tstatus = " + status + '\n' +
               "\tTotal pages = " + pages.size();
    }
}

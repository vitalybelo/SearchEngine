package searchengine.model;

import lombok.*;
import javax.persistence.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "site")
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

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PageEntity> pages = new HashSet<>();

    public void addPage(PageEntity page) {
        this.pages.add(page);
        page.setSite(this);
    }

    public void removePage(PageEntity page) {
        this.pages.remove(page);
        page.setSite(null);
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
    }

    @Override
    public String toString() {
        return "\tURL   = " + url + '\n' +
               "\tname  = " + name + '\n' +
               "\tdate  = " + status_time + '\n' +
               "\tTotal pages = " + pages.size();
    }
}

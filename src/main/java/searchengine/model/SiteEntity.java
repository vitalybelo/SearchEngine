package searchengine.model;

import lombok.*;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "site")
@Setter
@Getter
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // адрес главной страницы сайта
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    // имя сайта - возможно заголовок
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
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
    private List<PageEntity> pages;

    public List<PageEntity> getPages() {
        return pages;
    }

}

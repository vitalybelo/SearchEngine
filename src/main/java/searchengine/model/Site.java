package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

//    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
//    private Status status;            // статус состояние: Индексируется, Индексация выполнена, Индексация провалена

//    @Column(columnDefinition = "DATETIME", nullable = false)
//    private LocalDateTime status_time;  // дата и время получения статуса (обновляется при добавлении новой страницы в PageDB

//    @Column(columnDefinition = "TEXT", nullable = true)
    private String last_error;          // текст ошибки индексации или NULL, если её не было

//    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;                 // адрес главной страницы сайта

//    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;                // имя сайта

}

package searchengine.model;

import lombok.*;
import javax.persistence.*;

@Entity(name = "site")
@Setter
@Getter
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;


}

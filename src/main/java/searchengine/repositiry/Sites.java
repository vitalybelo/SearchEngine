package searchengine.repositiry;

import lombok.*;
import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
public class Sites {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String url;
    private String name;

}

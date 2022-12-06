package searchengine.repositiry;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SitesRepository extends CrudRepository<Sites, Integer> {

}


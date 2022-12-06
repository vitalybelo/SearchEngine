package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageDBRepository extends CrudRepository<PageDB, Integer> {
}
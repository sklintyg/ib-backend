package se.inera.intyg.intygsbestallning.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;

public interface InternForfraganRepository extends CrudRepository<InternForfragan, Long> {
}

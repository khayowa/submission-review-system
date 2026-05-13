package cos730.submission_review_system.repository;

import cos730.submission_review_system.domain.EvaluationOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationOutcomeRepository extends JpaRepository<EvaluationOutcome, Long>
{
}

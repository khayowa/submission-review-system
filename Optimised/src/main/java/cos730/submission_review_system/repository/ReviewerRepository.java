package cos730.submission_review_system.repository;

import cos730.submission_review_system.domain.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerRepository extends JpaRepository<Reviewer, Long>
{

}

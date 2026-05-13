package cos730.submission_review_system.repository;

import cos730.submission_review_system.domain.ReviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewResultRepository extends JpaRepository<ReviewResult, Integer>
{
}

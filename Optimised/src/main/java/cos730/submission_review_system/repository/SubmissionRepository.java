package cos730.submission_review_system.repository;

import cos730.submission_review_system.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long>
{
}

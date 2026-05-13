package cos730.submission_review_system.command;

import cos730.submission_review_system.domain.EEvaluationOutcome;
import cos730.submission_review_system.domain.ReviewAssignment;
import cos730.submission_review_system.domain.ReviewResult;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.repository.ReviewResultRepository;
import cos730.submission_review_system.repository.ReviewerRepository;
import cos730.submission_review_system.service.EvaluationService;
import cos730.submission_review_system.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EvaluationManager
{
  private final EvaluationService evaluationService;
  private final ReviewResultRepository reviewResultRepository;


  public EvaluationManager(EvaluationService evaluationService,
                           ReviewResultRepository reviewResultRepository)
  {
    this.evaluationService = evaluationService;
    this.reviewResultRepository = reviewResultRepository;
  }

  public EEvaluationOutcome startEvaluation(List<Reviewer> reviewers, Submission submission)
  {
    for (Reviewer r : reviewers)
    {
      if (r.getLastSubmittedScore() == null)
      {
        int score = ThreadLocalRandom.current().nextInt(35,80);
        submitScore(r, score);
        saveScore(submission, score);
      }
    }
    return evaluationService.evaluate(reviewers, submission);
  }

  /**
   * Submits a score for a review.
   */
  public void submitScore(Reviewer reviewer, int score)
  {
    if (score < 0 || score > 100)
    {
      throw new IllegalArgumentException("Score must be between 0 and 100");
    }
    reviewer.setLastSubmittedScore(score);
    int currentLoad = reviewer.getCurrentLoad();

    if (currentLoad > 0)
    {
      reviewer.setCurrentLoad(currentLoad--);
    }
  }

  public ReviewResult saveScore(Submission submission, int score)
  {
    Long submissionId = submission.getId();
    String comments = "Baseline evaluation for research output.";

    ReviewResult rr = new ReviewResult(
            submissionId,
            score,
            comments
    );
    return reviewResultRepository.save(rr);
  }
}

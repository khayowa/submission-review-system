package cos730.submission_review_system.command;

import cos730.submission_review_system.domain.EvaluationOutcome;
import cos730.submission_review_system.domain.ReviewResult;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.service.NotificationService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EvaluationManager
{
  private final NotificationService notificationService;
  private final JdbcTemplate jdbcTemplate;

  private static final int ACCEPT_THRESHOLD = 70;
  private static final int REJECT_THRESHOLD = 50;
  private static final int CONSENSUS_SPREAD = 15;

  public EvaluationManager(NotificationService notificationService, JdbcTemplate jdbcTemplate)
  {
    this.notificationService = notificationService;
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Entry point for evaluation (baseline orchestration).
   */
  public EvaluationOutcome evaluate(List<Reviewer> reviewers, Submission submission)
  {
    for (Reviewer r : reviewers)
    {
      if (r.getLastSubmittedScore() == null)
      {
        int score = ThreadLocalRandom.current().nextInt(15,40);
        r.submitScore(score);

        jdbcTemplate.update("INSERT INTO reviewer(display_name, email, expertise_tags, active, current_load) VALUES (?, ?, ?, ?, ?)",
                r.getDisplayName(), r.getEmail(), r.getExpertiseTags(), r.isActive(), r.getCurrentLoad());

        saveScore(submission, score);
      }
    }

    EvaluationOutcome outcome = applyRules(reviewers);

    if (outcome == EvaluationOutcome.ACCEPT)
    {
      notifyAcceptance();
    }
    else if (outcome == EvaluationOutcome.REJECT)
    {
      notifyRejection();
    }
    else
    {
      notifyRevision();
    }
    return outcome;
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

    jdbcTemplate.update("INSERT INTO review_result(submission_id, score, comments) VALUES (?, ?, ?)",
            rr.getSubmissionId(), rr.getScore(), rr.getComments());

    return rr;
  }


  /**
   * Calculates average reviewer score.
   * Baseline flaw: null scores count as 0.
   */
  public double calculateAverage(List<Reviewer> reviewers)
  {
    if (reviewers == null || reviewers.isEmpty())
    {
      return 0.0;
    }

    int total = 0;
    int count = 0;

    for (Reviewer reviewer : reviewers)
    {
      Integer score = reviewer.getLastSubmittedScore();
      if (score == null)
      {
        score = 0;
      }
      total += score;
      count++;
    }

    return (double) total / count;
  }

  /**
   * Applies decision rules (baseline).
   * - recalculates average
   * - calls consensus logic
   */
  public EvaluationOutcome applyRules(List<Reviewer> reviewers)
  {
    double recalculatedAverage = calculateAverage(reviewers);
    boolean consensus = checkConsensus(reviewers);

    if (consensus && recalculatedAverage >= ACCEPT_THRESHOLD)
    {
      return EvaluationOutcome.ACCEPT;
    }

    if (recalculatedAverage < REJECT_THRESHOLD)
    {
      return EvaluationOutcome.REJECT;
    }

    return EvaluationOutcome.REVISION;
  }

  /**
   * Consensus exists if reviewers do not diverge too much.
   */
  public boolean checkConsensus(List<Reviewer> reviewers)
  {
    if (reviewers == null || reviewers.isEmpty())
    {
      return false;
    }

    Integer min = null;
    Integer max = null;

    for (Reviewer reviewer : reviewers)
    {
      Integer score = reviewer.getLastSubmittedScore();

      if (score == null)
      {
        score = 0;
      }

      if (min == null || score < min)
      {
        min = score;
      }
      if (max == null || score > max)
      {
        max = score;
      }
    }

    return (max - min) <= CONSENSUS_SPREAD;
  }

  /* ========= Notifications ========= */

  public void notifyAcceptance()
  {
    notificationService.notifyAcceptance();
  }

  public void notifyRejection()
  {
    notificationService.notifyRejection();
  }

  public void notifyRevision()
  {
    notificationService.notifyRevision();
  }
}

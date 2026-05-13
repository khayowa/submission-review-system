package cos730.submission_review_system.service;

import cos730.submission_review_system.domain.EEvaluationOutcome;
import cos730.submission_review_system.domain.EvaluationOutcome;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.repository.EvaluationOutcomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService
{
  private final EvaluationOutcomeRepository outcomeRepository;

  private static final int ACCEPT_THRESHOLD = 70;
  private static final int REJECT_THRESHOLD = 40;
  private static final int CONSENSUS_SPREAD = 15;

  public EvaluationService(EvaluationOutcomeRepository outcomeRepository) {
    this.outcomeRepository = outcomeRepository;
  }

  /**
   * For evaluation.
   */
  public EEvaluationOutcome evaluate(List<Reviewer> reviewers, Submission submission)
  {
    EEvaluationOutcome outcome = applyRules(reviewers);
    saveOutcome(submission.getId(), outcome.toString(), calculateAverage(reviewers));
    return outcome;
  }

  public EvaluationOutcome saveOutcome(Long submissionId, String decision, Double averageScore) {

    EvaluationOutcome outcome = new EvaluationOutcome(submissionId, decision, averageScore);
    return outcomeRepository.save(outcome);
  }

  /**
   * Calculates average reviewer score.
   *
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
   * Applies decision rules .
   *
   * - recalculates average
   * - calls consensus logic
   */
  public EEvaluationOutcome applyRules(List<Reviewer> reviewers)
  {
    double recalculatedAverage = calculateAverage(reviewers);
    boolean consensus = checkConsensus(reviewers);

    if (consensus && recalculatedAverage >= ACCEPT_THRESHOLD)
    {
      return EEvaluationOutcome.ACCEPT;
    }

    if (recalculatedAverage < REJECT_THRESHOLD)
    {
      return EEvaluationOutcome.REJECT;
    }

    return EEvaluationOutcome.REVISION;
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
}

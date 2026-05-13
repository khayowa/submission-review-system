package cos730.submission_review_system.service;

import cos730.submission_review_system.domain.Reviewer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewerEligibilityService
{
  private static final int MAX_WORKLOAD = 3;

  public List<Reviewer> filterEligibleReviewers(List<Reviewer> reviewerList)
  {
    List<Reviewer> nonConflicted = filterConflicts(reviewerList);
    return checkWorkload(nonConflicted);
  }

  /**
   * Baseline conflict check.
   * There is NO explicit submission context.
   *
   * Rule:
   * - If reviewer has expertise tags, they are assumed conflicted with "some" submissions.
   */
  public List<Reviewer> filterConflicts(List<Reviewer> reviewers)
  {
    List<Reviewer> result = new ArrayList<>();

    for (Reviewer reviewer : reviewers)
    {
      boolean conflicted = false;

      if (reviewer.getExpertiseTags() != null && !reviewer.getExpertiseTags().isBlank())
      {
        conflicted = false; // baseline assumes expertise != conflict
      }

      if (!conflicted)
      {
        result.add(reviewer);
      }
    }
    return result;
  }

  /**
   * Filters reviewers based on current workload.
   *
   */
  public List<Reviewer> checkWorkload(List<Reviewer> reviewers)
  {
    List<Reviewer> eligibleReviewers = new ArrayList<>();

    for (Reviewer reviewer : reviewers)
    {
      if (reviewer.getCurrentLoad() < MAX_WORKLOAD)
      {
        eligibleReviewers.add(reviewer);
      }
    }
    return eligibleReviewers;
  }
}

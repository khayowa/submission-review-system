package cos730.submission_review_system.command;

import cos730.submission_review_system.domain.ReviewAssignment;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.repository.ReviewAssignmentRepository;
import cos730.submission_review_system.repository.ReviewerRepository;
import cos730.submission_review_system.service.ReviewerEligibilityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewerManager
{
  private final ReviewerRepository reviewerRepository;
  private final ReviewAssignmentRepository reviewAssignmentRepository;
  private final ReviewerEligibilityService reviewerEligibilityService;

  public ReviewerManager(ReviewerRepository reviewerRepository,
                         ReviewAssignmentRepository reviewAssignmentRepository ,
                         ReviewerEligibilityService reviewerEligibilityService)
  {
    this.reviewerRepository = reviewerRepository;
    this.reviewAssignmentRepository = reviewAssignmentRepository;
    this.reviewerEligibilityService = reviewerEligibilityService;
  }

  /**
   * - Fetches all reviewers from DB
   * - Filters inactive reviewers
   * - Calls service for filtering
   *
   */
  public List<Reviewer> getAvailableReviewers()
  {
    List<Reviewer> allReviewers = reviewerRepository.findAll();
    List<Reviewer> activeReviewers = new ArrayList<>();

    for (Reviewer reviewer : allReviewers)
    {
      if (reviewer.isActive()) {
        activeReviewers.add(reviewer);
      }
    }
    return reviewerEligibilityService.filterEligibleReviewers(activeReviewers);
  }

  public List<Reviewer> assignReviewers(Submission submission, List<Reviewer> available)
  {
    List<Reviewer> assigned = new ArrayList<>();
    int toAssign = Math.min(3, available.size());

    for (int i = 0; i < toAssign; i++)
    {
      Reviewer r = available.get(i);
      r.assignReview();

      // store assignment
      ReviewAssignment ar = new ReviewAssignment(submission.getId(), r.getId(), "ASSIGNED");
      reviewAssignmentRepository.save(ar);
      assigned.add(r);
    }
    return assigned;
  }
}

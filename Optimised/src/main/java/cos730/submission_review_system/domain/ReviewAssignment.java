package cos730.submission_review_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_assignment")
public class ReviewAssignment
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "submission_id")
  private Long submissionId;

  @Column(name = "reviewer_id")
  private Long reviewerId;

  @Column(name = "assigned_at")
  private LocalDateTime assignedAt;

  @Column(name = "assignment_status")
  private String assignmentStatus;

  protected ReviewAssignment() { }

  public ReviewAssignment(Long submissionId, Long reviewerId, String assignmentStatus) {
    this.submissionId = submissionId;
    this.reviewerId = reviewerId;
    this.assignmentStatus = assignmentStatus;
    this.assignedAt = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public Long getSubmissionId() { return submissionId; }
  public Long getReviewerId() { return reviewerId; }
  public LocalDateTime getAssignedAt() { return assignedAt; }
  public String getAssignmentStatus() { return assignmentStatus; }
}

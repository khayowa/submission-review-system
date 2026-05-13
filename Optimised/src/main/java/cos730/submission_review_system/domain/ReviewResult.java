package cos730.submission_review_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_result")
public class ReviewResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "submission_id")
  private Long submissionId;

  @Column(name = "score")
  private int score;

  @Column(name = "comments")
  private String comments;

  @Column(name = "evaluated_at")
  private LocalDateTime evaluatedAt;

  protected ReviewResult() {}

  public ReviewResult(Long submissionId, int score, String comments) {
    this.submissionId = submissionId;
    this.score = score;
    this.comments = comments;
    this.evaluatedAt = LocalDateTime.now();
  }

  public int getScore() {
    return score;
  }
}
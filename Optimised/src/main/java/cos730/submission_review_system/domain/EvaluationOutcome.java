package cos730.submission_review_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_outcome")
public class EvaluationOutcome {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "submission_id", nullable = false)
  private Long submissionId;

  @Column(name = "final_decision", nullable = false)
  private String finalDecision;

  @Column(name = "average_score")
  private Double averageScore;

  @Column(name = "evaluated_at")
  private LocalDateTime evaluatedAt;

  public EvaluationOutcome() {}

  public EvaluationOutcome(Long submissionId, String finalDecision, Double averageScore) {
    this.submissionId = submissionId;
    this.finalDecision = finalDecision;
    this.averageScore = averageScore;
    this.evaluatedAt = LocalDateTime.now();
  }

}


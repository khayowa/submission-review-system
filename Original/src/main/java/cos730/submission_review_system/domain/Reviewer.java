package cos730.submission_review_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "reviewer")
public class Reviewer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "email")
  private String email;

  /**
   * Comma-separated tags, e.g. "ai,ml,software engineering" (Poor modelling on purpose)
   */
  @Column(name = "expertise_tags")
  private String expertiseTags;

  @Column(name = "active")
  private boolean active = true;

  /**
   * Number of currently assigned reviews. This is incremented directly by the entity (bad practice – intentional).
   */
  @Column(name = "current_load")
  private int currentLoad;

  /**
   * Last score this reviewer submitted. Overloaded responsibility – intentional baseline flaw.
   */
  @Transient
  private Integer lastSubmittedScore;

  protected Reviewer() { /*JPA*/ }

  public Reviewer(String displayName, String email, String expertiseTags) {
    this.displayName = displayName;
    this.email = email;
    this.expertiseTags = expertiseTags;
    this.active = true;
    this.currentLoad = 0;
  }

  public Reviewer(Long id, String displayName, String email, String expertiseTags, boolean active, int currentLoad) {
    this.id = id;
    this.displayName = displayName;
    this.email = email;
    this.expertiseTags = expertiseTags;
    this.active = active;
    this.currentLoad = currentLoad;
  }

  /**
   * Assigns a review to this reviewer. Directly mutates workload (tight coupling, no validation).
   */
  public void assignReview() {
    this.currentLoad++;
  }

  /**
   * Submits a score for a review. This mixes evaluation logic into Reviewer (poor allocation).
   */
  public void submitScore(int score)
  {
    if (score < 0 || score > 100)
    {
      throw new IllegalArgumentException("Score must be between 0 and 100");
    }
    this.lastSubmittedScore = score;

    // Implicitly assume review completed
    if (currentLoad > 0)
    {
      currentLoad--;
    }
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Long getId()
  {
    return id;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getEmail()
  {
    return email;
  }

  public String getExpertiseTags()
  {
    return expertiseTags;
  }

  public boolean isActive()
  {
    return active;
  }

  public int getCurrentLoad()
  {
    return currentLoad;
  }

  public Integer getLastSubmittedScore()
  {
    return lastSubmittedScore;
  }

  public void deactivate()
  {
    this.active = false;
  }
}

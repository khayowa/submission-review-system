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
   * Comma-separated tags, e.g. "ai,ml,software engineering"
   */
  @Column(name = "expertise_tags")
  private String expertiseTags;

  @Column(name = "active")
  private boolean active = true;

  /**
   * Number of currently assigned reviews.
   */
  @Column(name = "current_load")
  private int currentLoad;

  /**
   * Last score this reviewer submitted.
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

  /**
   * Assigns a review to this reviewer.
   */
  public void assignReview() {
    this.currentLoad++;
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

  public void setLastSubmittedScore(Integer lastSubmittedScore)
  {
    this.lastSubmittedScore = lastSubmittedScore;
  }

  public void setCurrentLoad(int currentLoad)
  {
    this.currentLoad = currentLoad;
  }
}

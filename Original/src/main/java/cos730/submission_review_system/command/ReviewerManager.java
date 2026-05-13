package cos730.submission_review_system.command;

import cos730.submission_review_system.domain.Reviewer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewerManager
{
  private final JdbcTemplate jdbcTemplate;
  private static final int MAX_WORKLOAD = 3;

  public ReviewerManager(JdbcTemplate jdbcTemplate)
  {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * BASELINE VERSION:
   * - Fetches all reviewers from DB
   * - Filters inactive reviewers
   * - Filters conflicts
   * - Checks workload
   *
   * Intentionally procedural and inefficient.
   */
  public List<Reviewer> getAvailableReviewers()
  {
    List<Reviewer> allReviewers = jdbcTemplate.query(
            "SELECT * FROM reviewer",
            (rs, rowNum) -> new Reviewer(
                    rs.getLong("id"),
                    rs.getString("display_name"),
                    rs.getString("email"),
                    rs.getString("expertise_tags"),
                    rs.getBoolean("active"),
                    rs.getInt("current_load")
            )
    );

    List<Reviewer> activeReviewers = new ArrayList<>();

    for (Reviewer reviewer : allReviewers)
    {
      if (reviewer.isActive())
      {
        activeReviewers.add(reviewer);
      }
    }

    List<Reviewer> nonConflicted = filterConflicts(activeReviewers);
    List<Reviewer> available = checkWorkload(nonConflicted);

    return available;
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
   * Uses a hard-coded threshold.
   */
  public List<Reviewer> checkWorkload(List<Reviewer> reviewers)
  {
    List<Reviewer> result = new ArrayList<>();

    for (Reviewer reviewer : reviewers)
    {
      if (reviewer.getCurrentLoad() < MAX_WORKLOAD)
      {
        result.add(reviewer);
      }
    }
    return result;
  }
}

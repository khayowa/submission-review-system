package cos730.submission_review_system.controller;

import cos730.submission_review_system.command.EvaluationManager;
import cos730.submission_review_system.command.ReviewerManager;
import cos730.submission_review_system.domain.EvaluationOutcome;
import cos730.submission_review_system.domain.ReviewAssignment;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.service.Validator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SubmissionController
{
  private final Validator validator;
  private final JdbcTemplate jdbcTemplate;
  private final ReviewerManager reviewerManager;
  private final EvaluationManager evaluationManager;

  public SubmissionController(Validator validator,
                              JdbcTemplate jdbcTemplate,
                              ReviewerManager reviewerManager,
                              EvaluationManager evaluationManager)
  {
    this.validator = validator;
    this.jdbcTemplate = jdbcTemplate;
    this.reviewerManager = reviewerManager;
    this.evaluationManager = evaluationManager;
  }

  @GetMapping("/")
  public String home(Model model)
  {
    return "submission-ui";
  }

  @PostMapping("/submit")
  public String submitArtefact(@RequestParam("file") MultipartFile file, Model model)
  {
    Validator.ValidationResult validationResult;
    try
    {
      validationResult = this.validator.validateFormat(file);
    }
    catch (RuntimeException ex)
    {
      model.addAttribute("errorMessage", ex.getMessage());
      return "submission-ui";
    }

    Submission submission = saveSubmission(validationResult);
    List<Reviewer> filteredReviewers = reviewerManager.getAvailableReviewers();

    if (filteredReviewers.isEmpty())
    {
      model.addAttribute("errorMessage", "No reviewers at the moment.");
      return "submission-ui";
    }

    List<Reviewer> assigned = assignReviewers(submission, filteredReviewers);
    EvaluationOutcome outcome = startEvaluation(assigned, submission);

    model.addAttribute("successMessage", "Submission saved (ID: " + submission.getId() + "). Assigned reviewers: " + assigned.size());
    model.addAttribute("outcome", outcome);
    return "submission-ui";
  }

  private Submission saveSubmission(Validator.ValidationResult validationResult)
  {
    Submission submission = new Submission(
            validationResult.fileName(),
            validationResult.fileName(),
            validationResult.contentType(),
            validationResult.sizeBytes(),
            validationResult.sha256(),
            "SUBMITTED"
    );

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO submission (title, original_filename, content_type, size_bytes, checksum_sha256, status) VALUES (?, ?, ?, ?, ?, ?)",
              Statement.RETURN_GENERATED_KEYS
      );

      ps.setString(1, submission.getTitle());
      ps.setString(2, submission.getOriginalFilename());
      ps.setString(3, submission.getContentType());
      ps.setLong(4, submission.getSizeBytes());
      ps.setString(5, submission.getChecksumSha256());
      ps.setString(6, submission.getStatus());

      return ps;
    }, keyHolder);

    Map<String, Object> keys = keyHolder.getKeys();
    submission.setId((Long) keys.get("ID"));
    return submission;
  }

  private List<Reviewer> assignReviewers(Submission submission, List<Reviewer> available)
  {
    List<Reviewer> assigned = new ArrayList<>();
    int toAssign = Math.min(3, available.size());

    for (int i = 0; i < toAssign; i++)
    {
      Reviewer r = available.get(i);
      r.assignReview();

      KeyHolder keyHolder = new GeneratedKeyHolder();
      Reviewer finalR = r;

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO reviewer (display_name, email, expertise_tags, active, current_load) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, finalR.getDisplayName());
        ps.setString(2, finalR.getEmail());
        ps.setString(3, finalR.getExpertiseTags());
        ps.setBoolean(4, finalR.isActive());
        ps.setInt(5, finalR.getCurrentLoad());

        return ps;
      }, keyHolder);

      Map<String, Object> keys = keyHolder.getKeys();
      r.setId((Long) keys.get("ID"));

      // store assignment
      ReviewAssignment ar = new ReviewAssignment(submission.getId(), r.getId(), "ASSIGNED");
      jdbcTemplate.update("INSERT INTO review_assignment(submission_id, reviewer_id, assignment_status) VALUES (?, ?, ?)",
              ar.getSubmissionId(), ar.getReviewerId(), ar.getAssignmentStatus());
      assigned.add(r);
    }
    return assigned;
  }

  private EvaluationOutcome startEvaluation(List<Reviewer> assignedReviewers, Submission submission)
  {
    return evaluationManager.evaluate(assignedReviewers, submission);
  }
}


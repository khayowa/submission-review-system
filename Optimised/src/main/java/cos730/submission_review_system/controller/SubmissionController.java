package cos730.submission_review_system.controller;

import cos730.submission_review_system.command.EvaluationManager;
import cos730.submission_review_system.command.ReviewerManager;
import cos730.submission_review_system.domain.EEvaluationOutcome;
import cos730.submission_review_system.domain.EvaluationOutcome;
import cos730.submission_review_system.domain.ReviewAssignment;
import cos730.submission_review_system.domain.Reviewer;
import cos730.submission_review_system.domain.Submission;
import cos730.submission_review_system.repository.ReviewAssignmentRepository;
import cos730.submission_review_system.repository.ReviewerRepository;
import cos730.submission_review_system.repository.SubmissionRepository;
import cos730.submission_review_system.service.NotificationService;
import cos730.submission_review_system.service.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SubmissionController
{
  private final Validator validator;
  private final NotificationService  notificationService;
  private final SubmissionRepository submissionRepository;
  private final ReviewerManager reviewerManager;
  private final EvaluationManager evaluationManager;

  public SubmissionController(Validator validator,
                              NotificationService  notificationService,
                              SubmissionRepository submissionRepository,
                              ReviewerManager reviewerManager,
                              EvaluationManager evaluationManager)
  {
    this.validator = validator;
    this.notificationService = notificationService;
    this.submissionRepository = submissionRepository;
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
      model.addAttribute("errorMessage", "No reviewers filteredReviewers at the moment.");
      return "submission-ui";
    }

    List<Reviewer> assigned = reviewerManager.assignReviewers(submission, filteredReviewers);
    EEvaluationOutcome outcome = evaluationManager.startEvaluation(assigned, submission);
    String notification = notificationService.sendNotification(outcome);

    model.addAttribute("successMessage", "Submission saved (ID: " + submission.getId() + "). Assigned reviewers: " + assigned.size());
    model.addAttribute("notification", notification);
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

    submissionRepository.save(submission);
    return submission;
  }
}


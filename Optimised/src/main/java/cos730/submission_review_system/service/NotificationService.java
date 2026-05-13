package cos730.submission_review_system.service;

import cos730.submission_review_system.domain.EEvaluationOutcome;
import org.springframework.stereotype.Service;

@Service
public class NotificationService
{

  public String sendNotification(EEvaluationOutcome outcome)
  {
    if (outcome == EEvaluationOutcome.ACCEPT)
    {
      return notifyAcceptance();
    }
    else if (outcome == EEvaluationOutcome.REJECT)
    {
      return notifyRejection();
    }
    else
    {
      return notifyRevision();
    }
  }

  /* ========= Notifications ========= */

  public String notifyAcceptance() {
    return "[NOTIFICATION] Research output ACCEPTED";
  }

  public String notifyRejection() {
    return "[NOTIFICATION] Research output REJECTED";
  }

  public String notifyRevision() {
    return "[NOTIFICATION] Research output REQUIRES REVISION";
  }
}

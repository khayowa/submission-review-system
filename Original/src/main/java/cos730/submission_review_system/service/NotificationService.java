package cos730.submission_review_system.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService
{
  public void notifyAcceptance() {
    System.out.println("[NOTIFY] Research output ACCEPTED");
  }

  public void notifyRejection() {
    System.out.println("[NOTIFY] Research output REJECTED");
  }

  public void notifyRevision() {
    System.out.println("[NOTIFY] Research output REQUIRES REVISION");
  }
}

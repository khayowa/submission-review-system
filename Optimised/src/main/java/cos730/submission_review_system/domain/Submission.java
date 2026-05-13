package cos730.submission_review_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
public class Submission
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Column(name = "original_filename")
  private String originalFilename;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "size_bytes")
  private long sizeBytes;

  @Column(name = "checksum_sha256")
  private String checksumSha256;

  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  protected Submission() { }

  public Submission(String title,
                          String originalFilename,
                          String contentType,
                          long sizeBytes,
                          String checksumSha256,
                          String status) {
    this.title = title;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.checksumSha256 = checksumSha256;
    this.status = status;
    this.createdAt = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public String getTitle() { return title; }
  public String getOriginalFilename() { return originalFilename; }
  public String getContentType() { return contentType; }
  public long getSizeBytes() { return sizeBytes; }
  public String getChecksumSha256() { return checksumSha256; }
  public String getStatus() { return status; }
  public LocalDateTime getCreatedAt() { return createdAt; }

  public void setStatus(String status) { this.status = status; }
}

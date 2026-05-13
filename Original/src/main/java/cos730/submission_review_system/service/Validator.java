package cos730.submission_review_system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Set;

@Service
public class Validator {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx", "zip", "txt");
  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
          "application/pdf",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // docx
          "application/zip",
          "text/plain"
  );

  private static final long MAX_BYTES = 20L * 1024L * 1024L; // Example: 20 MB max

  public record ValidationResult(
          String fileName,
          String contentType,
          long sizeBytes,
          String sha256
  ) {}

  public static class ValidationException extends RuntimeException
  {
    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
  }

  public ValidationResult validateFormat(MultipartFile file)
  {
    if (file == null)
    {
      throw new ValidationException("No file was provided.");
    }

    if (file.isEmpty() || file.getSize() <= 0)
    {
      throw new ValidationException("Uploaded file is empty.");
    }

    if (file.getSize() > MAX_BYTES)
    {
      throw new ValidationException("File exceeds maximum allowed size (" + MAX_BYTES + " bytes).");
    }

    String originalName = file.getOriginalFilename();

    if (originalName == null || originalName.trim().isEmpty())
    {
      throw new ValidationException("File name is missing.");
    }

    String safeName = Paths.get(originalName).getFileName().toString();

    if (!safeName.equals(originalName) || safeName.contains("..") || safeName.contains("/") || safeName.contains("\\"))
    {
      throw new ValidationException("Invalid file name.");
    }

    String extension = getExtension(safeName);
    if (!ALLOWED_EXTENSIONS.contains(extension))
    {
      throw new ValidationException("Unsupported file extension: " + extension);
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType))
    {
      throw new ValidationException("Unsupported content type: " + contentType);
    }

    // - PDF should start with "%PDF"
    // - ZIP starts with "PK" (DOCX is also ZIP internally)
    // - TXT: we don’t enforce signature; we only check it’s readable-ish.
    validateMagicBytes(file, extension);

    // Compute checksum for deduplication / traceability
    String sha256 = sha256Hex(file);

    return new ValidationResult(safeName, contentType, file.getSize(), sha256);
  }

  private void validateMagicBytes(MultipartFile file, String extension)
  {
    try (InputStream in = file.getInputStream())
    {
      byte[] first8 = in.readNBytes(8);

      if ("pdf".equals(extension))
      {
        if (!startsWith(first8, new byte[]{'%', 'P', 'D', 'F'}))
        {
          throw new ValidationException("File does not appear to be a valid PDF (missing %PDF header).");
        }
      }

      if ("zip".equals(extension) || "docx".equals(extension))
      {
        // ZIP signature is 'P' 'K' (0x50 0x4B)
        if (!startsWith(first8, new byte[]{'P', 'K'}))
        {
          throw new ValidationException("File does not appear to be a valid ZIP/DOCX (missing PK header).");
        }
      }

      if ("txt".equals(extension))
      {
        // If it contains many zero bytes, it's probably not plain text.
        int zeros = 0;
        for (byte b : first8) if (b == 0) zeros++;

        if (zeros >= 2)
        {
          throw new ValidationException("File does not appear to be plain text.");
        }
      }

    }
    catch (IOException e)
    {
      throw new ValidationException("Unable to read uploaded file for validation.", e);
    }
  }

  private String sha256Hex(MultipartFile file)
  {
    try
    {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      try (InputStream in = file.getInputStream())
      {
        byte[] buf = new byte[8192];
        int read;
        while ((read = in.read(buf)) != -1)
        {
          digest.update(buf, 0, read);
        }
      }
      byte[] hash = digest.digest();
      StringBuilder sb = new StringBuilder(hash.length * 2);
      for (byte b : hash) sb.append(String.format("%02x", b));
      return sb.toString();
    }
    catch (NoSuchAlgorithmException | IOException e)
    {
      throw new ValidationException("Unable to compute checksum.", e);
    }
  }

  private String getExtension(String filename)
  {
    int idx = filename.lastIndexOf('.');
    if (idx < 0 || idx == filename.length() - 1) return "";
    return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
  }

  private boolean startsWith(byte[] actual, byte[] prefix)
  {
    if (actual == null || actual.length < prefix.length) return false;

    for (int i = 0; i < prefix.length; i++)
    {
      if (actual[i] != prefix[i]) return false;
    }
    return true;
  }

}



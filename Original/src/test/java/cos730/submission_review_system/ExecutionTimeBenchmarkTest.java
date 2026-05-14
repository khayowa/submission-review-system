package cos730.submission_review_system;

import cos730.submission_review_system.controller.SubmissionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class ExecutionTimeBenchmarkTest
{
  @Autowired
  private SubmissionController submissionController;

  private static final int WARMUP_RUNS = 10;
  private static final int MEASURED_RUNS = 100;

  @Test
  void benchmark_full_submission_flow()
  {
    for (int i = 0; i < WARMUP_RUNS; i++)
    {
      runScenario();
    }

    List<Long> times = new ArrayList<>();

    for (int i = 0; i < MEASURED_RUNS; i++)
    {
      long start = System.nanoTime();

      runScenario();

      long end = System.nanoTime();
      times.add(end - start);
    }

    Collections.sort(times);

    long median = times.get(times.size() / 2);
    long p95 = times.get((int) (times.size() * 0.95));
    long min = times.get(0);
    long max = times.get(times.size() - 1);
    long avg = (long) times.stream().mapToLong(Long::longValue).average().orElse(0);

    System.out.println("===== EXECUTION TIME RESULTS =====");
    System.out.println("Warmup runs: " + WARMUP_RUNS);
    System.out.println("Measured runs: " + MEASURED_RUNS);

    System.out.println("Median (ms): " + median / 1_000_000.0);
    System.out.println("P95 (ms): " + p95 / 1_000_000.0);
    System.out.println("Average (ms): " + avg / 1_000_000.0);
    System.out.println("Min (ms): " + min / 1_000_000.0);
    System.out.println("Max (ms): " + max / 1_000_000.0);
  }

  private void runScenario()
  {
    try
    {
      String uniqueName = "test-" + System.nanoTime() + ".pdf";
      MockMultipartFile file = new MockMultipartFile(
              "file", uniqueName, "application/pdf",
              ("content-" + uniqueName).getBytes()
      );
      Model model = new ExtendedModelMap();
      submissionController.submitArtefact(file, model);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

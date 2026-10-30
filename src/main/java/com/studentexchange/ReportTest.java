
package com.studentexchange;

import com.studentexchange.repository.ReportRepository;

public class ReportTest {

    public static void main(String[] args) {

        ReportRepository repository = new ReportRepository();

        // User ID 1 is the active admin in your current test database.
        // For this test, use a separate active student account as reporter.
        int reporterId = 1;
        int postId = 1;

        boolean reported = repository.reportPost(
                reporterId,
                postId,
                "This post needs admin review."
        );

        System.out.println(
                reported
                        ? "Post reported successfully!"
                        : "Could not report post."
        );

        System.out.println("\n=== Pending Reports ===");
        repository.displayPendingReports(1);
    }
}


package com.studentexchange;

import com.studentexchange.repository.ReportRepository;

public class ModerationTest {

    public static void main(String[] args) {

        ReportRepository repository = new ReportRepository();

        int reportId = 2;
        int adminId = 1;

        boolean reviewed = repository.reviewReport(
                reportId,
                adminId,
                "DISMISSED",
                false
        );

        if (reviewed) {
            System.out.println("Report reviewed successfully!");
            System.out.println("Decision: DISMISSED");
            System.out.println("Content was not hidden.");
        } else {
            System.out.println(
                    "Review failed. Check admin permissions and report status."
            );
        }

        System.out.println("\nRemaining pending reports:");
        repository.displayPendingReports(adminId);
    }
}

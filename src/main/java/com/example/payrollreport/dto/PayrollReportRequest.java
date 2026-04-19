package com.example.payrollreport.dto;

import java.time.LocalDate;
import java.util.List;

public record PayrollReportRequest(
        String companyName,
        String payrollReference,
        LocalDate generationDate,
        List<PayrollEmployeeLine> employees,
        String responsibleName
) {
}

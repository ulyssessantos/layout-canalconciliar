package com.example.payrollreport.dto;

import java.math.BigDecimal;

public record PayrollEmployeeLine(
        String employeeName,
        String role,
        BigDecimal workedHours,
        BigDecimal grossSalary,
        BigDecimal deductions,
        BigDecimal netSalary
) {
}

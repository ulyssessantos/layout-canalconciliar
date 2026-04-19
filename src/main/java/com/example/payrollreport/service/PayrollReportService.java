package com.example.payrollreport.service;

import com.example.payrollreport.dto.PayrollReportRequest;
import org.springframework.stereotype.Service;
import pro.verron.officestamper.api.OfficeStampers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Service
public class PayrollReportService {

    public byte[] generate(InputStream templateInputStream, PayrollReportRequest request) {
        var stamper = OfficeStampers.docxStamper();

        try (templateInputStream; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            stamper.stamp(templateInputStream, request, output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gerar relatório de folha em DOCX.", e);
        }
    }
}

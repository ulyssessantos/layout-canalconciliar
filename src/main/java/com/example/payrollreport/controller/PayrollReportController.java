package com.example.payrollreport.controller;

import com.example.payrollreport.dto.PayrollReportRequest;
import com.example.payrollreport.service.PayrollReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/api/payroll")
public class PayrollReportController {

    private final PayrollReportService payrollReportService;
    private final ObjectMapper objectMapper;

    public PayrollReportController(PayrollReportService payrollReportService, ObjectMapper objectMapper) {
        this.payrollReportService = payrollReportService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> generate(
            @RequestPart("template") MultipartFile template,
            @RequestPart("payload") String payloadJson
    ) {
        try {
            PayrollReportRequest request = objectMapper.readValue(payloadJson, PayrollReportRequest.class);
            byte[] report = payrollReportService.generate(template.getInputStream(), request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("folha-pagamento.docx")
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(report);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler template/payload para geração da folha.", e);
        }
    }
}

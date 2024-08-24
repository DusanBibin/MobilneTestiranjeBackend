package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.MonthlyReportDTO;
import com.example.mobilnetestiranjebackend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReservationService reservationService;

    @GetMapping("/last-12-months-report")
    public ResponseEntity<Map<String, MonthlyReportDTO>> getLast12MonthsReport(
            @RequestParam Long accommodationId) {
        Map<String, MonthlyReportDTO> reportData = reservationService.getLast12MonthsReport(accommodationId);
        return ResponseEntity.ok(reportData);
    }
}

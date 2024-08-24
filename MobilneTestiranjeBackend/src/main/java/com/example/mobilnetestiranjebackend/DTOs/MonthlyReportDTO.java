package com.example.mobilnetestiranjebackend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyReportDTO {
    private String monthYear;
    private double totalProfit;
    private long reservationCount;
}

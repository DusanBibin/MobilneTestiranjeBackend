package com.example.projekatmobilne.model.responseDTO;

public class MonthlyReportDTO {
    private String monthYear;
    private double totalProfit;

    public MonthlyReportDTO() {
    }

    public MonthlyReportDTO(String monthYear, double totalProfit, long reservationCount) {
        this.monthYear = monthYear;
        this.totalProfit = totalProfit;
        this.reservationCount = reservationCount;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public long getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(long reservationCount) {
        this.reservationCount = reservationCount;
    }

    private long reservationCount;
}

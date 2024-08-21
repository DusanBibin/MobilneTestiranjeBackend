package com.example.mobilnetestiranjebackend.DTOs;

public class ComplaintDTO {
    private Long id;
    private String reporedUser;
    private String reporterUser;
    private String reportedUserRole;
    private String reporterUserRole;
    private String reason;

    public ComplaintDTO() {
    }

    public ComplaintDTO(Long id, String reporedUser, String reporterUser, String reportedUserRole, String reporterUserRole, String reason) {
        this.id = id;
        this.reporedUser = reporedUser;
        this.reporterUser = reporterUser;
        this.reportedUserRole = reportedUserRole;
        this.reporterUserRole = reporterUserRole;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReporedUser() {
        return reporedUser;
    }

    public void setReporedUser(String reporedUser) {
        this.reporedUser = reporedUser;
    }

    public String getReporterUser() {
        return reporterUser;
    }

    public void setReporterUser(String reporterUser) {
        this.reporterUser = reporterUser;
    }

    public String getReportedUserRole() {
        return reportedUserRole;
    }

    public void setReportedUserRole(String reportedUserRole) {
        this.reportedUserRole = reportedUserRole;
    }

    public String getReporterUserRole() {
        return reporterUserRole;
    }

    public void setReporterUserRole(String reporterUserRole) {
        this.reporterUserRole = reporterUserRole;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

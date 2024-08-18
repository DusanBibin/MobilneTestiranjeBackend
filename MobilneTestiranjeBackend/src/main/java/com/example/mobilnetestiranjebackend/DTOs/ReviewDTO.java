package com.example.mobilnetestiranjebackend.DTOs;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  ReviewDTO {
    private Long reviewId;
    @NotBlank(message = "Comment must be present")
    private String comment;


    @NotNull(message = "Rating must be present")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Long rating;

    private String complaintReason;
    private Long complaintId;
}

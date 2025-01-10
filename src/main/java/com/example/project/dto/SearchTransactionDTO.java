package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchTransactionDTO {
    private String month;  // Optional
    
    @NotBlank(message = "Year is required")
    private String year;   // Required
}

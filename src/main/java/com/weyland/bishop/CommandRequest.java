package com.weyland.bishop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommandRequest(
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,
    
    CommandPriority priority,
    
    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    String author,
    
    @NotBlank(message = "Time is required")
    String time
) {}
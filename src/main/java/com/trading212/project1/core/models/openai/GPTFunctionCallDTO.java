package com.trading212.project1.core.models.openai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GPTFunctionCallDTO {
    private String functionName;
    private String arguments;
}

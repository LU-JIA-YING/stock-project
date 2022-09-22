package com.example.springservice.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealRequest {
    private String branchNo;
    private String custSeq;
    private String stock;

    private Double profitRateLowerLimit;
    private Double profitRateUpperLimit;

}

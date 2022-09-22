package com.example.springservice.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealSunResult {

    private String stock;
    private String stockName;
    private Double nowPrice;
    private Double sumRemainQty;
    private Double sumFee;
    private Double sumCost;
    private Double sumMarketValue;
    private Double sumUnrealProfit;

    private String sunProfitRate;

    private List<UnrealDetailResult> detaiList;
}

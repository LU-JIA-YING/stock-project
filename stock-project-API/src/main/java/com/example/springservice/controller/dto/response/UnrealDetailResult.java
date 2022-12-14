package com.example.springservice.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealDetailResult {

    private String tradeDate;
    private String docSeq;
    private String stock;
    private String stockName;
    private Double buyPrice;
    private Double nowPrice;
    private Double qty;
    private Double remainQty;
    private Double fee;
    private Double cost;
    private Double marketValue;
    private Double unrealProfit;

    private String profitRate;
}

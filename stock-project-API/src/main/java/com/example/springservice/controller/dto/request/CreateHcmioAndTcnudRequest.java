package com.example.springservice.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHcmioAndTcnudRequest {

    private String tradeDate;
    private String branchNo;
    private String custSeq;
    private String docSeq;
    private String stock;
    private Double price;
    private Double qty;

}

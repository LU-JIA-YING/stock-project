package com.example.springservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor // 生成一個包含所有參數的 constructor
@NoArgsConstructor // 生成一個沒有參數的constructor
@Entity
@Table(name = "hcmio")
@IdClass(HcmioAndTcnudCompositePK.class)
public class Hcmio {

    @Id
    @Column(name = "TradeDate")
    private String tradeDate;

    @Id
    @Column(name = "BranchNo")
    private String branchNo;

    @Id
    @Column(name = "CustSeq")
    private String custSeq;

    @Id
    @Column(name = "DocSeq")
    private String docSeq;

    @Column(name = "Stock")
    private String stock;

    @Column(name = "BsType")
    private String bsType;

    @Column(name = "Price")
    private double price;

    @Column(name = "Qty")
    private double qty;

    @Column(name = "Amt")
    private double amt;

    @Column(name = "Fee")
    private double fee;

    @Column(name = "Tax")
    private double tax;

    @Column(name = "StinTax")
    private double stinTax;

    @Column(name = "NetAmt")
    private double netAmt;

    @Column(name = "ModDate")
    private String modDate;

    @Column(name = "ModTime")
    private String modTime;

    @Column(name = "ModUser")
    private String modUser;

}

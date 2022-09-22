package com.example.springservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mstmb")
public class Mstmb {
    @Id
    @Column(name = "Stock")
    private String stock;

    @Column(name = "StockName")
    private String stockName;

    @Column(name = "MarketType")
    private String marketType;

    @Column(name = "CurPrice")
    private double curPrice;

    @Column(name = "RefPrice")
    private double refPrice;

    @Column(name = "Currency")
    private String currency;

    @Column(name = "ModDate")
    private String modDate;

    @Column(name = "ModTime")
    private String modTime;

    @Column(name = "ModUser")
    private String modUser;

}

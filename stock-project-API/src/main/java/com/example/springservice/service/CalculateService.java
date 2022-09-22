package com.example.springservice.service;

import org.springframework.stereotype.Service;

@Service
public class CalculateService {

    public static double feePercent = 0.001425;
    public static double taxPercent = 0.003;

    //  計算價金 amt
    public double getAmt(double qty, double price) {
        double amt = qty * price;
        return amt;
    }

    //  計算手續費 fee
    public double getFee(double amt) {
        double fee = Math.round(amt * feePercent);  //Math.round()四捨五入
        return fee;
    }

    //  計算交易稅 tax
    public double getTax(double amt, String bsType) {
        if ("B".equals(bsType)) {
            return 0;
        } else {
            double tax = Math.round(amt * taxPercent);
            return tax;
        }
    }

    //  證所稅 StinTax
    public double getStinTax() {
        return 0;
    }

    // 計算淨收付金額 NetAmt
    public double getNetAmt(double amt, String bsType, double fee, double tax) {

        if ("B".equals(bsType)) {
            double netAmt = (amt + fee) * (-1);
            return netAmt;
        } else if ("S".equals(bsType)) {
            double netAmt = amt - fee - tax;
            return netAmt;
        }
        return 0;
    }

    // 計算市值 MarketValue
    public double getMarketValue(double remainQty, double curPrice) {
        // 股票市值 = (現價 * 股數) - 賣出手續費 - 交易稅
        double marketValue = Math.round((remainQty * curPrice) - (remainQty * curPrice * taxPercent )- (remainQty * curPrice * feePercent));
        return marketValue;
    }

    //  計算未實現損益 UnrealProfit
    public double getUnrealProfit(double marketValue, double cost) {

        double unrealProfit = marketValue - cost;
        return unrealProfit;
    }

    public double getProfitRate(double unrealProfit, double cost) {
        double profitRate = (unrealProfit / cost) * 100;
        return profitRate;
    }
}

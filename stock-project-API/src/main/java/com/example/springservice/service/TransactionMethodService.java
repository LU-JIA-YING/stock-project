package com.example.springservice.service;

import com.example.springservice.controller.dto.request.CreateHcmioAndTcnudRequest;
import com.example.springservice.controller.dto.request.UnrealRequest;
import com.example.springservice.controller.dto.response.UnrealDetailResult;
import com.example.springservice.model.HcmioRepository;
import com.example.springservice.model.MstmbRepository;
import com.example.springservice.model.TcnudRepository;
import com.example.springservice.model.entity.Symbol;
import com.example.springservice.model.entity.Symbols;
import com.example.springservice.model.entity.Tcnud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;

//建議這整個class跟UnrealService放一起
@Service
public class TransactionMethodService {

    @Autowired
    private HcmioRepository hcmioRepository;
    @Autowired
    private TcnudRepository tcnudRepository;
    @Autowired
    private MstmbRepository mstmbRepository;
    @Autowired
    private CalculateService calculateService;

    //  檢查創建傳入的資料是否都正確
    public String check(CreateHcmioAndTcnudRequest request) {

        if (isBlank(request.getTradeDate()) || request.getTradeDate().length() != 8) {
            return "交易日期輸入錯誤";
        }
        if (isBlank(request.getBranchNo()) || request.getBranchNo().length() != 4) {
            return "分行代碼輸入錯誤";
        }
        if (isBlank(request.getCustSeq()) || request.getCustSeq().length() != 2) {
            return "客戶帳號輸入錯誤";
        }
        if (isBlank(request.getDocSeq()) || request.getDocSeq().length() != 5) {
            return "委託書號輸入錯誤";
        }
        if (isBlank(request.getStock()) || request.getStock().length() != 4) {
            return "股票代碼輸入錯誤";
        }
        if (null == request.getPrice() || request.getPrice() <= 0) {
            return "請輸入有效購買價格";
        }
        if (null == request.getQty() || request.getQty() <= 0 || request.getQty() % 1 != 0) {  // qty不得為空或小於等於0或含有小數
            return "請輸入有效購買數量";
        }
        if (null != tcnudRepository.findByTradeDateAndBranchNoAndCustSeqAndDocSeq(request.getTradeDate(), request.getBranchNo(), request.getCustSeq(), request.getDocSeq())) {
            return "輸入資料已存在";
        }
        if (null == mstmbRepository.findByStock(request.getStock())) { // mstmbRepository.findByStock(request.getStock()) 輸出為 Mstmb為class
            return "無此檔股票資料";
        }
        return "Success";
    }

//===========================================================================================================

    //  明細列
    public List<UnrealDetailResult> getUnrealDetail(UnrealRequest request) {

        List<Tcnud> tcnudList;  //  宣吿存放餘額表的陣列
        List<String> stockList = new ArrayList<>(); //  創建存股票的陣列
        if (null == request.getStock() || isBlank(request.getStock())) {   // 輸入無股票代碼，抓該用戶所有的餘額表資料  //  null == request.getStock()代表Postman的body沒有"stock": 這行
            tcnudList = tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq());
            stockList = tcnudRepository.findDistinctStock(request.getBranchNo(), request.getCustSeq()); //  取該用戶有購買的所有股票(去重複值)
        } else {
            tcnudList = tcnudRepository.findByBranchNoAndCustSeqAndStock(request.getBranchNo(), request.getCustSeq(), request.getStock());
            stockList.add(request.getStock());  //  抓到目前股票代碼的資料明細檔
        }


        List<UnrealDetailResult> profitRateList = new ArrayList<>(); //  將符合獲利區間的UnrealDetail 放入 profitRateList

        for (String stock : stockList) {

//            String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
//            RestTemplate restTemplate = new RestTemplate();
//            Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);
//            Symbol symbol = symbols.getSymbolList().get(0);

            List<Symbol> symbols = api(stock);
            Symbol symbol = symbols.get(0);

            for (Tcnud tcnudGetStock : tcnudList) {
                if (tcnudGetStock.getStock().equals(stock)) {

                    List<UnrealDetailResult> detaiList = new ArrayList<>();

                    UnrealDetailResult unrealDetailResult = new UnrealDetailResult();

                    unrealDetailResult.setTradeDate(tcnudGetStock.getTradeDate());
                    unrealDetailResult.setDocSeq(tcnudGetStock.getDocSeq());
                    unrealDetailResult.setStock(tcnudGetStock.getStock());
                    unrealDetailResult.setStockName(symbol.getShortname());
                    unrealDetailResult.setBuyPrice(tcnudGetStock.getPrice());
                    unrealDetailResult.setNowPrice(symbol.getDealprice());
                    unrealDetailResult.setQty(tcnudGetStock.getQty());
                    unrealDetailResult.setRemainQty(tcnudGetStock.getRemainQty());
                    unrealDetailResult.setFee(tcnudGetStock.getFee());
                    unrealDetailResult.setCost(tcnudGetStock.getCost());
                    unrealDetailResult.setMarketValue(calculateService.getMarketValue(tcnudGetStock.getRemainQty(), symbol.getDealprice()));
                    unrealDetailResult.setUnrealProfit((calculateService.getUnrealProfit(unrealDetailResult.getMarketValue(), tcnudGetStock.getCost())));

                    unrealDetailResult.setProfitRate(String.format("%.2f", (calculateService.getProfitRate(unrealDetailResult.getUnrealProfit(), unrealDetailResult.getCost()))) + "%");

                    detaiList.add(unrealDetailResult);


                    for (UnrealDetailResult unrealDetail : detaiList) { //  檢查獲利率

                        double profitRate = (double) (Math.round((calculateService.getProfitRate(unrealDetailResult.getUnrealProfit(), unrealDetailResult.getCost())) * 100)) / 100;

                        if (null != request.getProfitRateLowerLimit() && null != request.getProfitRateUpperLimit()) {
                            if (profitRate >= request.getProfitRateLowerLimit() && profitRate <= request.getProfitRateUpperLimit()) {
                                profitRateList.add(unrealDetail);

                            } else
                                continue;

                        } else if (null != request.getProfitRateLowerLimit() && null == request.getProfitRateUpperLimit()) {
                            if (profitRate >= request.getProfitRateLowerLimit()) {
                                profitRateList.add(unrealDetail);

                            } else
                                continue;

                        } else if (null == request.getProfitRateLowerLimit() && null != request.getProfitRateUpperLimit()) {
                            if (profitRate <= request.getProfitRateUpperLimit()) {
                                profitRateList.add(unrealDetail);

                            } else
                                continue;

                        } else if (null == request.getProfitRateLowerLimit() && null == request.getProfitRateUpperLimit()) {
                            profitRateList.add(unrealDetail);

                        }
                    }

                }
            }
        }
        return profitRateList;
    }

//===========================================================================================================

    //查詢即時價API介接
    public List<Symbol> api(String stock) {
        String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
        RestTemplate restTemplate = new RestTemplate();
        Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);

        return symbols.getSymbolList();
    }

//===========================================================================================================

//StringUtils.isEmpty(String str)判斷某字符串是否為空，為空的標準是str==null 或str.length()==0
//System.out.println(StringUtils.isEmpty( null ));     // true
//System.out.println(StringUtils.isEmpty(""));         // true
//System.out.println(StringUtils.isEmpty(" "));        // false
//System.out.println(StringUtils.isEmpty("dd"));       // false
//
//
//StringUtils.isBlank(String str)判斷某字符串是否為空 或長度為0 或由空白符(whitespace) 構成
//System.out.println(StringUtils.isBlank( null ));     // true
//System.out.println(StringUtils.isBlank(""));         // true
//System.out.println(StringUtils.isBlank(" "));        // true
//System.out.println(StringUtils.isBlank("dd"));       // false

//===========================================================================================================

//    private String makeLastDocSeq(String tradeDate) { //自動產生委託書號
//
//        String lastDocSeq = hcmioRepository.getLastDocSeq(tradeDate);
//        if (null == lastDocSeq) {
//            return "AA001";
//        }
//        int firstEngToAscii = lastDocSeq.charAt(0);
//        int secondEngToAscii = lastDocSeq.charAt(1);
//        String num = lastDocSeq.substring(2, 5);
//        int numToInt = Integer.parseInt(num) + 1;
//
//
//        if (numToInt > 999) {
//            numToInt = 1;
//            secondEngToAscii++;
//            if (secondEngToAscii > 90) {
//                secondEngToAscii = 65;
//                firstEngToAscii++;
//            }
//        }
//        String numToString = String.format("%03d", numToInt);
//        String firstEngToString = Character.toString((char) firstEngToAscii);
//        String secondEngToString = Character.toString((char) secondEngToAscii);
//
//        return firstEngToString + secondEngToString + numToString;
//    }
}

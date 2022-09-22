package com.example.springservice.service;

import com.example.springservice.controller.dto.request.CreateHcmioAndTcnudRequest;
import com.example.springservice.controller.dto.request.UnrealRequest;
import com.example.springservice.controller.dto.response.UnrealDetailResponse;
import com.example.springservice.controller.dto.response.UnrealDetailResult;
import com.example.springservice.controller.dto.response.UnrealSumResponse;
import com.example.springservice.controller.dto.response.UnrealSunResult;
import com.example.springservice.model.HcmioRepository;
import com.example.springservice.model.MstmbRepository;
import com.example.springservice.model.TcnudRepository;
import com.example.springservice.model.entity.Hcmio;
import com.example.springservice.model.entity.Symbol;
import com.example.springservice.model.entity.Tcnud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class UnrealService {

    @Autowired
    private HcmioRepository hcmioRepository;
    @Autowired
    private TcnudRepository tcnudRepository;
    @Autowired
    private MstmbRepository mstmbRepository;
    @Autowired
    private CalculateService calculateService;
    @Autowired
    private TransactionMethodService transactionMethodService;


    // 查詢未實現損益明細
    public UnrealDetailResponse findUnrealDetail(UnrealRequest request) {

        UnrealDetailResponse unrealDetailResponse = new UnrealDetailResponse();

        //  檢查輸入資料是否有誤
        if (isBlank(request.getBranchNo()) || request.getBranchNo().length() != 4) {
            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage("分行代碼輸入錯誤");

            //  跟上面一樣 下面可改成if
//            return new UnrealDetailResponse(null,"002","分行代碼輸入錯誤");

        } else if (isBlank(request.getCustSeq()) || request.getCustSeq().length() != 2) {
            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage("客戶帳號輸入錯誤");

        } else if (null != request.getStock() && request.getStock().length() != 4) {
            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage("股票代號輸入錯誤");

        } else if (null != request.getStock() && null == mstmbRepository.findByStock(request.getStock())) {
            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage("無此檔股票資料");

        } else if (null != request.getProfitRateLowerLimit() && null != request.getProfitRateUpperLimit() && request.getProfitRateLowerLimit() > request.getProfitRateUpperLimit()) {

            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage("獲利率區間限制範圍輸入錯誤");

        } else if (isBlank(tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).toString())) {
            unrealDetailResponse.setResponseCode("001");
            unrealDetailResponse.setMessage("查無符合資料");

        } else if (null != tcnudRepository.findByBranchNoAndCustSeqAndStock(request.getBranchNo(), request.getCustSeq(), request.getStock()).toString()) {

            List<UnrealDetailResult> resultList = transactionMethodService.getUnrealDetail(request);
            if (0 == resultList.size()) { //  避免篩選完出現沒有資料的情況
                unrealDetailResponse.setResponseCode("001");
                unrealDetailResponse.setMessage("查無符合資料");

            } else {
                unrealDetailResponse.setResultList(resultList);
                unrealDetailResponse.setResponseCode("000");
                unrealDetailResponse.setMessage("");

            }
        } else {
            unrealDetailResponse.setResponseCode("005");
            unrealDetailResponse.setMessage("伺服器忙碌中，請稍後嘗試");

        }
        return unrealDetailResponse;
    }

//===========================================================================================================

    //  查詢未實現損益彙總
    public UnrealSumResponse findUnrealSum(UnrealRequest request) {

        UnrealSumResponse unrealSumResponse = new UnrealSumResponse();

        if (isBlank(request.getBranchNo()) || request.getBranchNo().length() != 4) {
            unrealSumResponse.setResponseCode("002");
            unrealSumResponse.setMessage("分行代碼輸入錯誤");

        } else if (isBlank(request.getCustSeq()) || request.getCustSeq().length() != 2) {
            unrealSumResponse.setResponseCode("002");
            unrealSumResponse.setMessage("客戶帳號輸入錯誤");

        } else if (null != request.getStock() && request.getStock().length() != 4) {
            unrealSumResponse.setResponseCode("002");
            unrealSumResponse.setMessage("股票代號輸入錯誤");

        } else if (null != request.getStock() && null == mstmbRepository.findByStock(request.getStock())) {
            unrealSumResponse.setResponseCode("002");
            unrealSumResponse.setMessage("無此檔股票資料");

        } else if (null != request.getProfitRateLowerLimit() && null != request.getProfitRateUpperLimit() && request.getProfitRateLowerLimit() > request.getProfitRateUpperLimit()) {
            unrealSumResponse.setResponseCode("002");
            unrealSumResponse.setMessage("獲利率區間限制範圍輸入錯誤");
        } else if (tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).isEmpty()) {
            unrealSumResponse.setResponseCode("001");
            unrealSumResponse.setMessage("查無符合資料");

        } else if (null != tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).toString()) {

            List<UnrealSunResult> resultList = new ArrayList<>();    //  創建存未實現損益總和的陣列

            List<String> stockList = new ArrayList<>(); //  創建存股票的陣列

            if (null == request.getStock() || request.getStock().isBlank()) {
                stockList = tcnudRepository.findDistinctStock(request.getBranchNo(), request.getCustSeq()); //  取該用戶有購買的所有股票(去重複值)
            } else {
                stockList.add(request.getStock());  //  抓到目前股票代碼的資料明細檔
            }

            for (String stock : stockList) {    //  用使用者所持股票去跑迴圈

//                String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
//                RestTemplate restTemplate = new RestTemplate();
//                Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);
//                Symbol symbol = symbols.getSymbolList().get(0);

                List<Symbol> symbols = transactionMethodService.api(stock);
                Symbol symbol = symbols.get(0);

                //  創建存放 新未實現損益明細總和的物件，當換下一個股票時，在new一新的，不同股票才可分開
                UnrealSunResult unrealSunResult = new UnrealSunResult();

                //  將同檔股票的未實現損益明細放入同一陣列
                List<UnrealDetailResult> detailList = transactionMethodService.getUnrealDetail(request);

                double sumRemainQty = 0;
                double sumFee = 0;
                double sumCost = 0;
                double sumMarketValue = 0;

                for (UnrealDetailResult unrealDetailResult : detailList) {  //  跑同一個股票的每個未實現損益明細
                    sumRemainQty += unrealDetailResult.getRemainQty();
                    sumFee += unrealDetailResult.getFee();
                    sumCost += unrealDetailResult.getCost();
                    sumMarketValue += unrealDetailResult.getMarketValue();
                }

                unrealSunResult.setStock(stock);
                unrealSunResult.setStockName(symbol.getShortname());
                unrealSunResult.setNowPrice(symbol.getDealprice());
                unrealSunResult.setSumRemainQty(sumRemainQty);
                unrealSunResult.setSumFee(sumFee);
                unrealSunResult.setSumCost(sumCost);
                unrealSunResult.setSumMarketValue(sumMarketValue);
                unrealSunResult.setSumUnrealProfit(sumMarketValue - sumCost);

                unrealSunResult.setSunProfitRate(String.format("%.2f", (calculateService.getProfitRate(unrealSunResult.getSumUnrealProfit(), unrealSunResult.getSumCost()))) + "%");
                unrealSunResult.setDetaiList(detailList);   //  將未實現損益明細陣列也放入進物件中

                // 如果篩選明細查詢後有資料，才存放到彙總明細(SunResult)，以免造成彙總明細有資料 但明細為空陣列
                //                {
                //                        "stock": "6214",
                //                        "stockName": "精誠資訊股份有限公司",
                //                        "nowPrice": 72.4,
                //                        "sumRemainQty": 0.0,
                //                        "sumFee": 0.0,
                //                        "sumCost": 0.0,
                //                        "sumMarketValue": 0.0,
                //                        "sumUnrealProfit": 0.0,
                //                        "sunProfitRate": "NaN%",
                //                        "detaiList": []
                //                }
                if (unrealSunResult.getSumCost() != 0) {
                    resultList.add(unrealSunResult); // 將unrealSunResultList放入最終要回傳的陣列中
                }
            }
            if (resultList.size() == 0) {
                unrealSumResponse.setResponseCode("001");
                unrealSumResponse.setMessage("查無符合資料");
            } else {
                unrealSumResponse.setResultList(resultList);
                unrealSumResponse.setResponseCode("000");
                unrealSumResponse.setMessage("Success");
            }

        } else {
            unrealSumResponse.setResponseCode("005");
            unrealSumResponse.setMessage("伺服器忙碌中，請稍後嘗試");

        }
        return unrealSumResponse;
    }

//===========================================================================================================

    // 新增餘額
    // 明細檔(HCMIO)和餘額檔(TCNUD)必須同時寫入資料，當其中一筆資料沒有被寫入時，另一筆資料就必須rollback
    @Transactional  //  Rollback
    public UnrealDetailResponse CraeteHcmioAndTcnud(CreateHcmioAndTcnudRequest request) {

        UnrealDetailResponse unrealDetailResponse = new UnrealDetailResponse();

        if (!"Success".equals(transactionMethodService.check(request))) {
            unrealDetailResponse.setResponseCode("002");
            unrealDetailResponse.setMessage(transactionMethodService.check(request));
            return unrealDetailResponse;
        }

        // 新增一個空的 tcnud與hcmio 的 entity = 新增一筆空的資料
        Hcmio hcmio = new Hcmio();
        Tcnud tcnud = new Tcnud();

        // 塞好資料：hcmio 裡的資料是從 request 來的
        hcmio.setTradeDate(request.getTradeDate());
        hcmio.setBranchNo(request.getBranchNo());
        hcmio.setDocSeq(request.getDocSeq());
        hcmio.setCustSeq(request.getCustSeq());
        hcmio.setStock(request.getStock());
        hcmio.setBsType("B");
        hcmio.setPrice(request.getPrice());
        hcmio.setQty(request.getQty());
        hcmio.setAmt(calculateService.getAmt(request.getPrice(), request.getQty()));
        hcmio.setFee(calculateService.getFee(hcmio.getAmt()));
        hcmio.setTax(calculateService.getTax(hcmio.getAmt(), hcmio.getBsType()));
        hcmio.setStinTax(calculateService.getStinTax());
        hcmio.setNetAmt(calculateService.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()));
        hcmio.setModDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        hcmio.setModTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
        hcmio.setModUser("Joyce");

        //insert
        // 塞好資料：tcnud 裡的資料是從 request 來的
        tcnud.setTradeDate(request.getTradeDate());
        tcnud.setBranchNo(request.getBranchNo());
        tcnud.setDocSeq(request.getDocSeq());
        tcnud.setCustSeq(request.getCustSeq());
        tcnud.setStock(request.getStock());
        tcnud.setPrice(request.getPrice());
        tcnud.setQty(request.getQty());
        tcnud.setRemainQty(request.getQty());
        tcnud.setFee(hcmio.getFee());
        tcnud.setCost(Math.abs(hcmio.getNetAmt()));  //Math.abs 絕對值
        tcnud.setModDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        tcnud.setModTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
        tcnud.setModUser("Joyce");


        // 儲存進 DB
        tcnudRepository.save(tcnud);
//        System.out.println(1 / 0);  //  測試rollback(@Transactional)-->如果有此行，兩個表都不會新增;沒加@Transactional，則會新增hcmio，但不會新增tcnud(responseBody都顯示 005伺服器忙碌中，請稍後嘗試)
        hcmioRepository.save(hcmio);

        if (null != tcnudRepository.findByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).toString()) {
            List<UnrealDetailResult> resultList = new ArrayList<>();
            UnrealDetailResult unrealDetailResult = new UnrealDetailResult();//  先創建一未實現損益明細的物件


//                String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
//                RestTemplate restTemplate = new RestTemplate();
//                Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);
//                Symbol symbol = symbols.getSymbolList().get(0);

            List<Symbol> symbolList = transactionMethodService.api(request.getStock());
            Symbol symbol = symbolList.get(0);


            unrealDetailResult.setTradeDate(tcnud.getTradeDate());
            unrealDetailResult.setDocSeq(tcnud.getDocSeq());
            unrealDetailResult.setStock(tcnud.getStock());
            unrealDetailResult.setStockName(symbol.getShortname()); //   明細檔抓股票名稱
            unrealDetailResult.setBuyPrice(tcnud.getPrice());
            unrealDetailResult.setNowPrice(symbol.getDealprice());    //  明細檔抓股票現值
            unrealDetailResult.setQty(tcnud.getQty());
            unrealDetailResult.setRemainQty(tcnud.getRemainQty());
            unrealDetailResult.setFee(tcnud.getRemainQty() * tcnud.getPrice() * 0.001425);
            unrealDetailResult.setCost(tcnud.getCost());
            unrealDetailResult.setMarketValue(calculateService.getMarketValue(unrealDetailResult.getRemainQty(), symbol.getDealprice()));
            unrealDetailResult.setUnrealProfit(calculateService.getUnrealProfit(unrealDetailResult.getMarketValue(), unrealDetailResult.getCost()));
            unrealDetailResult.setProfitRate(String.format("%.2f", (calculateService.getProfitRate(unrealDetailResult.getUnrealProfit(), unrealDetailResult.getCost()))) + "%");

            resultList.add(unrealDetailResult); //  物件放入陣列

            unrealDetailResponse.setResultList(resultList);
            unrealDetailResponse.setResponseCode("000");
            unrealDetailResponse.setMessage("");
        } else {
            unrealDetailResponse.setResponseCode("005");
            unrealDetailResponse.setMessage("伺服器忙碌中，請稍後嘗試");

        }
        return unrealDetailResponse;
    }

}

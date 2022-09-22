package com.example.springservice.controller;

import com.example.springservice.controller.dto.request.MstmbRequest;
import com.example.springservice.controller.dto.response.ApiResponse;
import com.example.springservice.controller.dto.response.MstmbResponse;
import com.example.springservice.model.entity.Symbol;
import com.example.springservice.model.entity.Symbols;
import com.example.springservice.service.MstmbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/mstmb")
public class MstmbController {

    @Autowired
    MstmbService mstmbService;


    //用DocSeq查詢資料
    @PostMapping("/stock")
    public MstmbResponse getMstmbByDocSeq(@RequestBody MstmbRequest request) {

        try {
            MstmbResponse response = mstmbService.postMstmbByStock(request);
            return response;
        } catch (Exception e) {
            MstmbResponse mstmbResponse = new MstmbResponse();
            mstmbResponse.setStatus("005 伺服器忙碌中，請稍後嘗試");

            return mstmbResponse;

        }

    }

//=======================================================================================================

    //更改Mstmb的現價
    @PostMapping("/updateCurPrice")
    public MstmbResponse updateUser(@RequestBody MstmbRequest request) {

        try {
            MstmbResponse response = mstmbService.updateMstmb(request);
            return response;
        } catch (Exception e) {
            MstmbResponse mstmbResponse = new MstmbResponse();
            mstmbResponse.setStatus("005 伺服器忙碌中，請稍後嘗試");

            return mstmbResponse;

        }
    }

//=======================================================================================================

    //查詢即時價API介接
    @PostMapping("/XmlByDocSeq")
    public ApiResponse getXmlByDocSeq(@RequestBody MstmbRequest request) {

        try {
            ApiResponse response = mstmbService.postMstmbByStock2(request);
            return response;
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setStatus("005 伺服器忙碌中，請稍後嘗試");

            return apiResponse;

        }
    }

//    //查詢即時價API介接
//    //http://localhost:8080/mstmb/XmlByDocSeq?stock=2357,1101
//    @PostMapping("/XmlByDocSeq")
//    public ApiResponse getXmlByDocSeq(@RequestParam String stock) {
//
//        try {
//            ApiResponse response = mstmbService.postMstmbByStock2(stock);
//            return response;
//        } catch (Exception e) {
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setStatus("005 伺服器忙碌中，請稍後嘗試");
//
//            return apiResponse;
//
//        }
//    }
//=======================================================================================================

    //查詢即時價API介接
    @PostMapping("/stockxml")
    public String getByDocSeq(@RequestParam String stock) {
        String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";

        RestTemplate restTemplate = new RestTemplate();
        Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);

        String response = symbols.getSymbolList().toString();

        return response;
    }

//    @PostMapping("/stockxml")
//    public List<Symbol> getByDocSeq2(@RequestParam String stock) {
//        String URL = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
//
//        RestTemplate restTemplate = new RestTemplate();
//        Symbols symbols = restTemplate.getForObject(URL + stock, Symbols.class);
//
//        List<Symbol> response = symbols.getSymbolList();
//
//        return response;
//    }
}

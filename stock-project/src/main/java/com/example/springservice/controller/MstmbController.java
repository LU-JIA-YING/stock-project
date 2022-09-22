package com.example.springservice.controller;

import com.example.springservice.controller.dto.request.MstmbRequest;
import com.example.springservice.controller.dto.response.MstmbResponse;
import com.example.springservice.service.MstmbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

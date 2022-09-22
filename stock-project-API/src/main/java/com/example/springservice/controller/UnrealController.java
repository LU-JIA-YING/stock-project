package com.example.springservice.controller;

import com.example.springservice.controller.dto.request.CreateHcmioAndTcnudRequest;
import com.example.springservice.controller.dto.request.UnrealRequest;
import com.example.springservice.controller.dto.response.UnrealDetailResponse;
import com.example.springservice.controller.dto.response.UnrealSumResponse;
import com.example.springservice.service.UnrealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/unreal")
public class UnrealController {

    @Autowired
    private UnrealService unrealService;

    //  查詢未實現損益明細
    @PostMapping("/detail")
    public UnrealDetailResponse findUnrealDetail(@RequestBody UnrealRequest request) {

        try {
            UnrealDetailResponse response = this.unrealService.findUnrealDetail(request);
            return response;
        } catch (Exception e) {
            UnrealDetailResponse unrealDetailResponse = new UnrealDetailResponse();
            unrealDetailResponse.setResponseCode("005");
            unrealDetailResponse.setMessage("伺服器忙碌中，請稍後嘗試");

            return unrealDetailResponse;

        }
    }

//=====================================================================================================

    //  查詢未實現損益彙總
    @PostMapping("/sum")
    public UnrealSumResponse findSumUnrealProfitByStock(@RequestBody UnrealRequest request) {

        try {
            UnrealSumResponse response = this.unrealService.findUnrealSum(request);
            return response;
        } catch (Exception e) {
            UnrealSumResponse unrealSumResponse = new UnrealSumResponse();
            unrealSumResponse.setResponseCode("005");
            unrealSumResponse.setMessage("伺服器忙碌中，請稍後嘗試");

            return unrealSumResponse;

        }
    }

//=====================================================================================================

    // 新增餘額
    @PostMapping("/add")
    public UnrealDetailResponse CraeteHcmioAndTcnud(@RequestBody CreateHcmioAndTcnudRequest request) {

        try {
            UnrealDetailResponse response = this.unrealService.CraeteHcmioAndTcnud(request);
            return response;
        } catch (Exception e) {
            UnrealDetailResponse unrealDetailResponse = new UnrealDetailResponse();
            unrealDetailResponse.setResponseCode("005");
            unrealDetailResponse.setMessage("伺服器忙碌中，請稍後嘗試");

            return unrealDetailResponse;

        }
    }
}

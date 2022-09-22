package com.example.springservice.controller;

import com.example.springservice.controller.dto.request.TodayDeliveryFeeRequest;
import com.example.springservice.service.TodayDeliveryFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TodayDeliveryFee")
public class TodayDeliveryFeeController {

    @Autowired
    TodayDeliveryFeeService todayDeliveryFeeService;

    @PostMapping()
    public String getDeliveryFee(@RequestBody TodayDeliveryFeeRequest request) {


        try {
            String response = todayDeliveryFeeService.getDeliveryFee(request);
            return response;

        } catch (Exception e) {
            String response = ("005 伺服器忙碌中，請稍後嘗試");
            return response;

        }
    }
}

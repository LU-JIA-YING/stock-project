package com.example.springservice.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealSumResponse {

    private List<UnrealSunResult> resultList;
    private String responseCode;
    private String message;

}

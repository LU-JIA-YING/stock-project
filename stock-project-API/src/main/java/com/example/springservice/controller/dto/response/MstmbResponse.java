package com.example.springservice.controller.dto.response;

import com.example.springservice.model.entity.Mstmb;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MstmbResponse {

    private String status;
    private Mstmb mstmb;

}


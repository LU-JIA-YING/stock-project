package com.example.springservice.controller.dto.response;

import com.example.springservice.model.entity.Symbol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String status;
    private List<Symbol> symbolList;

}

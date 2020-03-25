package com.fsp.solative.index.dto;

import lombok.Data;


public @Data
class InstrumentDTO {
    private String instrument;
    private Double price;
    private Long timestamp;
}

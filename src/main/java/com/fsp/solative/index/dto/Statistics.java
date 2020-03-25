package com.fsp.solative.index.dto;

import lombok.Data;

/**
 * This class is intended to serve as a Data Transfer Object for Statistics calculated in system.
 */
public @Data
class Statistics {
    private Double avg=0.0;
    private Double max=0.0;
    private Double min=0.0;
    private Long count=0l;
}

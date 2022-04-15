package org.titanic.telegram.dto;

import lombok.Data;

@Data
public class UserData {
    private int state;

    private String symbol;
    private double volume;
    private int durationHourMin;
    private int durationHourMax;
    private double priceMin;
    private double priceMax;
    private int numberOfTradesMin;
    private int numberOfTradesMax;
    private int stopById;
}

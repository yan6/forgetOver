package com.example.demo.core.project;

import lombok.Data;

@Data
public class PayPalTrackerInfo {
    private String transaction_id;  //paypal订单id
    private String status;
}

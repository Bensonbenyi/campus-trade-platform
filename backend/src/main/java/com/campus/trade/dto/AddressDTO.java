package com.campus.trade.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String contactName;
    private String contactPhone;
    private String campus;
    private String building;
    private String room;
    private Boolean isDefault;
}

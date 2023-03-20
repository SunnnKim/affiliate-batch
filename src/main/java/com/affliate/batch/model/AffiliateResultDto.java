package com.affliate.batch.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
public class AffiliateResultDto {

    private String ordNo;
    private String custNo;
    private String itemCode;
    private Integer quantity;
    private Double salePrice;
    private String itemName;
    private String affiliateCode;
    private String affiliateName;
    private String itemTypeCode;
    private LocalDateTime orderDtm;
    private String affiliateCustNo;

    private boolean isSkipItem;

}

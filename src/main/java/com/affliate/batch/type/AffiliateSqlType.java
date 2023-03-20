package com.affliate.batch.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AffiliateSqlType {

    A_COMPANY (
        "SELECT A.ORD_NO, CUST_NO, AFFILIATE_CD, ITEM_TYPE_CD, AFFILIATE_CUST_NO, ORD_DTM_DTL, SALE_PRICE, QTY, ITEM_CD ",
        "FROM adm.ORDER_DTL A INNER JOIN AFFLIATE_ORD_INFO B ON A.ORD_NO = B.ORD_NO",
        "WHERE B.AFFILIATE_CD = :affiliateCode",
        ""
    ),
    B_COMPANY (""
            ,""
            ,""
            ,""
    );

    private final String selectClause;
    private final String fromClause;
    private final String whereClause;
    private final String groupClause;

}

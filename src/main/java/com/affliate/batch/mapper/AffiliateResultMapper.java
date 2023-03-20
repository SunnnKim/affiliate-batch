package com.affliate.batch.mapper;

import com.affliate.batch.model.AffiliateResultDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AffiliateResultMapper implements RowMapper<AffiliateResultDto> {

    @Override
    public AffiliateResultDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AffiliateResultDto.builder()
                .ordNo(rs.getString("ORD_NO"))
                .custNo(rs.getString("CUST_NO"))
                .affiliateCode(rs.getString("AFFILIATE_CD"))
                .itemTypeCode(rs.getString("ITEM_TYPE_CD"))
                .affiliateCustNo(rs.getString("AFFILIATE_CUST_NO"))
                .orderDtm(rs.getObject("ORD_DTM_DTL", LocalDateTime.class))
                .salePrice(rs.getDouble("SALE_PRICE"))
                .quantity(rs.getObject("QTY", Integer.class))
                .itemCode(rs.getString("ITEM_CD"))
                .build();

    }

}

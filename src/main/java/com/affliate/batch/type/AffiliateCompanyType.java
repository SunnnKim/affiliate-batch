package com.affliate.batch.type;

import com.affliate.batch.utility.AffliateTimeUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AffiliateCompanyType {
    A_COMPANY("01", 20, "a_company_all", AffiliateFileType.CSV, AffiliateSqlType.A_COMPANY),
    B_COMPANY("02", 26, "b_company_pc", AffiliateFileType.CSV, AffiliateSqlType.B_COMPANY)
    ;


    private final String affiliateCode; // 제휴사코드
    private final int adjustmentDate; // 제휴사 정산일
    private final String fileName;
    private final AffiliateFileType fileType;
    private final AffiliateSqlType affiliateSqlType;


    public static AffiliateCompanyType findTypeByAffiliateName(String name){
        for(AffiliateCompanyType type : values()){
            if(type.name().equals(name)){
                return type;
            }
        }
        return null;
    }

    public static AffiliateCompanyType findTypeByAffiliateCode(String code){
        for(AffiliateCompanyType type : values()){
            if(type.name().equals(code)){
                return type;
            }
        }
        return null;
    }
    public String getFullFilePathWithDate(String path){
        return path + "/" + AffliateTimeUtility.getBatchStartDate() + "/" + fileName + "." + fileType.getExtension();
    }

    public String getTemporaryFilePath(String temporayPath){
        return temporayPath + "/" + fileName + "." + fileType.getExtension();
    }
    public String getFileName(){
        return fileName + "." + fileType.getExtension();
    }
}

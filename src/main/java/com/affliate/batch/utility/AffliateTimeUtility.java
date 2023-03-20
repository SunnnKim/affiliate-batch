package com.affliate.batch.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AffliateTimeUtility {

    /* 정산시작일 : 전월 1일 */
    public static LocalDate getAdjestmentStartDate(){
        return LocalDate.now().minusMonths(1).withDayOfMonth(1);
    }
    /* 정산마감일 전월 말일 */
    public static LocalDate getAdjestmentEndDate(){
        return LocalDate.now().withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
    }

    /* 배치시작일 */
    public static String getBatchStartDateTime(){ return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")); }
    public static String getBatchStartDate(){ return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); }


}

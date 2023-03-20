# Affiliate-batch

💰 제휴주문정산파일생성 배치 샘플만들기

## 개요

특정 제휴사의 전월 제휴주문실적을 추출하고 제외 상품을 걸러낸 뒤 CSV파일을 생성해주는 배치.

실제 서비스에서 수행되고 있는 정산 배치를 참고하여 만든 샘플로직

## 구조

1. `AffliateSampleJobConfiguration` : 메인 잡, 2개의 Step으로 구성
2. `makeAffiliateFileStep` : 제휴데이터추출, 정산제외상품코드 적용 및 CSV 파일로 생성하는 Step
3. `uploadFileToFinalPathStep` : Tasklet 으로 구성된 업로드 경로 변경 Step 
4. Enum
    - `AffiliateCompanyType` : 제휴사정보 작성
    - `AffiliateFileType` : 정산파일정보
    - `AffiliateSqlType` : 제휴사별 정산쿼리
5. `AffliateTimeUtility` : 시간 관련 유틸함수

## 기술

- JDK 11
- Spring Boot 2.7.9
- Spring Batch 4
- JDBC API
- MySQL

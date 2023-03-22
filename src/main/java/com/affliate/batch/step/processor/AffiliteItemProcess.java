package com.affliate.batch.step.processor;

import com.affliate.batch.model.AffiliateResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class AffiliteItemProcess implements ItemProcessor<AffiliateResultDto, AffiliateResultDto> {

    /* 제외카테고리 */
    private final String[] excludedItemTypeCode = {
            "20", "22",
            "35",
            "80", "85",
            "90", "97", "98"
    };

    @Override
    public AffiliateResultDto process(AffiliateResultDto result) {
        log.info(">>>>>>>>>>>> affiliateProcessor");
        // 제외조건 처리
        Optional.of(result).ifPresent( data -> {
                    String itemTypeCode = data.getItemTypeCode();
                    // 제외카테고리코드에 포함되는 경우
                    Optional<String> optional = Arrays.stream(excludedItemTypeCode)
                            .filter(itemTypeCode::startsWith)
                            .findAny();
                    optional.ifPresent(op -> data.setSkipItem(true));
                });

        return result.isSkipItem() ? null : result;
    }
}

package io.github.gobsex.comparators;

import io.github.gobsex.result.SimilarityRecord;
import org.apache.commons.lang3.StringUtils;

public class StringJaroWinklerComparator implements Comparator<SimilarityRecord<String>, String> {
    @Override
    public SimilarityRecord<String> compare(String source, String target) {
        return new SimilarityRecord<>(source, target, StringUtils.getJaroWinklerDistance(source, target));
    }
}

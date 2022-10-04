package comparator;

import org.apache.commons.lang3.StringUtils;
import result.SimilarityRecord;

public class StringJaroWinklerComparator implements Comparator<SimilarityRecord<String>, String> {
    @Override
    public SimilarityRecord<String> compare(String source, String target) {
        return new SimilarityRecord<>(source, target, StringUtils.getJaroWinklerDistance(source, target));
    }
}

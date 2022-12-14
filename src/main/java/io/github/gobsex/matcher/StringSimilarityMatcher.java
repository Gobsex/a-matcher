package io.github.gobsex.matcher;

import io.github.gobsex.comparators.StringComboComparator;
import io.github.gobsex.comparators.Comparator;
import io.github.gobsex.filter.ThresholdFilter;
import io.github.gobsex.result.SimilarityRecord;
import io.github.gobsex.result.SimilarityResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StringSimilarityMatcher implements Matcher<String, SimilarityResult<String>> {
    @Builder.Default
    private ThresholdFilter filter = new ThresholdFilter(0.7);
    @Builder.Default
    private Comparator<SimilarityRecord<String>, String> comparator = StringComboComparator.builder().build();

    @Override
    public SimilarityResult<String> find(String source, Collection<String> targetGroup) {
        List<SimilarityRecord<String>> matches = targetGroup.stream().map(target -> comparator.compare(source, target)).filter(filter::filter).sorted(java.util.Comparator.comparingDouble(SimilarityRecord::getSimilarityCoefficient)).collect(Collectors.toList());
        SimilarityRecord<String> bestMatch = matches.stream().max(java.util.Comparator.comparingDouble(SimilarityRecord::getSimilarityCoefficient)).orElse(null);
        return new SimilarityResult<>(matches, bestMatch);
    }
}

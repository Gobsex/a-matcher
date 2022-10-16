package io.github.gobsex.filter;

import io.github.gobsex.result.SimilarityRecord;

public class ThresholdFilter implements Filter<SimilarityRecord<?>> {
    private final double threshold;

    public ThresholdFilter(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean filter(SimilarityRecord<?> element) {
        return threshold <= element.getSimilarityCoefficient();
    }
}

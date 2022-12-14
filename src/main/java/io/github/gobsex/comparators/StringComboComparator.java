package io.github.gobsex.comparators;

import io.github.gobsex.comparators.strategy.ComparingAlgorithm;
import io.github.gobsex.comparators.strategy.WordsComparingStrategy;
import info.debatty.java.stringsimilarity.*;
import io.github.gobsex.result.SimilarityRecord;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Builder
public class StringComboComparator implements Comparator<SimilarityRecord<String>, String> {
    @Builder.Default
    private WordsComparingStrategy wordsComparingStrategy = WordsComparingStrategy.MAX;
    @Builder.Default
    private String skipWordsPattern = null;
    @Builder.Default
    private boolean compareIgnoreCase = true;
    @Builder.Default
    private String separator = " ";
    @Builder.Default
    private boolean distinctWords = false;
    @Builder.Default
    private boolean normalizeSpaces = true;
    @Builder.Default
    private boolean compareByWords = true;
    @Builder.Default
    private ComparingAlgorithm comparingAlgorithm = ComparingAlgorithm.RATCLIFF_OBERSHELP;
    @Builder.Default
    private Double similarityThreshold = null;
    @Builder.Default
    private Integer minWordLength = null;

    @Override
    public SimilarityRecord<String> compare(String source, String target) {
        SimilarityRecord<String> record = new SimilarityRecord<>(source, target, 0);
        if (source == null || target == null) {
            return record;
        }
        if (normalizeSpaces) {
            source = source.replaceAll(separator + "+", separator).trim();
            target = target.replaceAll(separator + "+", separator).trim();
        }
        if (compareIgnoreCase) {
            source = source.toLowerCase();
            target = target.toLowerCase();
        }
        double coefficient = !compareByWords ? compareString(source, target) : compareForCommonWords(source, target);
        record.setSimilarityCoefficient(coefficient);
        return record;
    }

    public List<String> splitToWords(String str) {
        List<String> collect = Arrays.stream(str.split(separator)).filter(x -> {
            if (skipWordsPattern != null) {
                return !x.matches(skipWordsPattern);
            }
            return true;
        }).collect(Collectors.toList());
        // join words with length less than minWordLength to previous word
        if (minWordLength != null) {
            for (int i = 0; i < collect.size(); i++) {
                if (collect.get(i).length() < minWordLength) {
                    if (i > 0) {
                        collect.set(i - 1, collect.get(i - 1) + separator + collect.get(i));
                        collect.remove(i);
                        i--;
                    }
                }
            }
        }
        if (distinctWords) {
            collect = collect.stream().distinct().collect(Collectors.toList());
        }
        return collect;
    }

    public double compareForCommonWords(String s1, String s2) {
        // make method null safe
        if (s1 == null || s2 == null) return 0.0;

        List<String> words1 = splitToWords(s1);
        List<String> words2 = splitToWords(s2);
        double coefficientSum = 0;
        // calculate total chars in words1
        int totalChars1 = words1.stream().mapToInt(String::length).sum();
        int totalChars2 = words2.stream().mapToInt(String::length).sum();
        for (String word1 : words1) {
            double coefficient = 0;
            for (String word2 : words2) {
                double compareCoefficient = compareString(word1, word2);
                if (coefficient < compareCoefficient) {
                    coefficient = compareCoefficient;
                }
            }
            int totalChars;
            switch (wordsComparingStrategy) {
                case RIGHT:
                    totalChars = totalChars2;
                    break;
                case AVERAGE:
                    totalChars = (totalChars1 + totalChars2) / 2;
                    break;
                case MAX:
                    totalChars = Math.max(totalChars1, totalChars2);
                    break;
                case LEFT:
                    totalChars = totalChars1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            coefficientSum += coefficient * word1.length() / totalChars;
        }
        return Math.min(1, coefficientSum);
    }

    public double compareString(String s1, String s2) {
        double similarity;
        switch (comparingAlgorithm) {
            case JARO_WINKLER:
                similarity = new JaroWinkler().similarity(s1, s2);
                break;
            case JACCARD:
                similarity = new Jaccard().similarity(s1, s2);
                break;
            case COSINE:
                similarity = new Cosine().similarity(s1, s2);
                break;
            case LEVENSHTEIN:
                similarity = new Levenshtein().distance(s1, s2, 1);
                break;
            case NGRAM:
                similarity = new NGram().distance(s1, s2);
                break;
            case SORENSEN_DICE:
                similarity = new SorensenDice().similarity(s1, s2);
                break;
            case RATCLIFF_OBERSHELP:
                similarity = new RatcliffObershelp().similarity(s1, s2);
                break;
            case EQUALS:
                similarity = Objects.equals(s1, s2) ? 1 : 0;
                break;
            default:
                similarity = 0;
                break;
        }
        if (similarityThreshold != null) {
            return similarityThreshold <= similarity ? similarity : 0;
        }
        return similarity;
    }
}

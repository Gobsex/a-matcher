package comparator;

import comparator.strategy.ComparingAlgorithm;
import comparator.strategy.WordsComparingStrategy;
import info.debatty.java.stringsimilarity.*;
import lombok.Builder;
import result.SimilarityRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Builder
public class StringComboComparator implements Comparator<SimilarityRecord<String>, String> {
    @Builder.Default
    private WordsComparingStrategy wordsComparingStrategy = WordsComparingStrategy.MAX;
    @Builder.Default
    private String skipWordsPattern = null;
    @Builder.Default
    private boolean compareIgnoreCase = true;
    @Builder.Default
    private String spitBy = " ";
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

    @Override
    public SimilarityRecord<String> compare(String source, String target) {
        SimilarityRecord<String> record = new SimilarityRecord<>(source, target, 0);
        if (source == null || target == null) {
            return new SimilarityRecord<>(source, target, 0);
        }
        if (normalizeSpaces) {
            source = source.replaceAll(spitBy + "+", spitBy).trim();
            target = target.replaceAll(spitBy + "+", spitBy).trim();
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
        Stream<String> stream = Arrays.stream(str.split(spitBy)).filter(x -> {
            if (skipWordsPattern != null) {
                return !x.matches(skipWordsPattern);
            }
            return true;
        });
        if (distinctWords) stream = stream.distinct();
        return stream.collect(Collectors.toList());
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
            int totalChars = switch (wordsComparingStrategy) {
                case RIGHT -> totalChars2;
                case AVERAGE -> (totalChars1 + totalChars2) / 2;
                case MAX -> Math.max(totalChars1, totalChars2);
                case LEFT -> totalChars1;
            };
            coefficientSum += coefficient * word1.length() / totalChars;
        }
        return Math.min(1, coefficientSum);
    }

    public double compareString(String s1, String s2) {
        double similarity = switch (comparingAlgorithm) {
            case JARO_WINKLER -> new JaroWinkler().similarity(s1, s2);
            case JACCARD -> new Jaccard().similarity(s1, s2);
            case COSINE -> new Cosine().similarity(s1, s2);
            case LEVENSHTEIN -> new Levenshtein().distance(s1, s2, 1);
            case NGRAM -> new NGram().distance(s1, s2);
            case SORENSEN_DICE -> new SorensenDice().similarity(s1, s2);
            case RATCLIFF_OBERSHELP -> new RatcliffObershelp().similarity(s1, s2);
            case EQUALS -> Objects.equals(s1, s2) ? 1 : 0;
            default -> 0;
        };
        if (similarityThreshold != null) {
            return similarityThreshold <= similarity ? similarity : 0;
        }
        return similarity;
    }
}
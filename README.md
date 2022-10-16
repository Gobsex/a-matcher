# Overview
This library provide flexible functionality to compare and match strings. 
It is based on a lot of comparing algorithms and can be used to find similar strings in a list of strings.
# Installation
## Maven
```xml
<dependency>
  <groupId>io.github.gobsex</groupId>
  <artifactId>a-matcher</artifactId>
  <version>1.1.1</version>
</dependency>
```
## Gradle
```groovy
implementation 'io.github.gobsex:a-matcher:1.1.1'
```
# Usage
## String Matcher
### Configuration (example)
```java
StringSimilarityMatcher matcher = StringSimilarityMatcher.builder()
                .filter(new ThresholdFilter(0.5))
                .comparator(
                        StringComboComparator.builder()
                                .compareIgnoreCase(true)
                                .compareByWords(true)
                                    .wordsComparingStrategy(WordsComparingStrategy.MAX)
                                    .minWordLength(2)
                                    .similarityThreshold(0.5)
                                    .separator(" ")
                                .normalizeSpaces(true)
                                .comparingAlgorithm(ComparingAlgorithm.RATCLIFF_OBERSHELP)
                                .build()
                )
                .build();
```
### Find suitable strings (example)
```java
SimilarityResult<String> result = matcher.find("Hello World", 
        List.of(
        "word hello",
        "Hello World!", 
        "test record",
        " Hello World  ")
        );
```
### Results
```json
{
  "matches":[
    {
      "source":"Hello World",
      "target":"test record",
      "similarityCoefficient":0.2727272727272727
    },
    {
      "source":"Hello World",
      "target":"Hello World!",
      "similarityCoefficient":0.8677685950413223
    },
    {
      "source":"Hello World",
      "target":"word hello",
      "similarityCoefficient":1.0
    },
    {
      "source":"Hello World",
      "target":"  Hello World   ",
      "similarityCoefficient":1.0
    }
  ],
  "bestMatch":{
    "source":"Hello World",
    "target":"  Hello World   ",
    "similarityCoefficient":1.0
  }
}
```
### Description
#### Classes
* `ThresholdFilter` - filter to exclude matches with a similarity coefficient less than the specified threshold.
* `SimilarityResult` - this is a result of matching. It contains a list of matches and the best match.
* `SimilarityRecord` - this is a match of strings. It contains a source string, a target string and a similarity coefficient.
* `StringComboComparator` - this is a composite comparator. It can be used to compare strings by words and with ignore case, normalize spaces, etc.
* `ComparingAlgorithm` - this is an enum with comparing algorithms.
* `WordsComparingStrategy` - this is an enum with words comparing strategies.
* `StringSimilarityMatcher` - this is a matcher to find suitable strings.

#### StringSimilarityMatcher
| Param      | Description                                                                                |
|------------|--------------------------------------------------------------------------------------------|
| filter     | Filter to exclude matches with a similarity coefficient less than the specified threshold. |
| comparator | Comparator to compare strings. It contains a lot of parameters for comparing.              |

#### StringComboComparator
| Param                  | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| compareByWords         | If true, each string will be split into words and each word will be compared separately                                                                                                                                                                                                                                                                                                                                                                    |
| wordsComparingStrategy | Strategy for comparing words. <br/>`MAX` - means that the similarity coefficient will be calculated for max length between target and source. <br/>`LEFT` - means that the similarity coefficient will be calculated for source length. <br/>`RIGHT` - means that the similarity coefficient will be calculated for target length. <br/>`AVERAGE` - means that the similarity coefficient will be calculated for average length between target and source. |
| minWordLength          | Minimum length of word for comparing                                                                                                                                                                                                                                                                                                                                                                                                                       |
| similarityThreshold    | Similarity threshold for words comparing. All words with similarity coefficient less than this param will be ignored.                                                                                                                                                                                                                                                                                                                                      |
| separator              | Separator used for splitting strings into words                                                                                                                                                                                                                                                                                                                                                                                                            |
| normalizeSpaces        | If true, all spaces will be replaced with one space                                                                                                                                                                                                                                                                                                                                                                                                        |
| distinctWords          | If true, words will be compared without duplicates                                                                                                                                                                                                                                                                                                                                                                                                         |
| compareIgnoreCase      | If true, strings will be compared ignoring case                                                                                                                                                                                                                                                                                                                                                                                                            |
| comparingAlgorithm     | Comparing algorithm, used for comparing strings                                                                                                                                                                                                                                                                                                                                                                                                            |


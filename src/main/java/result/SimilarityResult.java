package result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class SimilarityResult<T> {
    private final List<SimilarityRecord<T>> matches;
    private final SimilarityRecord<T> bestMatch;
}

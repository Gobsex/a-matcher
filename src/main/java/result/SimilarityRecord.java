package result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class SimilarityRecord<T> {
    private T source;
    private T target;
    private double similarityCoefficient;
}

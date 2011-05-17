package hmmpostagging.scoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Used to do overall calculations using Scorables.
 *
 * @author Taylor
 */
public class Scorer {

    /**
     * Compares a map of Scorables. For each entry in the map, it will compare
     * the scores, and return the result in a larger map.
     * This map maps the entries of the first map to the entries's score.
     * Oh God. That sentence.
     *
     * The scores can range from 0.0 to 1.0
     * 0.0 is all incorrect. 1.0 is all correct
     *
     * @param predictedTags The map of scorables to score.
     * @return The map of the pairs of scorables to their scores.
     */
    public static Map<Entry<? extends Scorable, ? extends Scorable>, Double> score(Map<? extends Scorable, ? extends Scorable> predictedTags) {
        HashMap<Entry<? extends Scorable, ? extends Scorable>, Double> map = new HashMap<Entry<? extends Scorable, ? extends Scorable>, Double>();
        for(Entry<? extends Scorable, ? extends Scorable> entry: predictedTags.entrySet()) {
            Scorable gold = entry.getKey();
            Scorable predicted = entry.getValue();
            double score = predicted.numCorrectComparedTo(gold)/(double)predicted.numTotalComparedTo(gold);
            map.put(entry, score);
        }
        return map;
    }

}

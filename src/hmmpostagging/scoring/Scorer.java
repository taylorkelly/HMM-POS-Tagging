/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmmpostagging.scoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author taylor
 */
public class Scorer {

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

package hmmpostagging.hmmoo;

import hmmpostagging.Word;
import hmmpostagging.hmm.ObsersationLikelihoods;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author taylor
 */
public class OOObservationLikelihoods implements ObsersationLikelihoods<Word> {
    private HashMap<String, HashMap<String, Integer>> likelihoodMapping;
    private HashMap<String, Integer> stateTotals;
    private HashMap<String, LinkedList<Entry<String, Integer>>> sortedLikelihoods;

    public OOObservationLikelihoods() {
        likelihoodMapping = new HashMap<String, HashMap<String, Integer>>();
        stateTotals = new HashMap<String, Integer>();

        sortedLikelihoods = null;
    }

    public void add(Word currWord) {
        add(currWord.getState(), currWord.getWord());
    }

    private void add(String tag, String word) {
        if (likelihoodMapping.containsKey(tag)) {
            HashMap<String, Integer> map = likelihoodMapping.get(tag);
            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
            stateTotals.put(tag, stateTotals.get(tag) + 1);
        } else {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(word, 1);
            likelihoodMapping.put(tag, map);
            stateTotals.put(tag, 1);
        }
        sortedLikelihoods = null;
    }

    public String getMostLikelyWord(String tag) {
        if (!likelihoodMapping.containsKey(tag)) {
            return null;
        }
        if (sortedLikelihoods == null) {
            sort();
        }
        LinkedList<Entry<String, Integer>> tagList = sortedLikelihoods.get(tag);

        return tagList.get(0).getKey();
    }

    public String getRandomWord(String tag) {
        if (!likelihoodMapping.containsKey(tag)) {
            return null;
        }
        Random rand = new Random();
        int value = rand.nextInt(stateTotals.get(tag));

        for (Entry<String, Integer> entry :
                likelihoodMapping.get(tag).entrySet()) {
            value -= entry.getValue();
            if (value < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void sort() {
        sortedLikelihoods = new HashMap<String, LinkedList<Entry<String, Integer>>>();
        for (Entry<String, HashMap<String, Integer>> entry :
                likelihoodMapping.entrySet()) {

            LinkedList<Entry<String, Integer>> list = new LinkedList(entry.getValue().entrySet());
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
                }
            });

            sortedLikelihoods.put(entry.getKey(), list);
        }
    }

    public double probability(String tag, String word) {
        //TODO handle unknown words better
        if (!likelihoodMapping.containsKey(tag)) {
            return 0;
        }
        HashMap<String, Integer> map = likelihoodMapping.get(tag);
        if (!map.containsKey(word)) {
            //return 0;
            return unknownWord(map, word) / (double) (stateTotals.get(tag) + 1);
        }

        return (double) map.get(word) / stateTotals.get(tag);
    }

    public double unknownWord(HashMap<String, Integer> tagMap, String word) {
        // TODO Something cooler
        return 0.5;
    }

    private double average(Collection<Integer> values) {
        int sum = 0;
        for (Integer number : values) {
            sum += number;
        }
        return sum / (double) values.size();
    }

    public void save(BufferedWriter writer) throws IOException {
        writer.write("**Likelihoods**");
        writer.newLine();
        for (Entry<String, HashMap<String, Integer>> mainState :
                likelihoodMapping.entrySet()) {
            if (mainState.getKey() == null) {
                writer.write("^null^");
            } else {
                writer.write(mainState.getKey());
            }
            writer.write(":");
            writer.write(stateTotals.get(mainState.getKey()).toString());
            writer.newLine();
            writer.write("-");
            for (Entry<String, Integer> innerState :
                    mainState.getValue().entrySet()) {
                if (innerState.getKey() == null) {
                    writer.write("^null^");
                } else {
                    writer.write(innerState.getKey());
                }
                writer.write(":");
                writer.write(innerState.getValue().toString());
                writer.write(";");
            }
            writer.newLine();
        }
    }
}

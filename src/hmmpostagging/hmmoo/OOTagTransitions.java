package hmmpostagging.hmmoo;

import hmmpostagging.Word;
import hmmpostagging.hmm.TagTransitions;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author taylor
 */
public class OOTagTransitions implements TagTransitions<Word> {
    private HashMap<String, HashMap<String, Integer>> transitionMapping;
    private HashMap<String, Integer> stateTotals;
    private HashMap<String, LinkedList<Entry<String, Integer>>> sortedTransitions;

    public OOTagTransitions() {
        transitionMapping = new HashMap<String, HashMap<String, Integer>>();
        stateTotals = new HashMap<String, Integer>();

        sortedTransitions = null;
    }

    public void add(Word fromWord, Word toWord) {
        add(fromWord.getState(), toWord.getState());
    }

    public void startState(Word currWord) {
        add(null, currWord.getState());
    }

    public void endState(Word currWord) {
        add(currWord.getState(), null);
    }

    private void add(String fromTag, String toTag) {
        if (transitionMapping.containsKey(fromTag)) {
            HashMap<String, Integer> map = transitionMapping.get(fromTag);
            if (map.containsKey(toTag)) {
                map.put(toTag, map.get(toTag) + 1);
            } else {
                map.put(toTag, 1);
            }
            stateTotals.put(fromTag, stateTotals.get(fromTag) + 1);
        } else {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(toTag, 1);
            transitionMapping.put(fromTag, map);
            stateTotals.put(fromTag, 1);
        }
        sortedTransitions = null;
    }

    public String getMostLikelyTag(String fromTag) {
        if (!transitionMapping.containsKey(fromTag)) {
            return null;
        }
        if (sortedTransitions == null) {
            sort();
        }
        LinkedList<Entry<String, Integer>> tagList = sortedTransitions.get(fromTag);

        return tagList.get(0).getKey();
    }

    public String getRandomTag(String fromTag) {
        if (!transitionMapping.containsKey(fromTag)) {
            return null;
        }
        Random rand = new Random();
        int value = rand.nextInt(stateTotals.get(fromTag));

        for (Entry<String, Integer> entry :
                transitionMapping.get(fromTag).entrySet()) {
            value -= entry.getValue();
            if (value < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void sort() {
        sortedTransitions = new HashMap<String, LinkedList<Entry<String, Integer>>>();
        for (Entry<String, HashMap<String, Integer>> entry :
                transitionMapping.entrySet()) {

            LinkedList<Entry<String, Integer>> list = new LinkedList(entry.getValue().entrySet());
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
                }
            });

            sortedTransitions.put(entry.getKey(), list);
        }
    }

    public double probability(String fromTag, String toTag) {
        if (!transitionMapping.containsKey(fromTag)) {
            return 0;
        }
        HashMap<String, Integer> map = transitionMapping.get(fromTag);
        if (!map.containsKey(toTag)) {
            return 0;
        }
        return (double) map.get(toTag) / stateTotals.get(fromTag);
    }

    public void save(BufferedWriter writer) throws IOException {
        writer.write("**Transitions**");
        writer.newLine();
        for (Entry<String, HashMap<String, Integer>> mainState :
                transitionMapping.entrySet()) {
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

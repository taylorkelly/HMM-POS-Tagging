package hmmpostagging.hmmoo;

import hmmpostagging.Word;
import hmmpostagging.hmm.ObsersationLikelihoods;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * An implementation of the ObservationLikelihoods using HashMaps.
 * This makes it a little easier than managing a matrix or array of arrays.
 *
 * @see hmmpostagging.hmm.ObsersationLikelihoods
 */
public class OOObservationLikelihoods implements ObsersationLikelihoods<Word> {
    private HashMap<String, HashMap<String, Integer>> likelihoodMapping;
    private HashMap<String, Integer> stateTotals;
    private HashSet<String> seenWords;
    private HashMap<String, LinkedList<Entry<String, Integer>>> sortedLikelihoods;
    public static double ONES_COUNT_CONSTANT = 1.072;

    public OOObservationLikelihoods() {
        likelihoodMapping = new HashMap<String, HashMap<String, Integer>>();
        stateTotals = new HashMap<String, Integer>();
        seenWords = new HashSet<String>();

        sortedLikelihoods = null;
    }

    public OOObservationLikelihoods(String imported) {
        this();

        String[] states = imported.split("}");
        for (String stateString : states) {
            String[] pieces = stateString.split("\\{");
            String stateName = pieces[0];
            if (stateName.equals("^null^")) {
                stateName = null;
            }
            String[] likelihoods = pieces[1].split("\\|");
            int sum = 0;
            HashMap<String, Integer> likelihoodMap = new HashMap<String, Integer>();
            for (String likelihood : likelihoods) {
                String[] pieces2 = likelihood.split("<");
                String word = pieces2[0];
                if (word.equals("^null^")) {
                    word = null;
                }
                seenWords.add(word);
                int value = Integer.parseInt(pieces2[1]);
                sum += value;
                likelihoodMap.put(word, value);

            }
            likelihoodMapping.put(stateName, likelihoodMap);
            stateTotals.put(stateName, sum);
        }
        sort();
    }

    public void add(Word currWord) {
        add(currWord.getState(), currWord.getValue());
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
        seenWords.add(word);
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

    public final void sort() {
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
            return unknownWord(word, tag);
        }

        return (double) map.get(word) / stateTotals.get(tag);
    }

    public double unknownWord(String word, String tag) {
        if (seenWords.contains(word)) {
            // If the word has been seen before, but not in this state,
            // then it's a zero.
            return 0;
        } else {
            // Otherwise do a calculation to see what value to give it
            HashMap<String, Integer> tagMap = likelihoodMapping.get(tag);
            // Give unknown words a higher probability for the states that
            // have more one word counts than non-one word counts
            int onesCount = getOnesCount(tagMap.values());
            return Math.pow(ONES_COUNT_CONSTANT, onesCount) / (stateTotals.get(tag) * getStates(false).size());
        }
    }

    /**
     * Return the number of likelihoods with a count of one, minus those without.
     * This is an attempt to find the best place for unknown words
     * @param values The values of counts for the likelihoods of a state
     * @return The ideal 'ones count' number
     */
    private int getOnesCount(Collection<Integer> values) {
        int sum = 0;
        for (Integer number : values) {
            if (number.intValue() == 1) {
                sum++;
            } else {
                sum--;
            }
        }
        return sum;
    }

    private double average(Collection<Integer> values) {
        int sum = 0;
        for (Integer number : values) {
            sum += number;
        }
        return sum / (double) values.size();
    }

    public void save(BufferedWriter writer) throws IOException {
        for (Entry<String, HashMap<String, Integer>> mainState :
                likelihoodMapping.entrySet()) {
            if (mainState.getKey() == null) {
                writer.write("^null^");
            } else {
                writer.write(mainState.getKey());
            }
            writer.write("{");
            for (Entry<String, Integer> innerState :
                    mainState.getValue().entrySet()) {
                if (innerState.getKey() == null) {
                    writer.write("^null^");
                } else {
                    writer.write(innerState.getKey());
                }
                writer.write("<");
                writer.write(innerState.getValue().toString());
                writer.write("|");
            }
            writer.write("}");
        }
    }

    public Set<String> getStates(boolean includeNull) {
        Set<String> states = new HashSet<String>();
        states.addAll(likelihoodMapping.keySet());
        if (!includeNull) {
            states.remove(null);
        }
        return states;
    }

    public void unsupervisedAdd(Word currWord, String possibleState) {
        add(possibleState, currWord.getValue());
    }

    public boolean hasState(String value) {
        return seenWords.contains(value);
    }

    public String getState(String value) {
        for (Entry<String, HashMap<String, Integer>> entry :
                likelihoodMapping.entrySet()) {
            for (String entryValue : entry.getValue().keySet()) {
                if (entryValue.equals(value)) return entry.getKey();
            }
        }
        return null;
    }
}

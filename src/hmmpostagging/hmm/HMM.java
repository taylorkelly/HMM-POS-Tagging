package hmmpostagging.hmm;

import hmmpostagging.Sentence;
import hmmpostagging.Word;
import hmmpostagging.hmm.structures.Observation;
import hmmpostagging.hmm.structures.ObservationSequence;
import hmmpostagging.hmm.structures.ObservationSequenceStructure;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An HMM!
 * An abstract HMM, allowing the actual implementation to be changed up slightly.
 * The main pieces (ObservationLikelihoods and TagTransitions) are non-specified
 * and could be implemented OO-ly or using tables.
 *
 * Any class that extends HMM should initialize these variables in the
 * constructor.
 *
 * Has methods to train (sup or un-sup), analyze, predict, and all sorts of
 * crazy stuff.
 *
 * I would've liked it to be more abstracted away from Words and Sentences
 * (which it was original super-tied to), but I only got half way.
 *
 * @author Taylor
 */
public abstract class HMM {
    protected ObsersationLikelihoods likelihoods;
    protected TagTransitions transitions;
    protected Set<String> states;

    /**
     * Initializes the set of states, leaving the other two instance variables
     * up to actual implementation.
     * If there was more time, more abstraction *should* be done.
     */
    public HMM() {
        states = new HashSet<String>();
    }

    /**
     * Supervised training of the HMM given a set of observation sequences.
     * This will expect that the Observations have both a valid value and state.
     *
     * @param set The container holding the observation sequences.
     */
    public void train(ObservationSequenceStructure set) {
        List<ObservationSequence> sequences = set.getSequences();
        for (ObservationSequence sequence : sequences) {
            int size = sequence.length();
            for (int i = 0; i < size; i++) {
                Observation currO = sequence.get(i);
                likelihoods.add(currO);
                addToStateList(currO);

                if (i > 0) {
                    Observation prevO = sequence.get(i - 1);
                    transitions.add(prevO, currO);
                } else {
                    transitions.startState(currO);
                }

                if (i == size - 1) {
                    transitions.endState(currO);
                }
            }
        }
    }

    /**
     * Adds an observation's state to the HMM's own list of states.
     * @param observation
     */
    private void addToStateList(Observation observation) {
        addToStateList(observation.getState());
    }

    /**
     * Adds a given state to the HMM's own list of states.
     * @param observation
     */
    private void addToStateList(String state) {
        if (!states.contains(state)) {
            states.add(state);
        }
    }

    /**
     * This returns the most likely sentence (complete with both word and tag)
     * from the HMM.
     * It's current implementation is dumb, and does not return the actual
     * most likely sentence, but rather the string of most probable words given
     * the previous ones.
     * @return The most likely sentence
     */
    public Sentence getMostLikelySentence() {
        //TODO Make better, as in comprehensive, rather than just immediate
        // probabilities
        String currTag = transitions.getMostLikelyTag(null);
        Sentence sentence = new Sentence();
        while (currTag != null && sentence.length() < 15) {
            Word word = new Word(likelihoods.getMostLikelyWord(currTag), currTag);
            sentence.addWord(word);
            currTag = transitions.getMostLikelyTag(currTag);
        }
        return sentence;
    }

    /**
     * Returns a random but valid (to this HMM) sentence.
     * @return A random sentence.
     */
    public Sentence getRandomSentence() {
        String currTag = transitions.getRandomTag(null);
        Sentence sentence = new Sentence();
        while (currTag != null) {
            Word word = new Word(likelihoods.getRandomWord(currTag), currTag);
            sentence.addWord(word);
            currTag = transitions.getRandomTag(currTag);
        }
        return sentence;
    }

    /**
     * Computes the likelihood (based on forward algorithm) of a list of 
     * sentences (ignoring their built-in tags) and returns them and their
     * probabilities in a map (key: sentence, value: likelihood)
     * (from 0.0 to 1.0)
     * @param sentences The list of sentences.
     * @return The map of sentences to their probabilities
     */
    public Map<Sentence, Double> likelihoodForward(List<Sentence> sentences) {
        HashMap<Sentence, Double> ret = new HashMap<Sentence, Double>();

        for (Sentence sentence : sentences) {
            double probability = likelihoodForward(sentence);
            ret.put(sentence, probability);
        }

        return ret;
    }

    /**
     * Computes the likelihood (based on forward algorithm) of a structure of
     * sentences (ignoring their built-in tags) and returns them and their
     * probabilities in a map (key: sentence, value: likelihood)
     * (from 0.0 to 1.0)
     * @param sentences The structure of sentences
     * @return The map of sentences to their probabilities
     */
    public Map<Sentence, Double> likelihoodForward(ObservationSequenceStructure set) {
        return likelihoodForward(set.getSequences());
    }

    /**
     * Computes the likelihood (based on forward algorithm) of a sentence,
     * ignoring its built-in tags, and returns the likelihood of that sentence
     * under this HMM.
     * (from 0.0 to 1.0)
     * @param sentence The sentence to analyze.
     * @return The likelihood of the sentence
     */
    public double likelihoodForward(Sentence sentence) {
        double[][] forwardMatrix = new double[numStates()][sentence.length()];
        String[] stateArray = states.toArray(new String[states.size()]);

        // The intialization, from the start state to first word
        for (int i = 0; i < states.size(); i++) {
            String state = stateArray[i];
            forwardMatrix[i][0] = transitions.probability(null, state) * likelihoods.probability(state, sentence.get(0).getValue());
        }

        // The rest of the words in the sentence
        for (int t = 1; t < sentence.length(); t++) {
            for (int j = 0; j < states.size(); j++) {
                double probability = 0;
                for (int i = 0; i < states.size(); i++) {
                    probability += forwardMatrix[i][t - 1] * transitions.probability(stateArray[i], stateArray[j]);
                }
                forwardMatrix[j][t] = probability * likelihoods.probability(stateArray[j], sentence.get(t).getValue());
            }
        }

        // Overall probability, as well as last word -> end state probability.
        double probability = 0;
        for (int i = 0; i < states.size(); i++) {
            probability += forwardMatrix[i][sentence.length() - 1] * transitions.probability(stateArray[i], null);
        }
        return probability;
    }

    /**
     * Computes the likelihood (based on backward algorithm) of a list of
     * sentences (ignoring their built-in tags) and returns them and their
     * probabilities in a map (key: sentence, value: likelihood)
     * (from 0.0 to 1.0)
     * @param sentences The list of sentences.
     * @return The map of sentences to their probabilities
     */
    public Map<Sentence, Double> likelihoodBackward(List<Sentence> sentences) {
        HashMap<Sentence, Double> ret = new HashMap<Sentence, Double>();

        for (Sentence sentence : sentences) {
            double probability = likelihoodBackward(sentence);
            ret.put(sentence, probability);
        }

        return ret;
    }

    /**
     * Computes the likelihood (based on backward algorithm) of a structure of
     * sentences (ignoring their built-in tags) and returns them and their
     * probabilities in a map (key: sentence, value: likelihood)
     * (from 0.0 to 1.0)
     * @param sentences The structure of sentences
     * @return The map of sentences to their probabilities
     */
    public Map<Sentence, Double> likelihoodBackward(ObservationSequenceStructure set) {
        return likelihoodBackward(set.getSequences());
    }

    /**
     * Computes the likelihood (based on backward algorithm) of a sentence,
     * ignoring its built-in tags, and returns the likelihood of that sentence
     * under this HMM.
     * (from 0.0 to 1.0)
     * @param sentence The sentence to analyze.
     * @return The likelihood of the sentence
     */
    public double likelihoodBackward(Sentence sentence) {
        double[][] backwardMatrix = new double[numStates()][sentence.length()];
        String[] stateArray = states.toArray(new String[states.size()]);

        //Initialization, the probability that the last state can be an end state
        for (int i = 0; i < states.size(); i++) {
            backwardMatrix[i][sentence.length() - 1] = transitions.probability(stateArray[i], null);
        }

        //Rest of sentence
        for (int t = sentence.length() - 2; t >= 0; t--) {
            for (int i = 0; i < states.size(); i++) {
                double probability = 0;
                for (int j = 0; j < states.size(); j++) {
                    probability += backwardMatrix[j][t + 1] * transitions.probability(stateArray[i], stateArray[j]) * likelihoods.probability(stateArray[j], sentence.get(t + 1).getValue());
                }
                backwardMatrix[i][t] = probability;
            }
        }

        // Overall probability and first word
        double probability = 0;
        for (int i = 0; i < states.size(); i++) {
            probability += backwardMatrix[i][0] * transitions.probability(null, stateArray[i]) * likelihoods.probability(stateArray[i], sentence.get(0).getValue());
        }
        return probability;
    }

    /**
     * Computes the general likelihood of a list of sentences, taking into
     * consideration BOTH their words AND tags.
     * (Likelihoods can be between 0.0 to 1.0)
     * @param sentences The list of sentences to analyze
     * @return The map of the sentence to their likelihood
     */
    public Map<Sentence, Double> likelihood(List<Sentence> sentences) {
        HashMap<Sentence, Double> ret = new HashMap<Sentence, Double>();

        for (Sentence sentence : sentences) {
            double probability = likelihood(sentence);
            ret.put(sentence, probability);
        }

        return ret;
    }

    /**
     * Computes the general likelihood of a set of sentences, taking into
     * consideration BOTH their words AND tags.
     * (Likelihoods can be between 0.0 to 1.0)
     * @param set The structure holding the sentences
     * @return The map of the sentence to their likelihood
     */
    public Map<Sentence, Double> likelihood(ObservationSequenceStructure set) {
        return likelihood(set.getSequences());
    }

    /**
     * Computes the general likelihood of a sentence, taking into consideration
     * both the tags and words. This will be smaller than the forward or
     * backward likelihoods (which take into consideration all possible tags,
     * rather than the ones cooked into the sentence).
     * @param sentence The sentence to exam.
     * @return The likelihood of the sentence in this HMM.
     */
    public double likelihood(Sentence sentence) {
        double probability = 1;
        for (int i = 0; i <= sentence.length(); i++) {
            if (i == 0) {
                probability *= transitions.probability(null, sentence.get(i).getState());
            } else if (i == sentence.length()) {
                probability *= transitions.probability(sentence.get(i - 1).getState(), null);
            } else {
                probability *= transitions.probability(sentence.get(i - 1).getState(), sentence.get(i).getState());
            }

            if (i != sentence.length()) {
                probability *= likelihoods.probability(sentence.get(i).getState(), sentence.get(i).getValue());
            }
        }
        return probability;
    }

    /**
     * Predicts the POS Tag Sequence for a given sentence using the Viterbi
     * algorithm
     * Obviously this will ignore the 'tag' field of the words within the
     * sentence.
     *
     * @param sentence The Sentence to decode for POS Tags
     * @return A new sentence with the words having the predicted POS Tags.
     */
    public Sentence predictTags(Sentence sentence) {
        double[][] forwardMatrix = new double[numStates()][sentence.length()];
        int[][] backpointer = new int[numStates()][sentence.length()];
        String[] stateArray = states.toArray(new String[states.size()]);

        // Pretty much the forward algorithm with the backpointer and using
        // max rather than sum
        for (int i = 0; i < states.size(); i++) {
            String state = stateArray[i];
            forwardMatrix[i][0] = transitions.probability(null, state) * likelihoods.probability(state, sentence.get(0).getValue());
            backpointer[i][0] = 0;
        }

        for (int t = 1; t < sentence.length(); t++) {
            for (int j = 0; j < states.size(); j++) {
                double maxLikelihood = -1;
                int bestState = -1;
                for (int i = 0; i < states.size(); i++) {
                    double likelihood = forwardMatrix[i][t - 1] * transitions.probability(stateArray[i], stateArray[j]) * likelihoods.probability(stateArray[j], sentence.get(t).getValue());
                    if (likelihood > maxLikelihood) {
                        maxLikelihood = likelihood;
                        bestState = i;
                    }
                }
                forwardMatrix[j][t] = maxLikelihood * likelihoods.probability(stateArray[j], sentence.get(t).getValue());
                backpointer[j][t] = bestState;
            }
        }

        double maxLikelihood = -1;
        int bestState = -1;
        for (int s = 0; s < states.size(); s++) {
            double stateProb = forwardMatrix[s][sentence.length() - 1] * transitions.probability(stateArray[s], null);
            if (stateProb > maxLikelihood) {
                maxLikelihood = stateProb;
                bestState = s;
            }
        }

        // Back-trace and get the tag-sequence
        String[] tags = new String[sentence.length()];
        tags[sentence.length() - 1] = stateArray[bestState];
        int lastState = bestState;
        for (int t = sentence.length() - 2; t >= 0; t--) {
            String state = stateArray[backpointer[lastState][t + 1]];
            tags[t] = state;
            lastState = backpointer[lastState][t + 1];
        }

        // Shove the new tags into a new sentence with the old words
        Sentence ret = new Sentence();
        for (int i = 0; i < tags.length; i++) {
            Word word = new Word(sentence.get(i).getValue(), tags[i]);
            ret.addWord(word);
        }

        return ret;
    }

    /**
     * Predicts the POS-Tag sequence for a list of sentences.
     * It will return them in a Map, matching the supplied sentence (key) to
     * to the sentence with the predicted tags (value)
     * @param sentences The list of sentences to examine.
     * @return Map of old sentence to sentence with tags
     */
    public Map<Sentence, Sentence> predictTags(List<Sentence> sentences) {
        HashMap<Sentence, Sentence> ret = new HashMap<Sentence, Sentence>();

        for (Sentence sentence : sentences) {
            Sentence newSentence = predictTags(sentence);
            ret.put(sentence, newSentence);
        }

        return ret;
    }

    /**
     * Predicts the POS-Tag sequence for a set of sentences.
     * It will return them in a Map, matching the supplied sentence (key) to
     * to the sentence with the predicted tags (value)
     * @param set The structure holding the sentences to examine
     * @return Map of old sentence to sentence with tags
     */
    public Map<Sentence, Sentence> predictTags(ObservationSequenceStructure set) {
        return predictTags(set.getSequences());
    }

    /**
     * Returns the number of states currently embodied in this HMM.
     * @return The number of states.
     */
    private int numStates() {
        return states.size();
    }

    /**
     * Writes this HMM to the parameterized Writer.
     * Mainly passes it to the ObsersationLikelihoods and TagTransitions for
     * them to do their stuff.
     * @param writer The writer to write on
     * @throws IOException
     */
    public void save(BufferedWriter writer) throws IOException {
        transitions.save(writer);
        writer.newLine();
        writer.flush();
        likelihoods.save(writer);
    }

    /**
     * Trains based on the passed in set of sequences, but ignores their built
     * in tags or states. Instead, the HMM attempts to build its own states
     * to suit the sets. You can also specify a maximum number of states
     * to prevent it from building a ton of states.
     * @param set The set of sentences to train from
     * @param maxStates The maximum number of states the HMM should have.
     */
    public void unsupervisedTrain(ObservationSequenceStructure set, int maxStates) {
        List<ObservationSequence> sequences = set.getSequences();
        for (ObservationSequence sequence : sequences) {
            int size = sequence.length();
            String prevState = null;
            for (int i = 0; i < size; i++) {
                Observation currO = sequence.get(i);
                String state = null;
                
                // First check if there is already a state that the word
                // can have
                if (likelihoods.hasState(currO.getValue())) {
                    state = likelihoods.getState(currO.getValue());
                } else {
                    state = this.createState(maxStates);
                }

                if (i > 0) {
                    transitions.unsupervisedAdd(prevState, state);
                } else {
                    transitions.unsupervisedStartState(state);
                }

                if (i == size - 1) {
                    transitions.unsupervisedEndState(state);
                }

                addToStateList(state);
                likelihoods.unsupervisedAdd(currO, state);
                prevState = state;
            }
        }
    }

    /**
     * "Creates" a state. It will return an old state or create a new state
     * depending on what it thinks it should do.
     * @param maxStates The maximum number of states the HMM should have
     * @return The state that the word should get
     */
    private String createState(int maxStates) {
        int currSize = states.size();

        if(currSize == maxStates) {
            return getOldState();
        } else {
            return createNewState();
        }
    }

    /**
     * Returns an already existing state.
     * Current implementation... returns.. a random state.
     * @return An old state that the word should get.
     */
    private String getOldState() {
        Random rand = new Random();
        String[] stateArray = states.toArray(new String[]{});
        int index = rand.nextInt(stateArray.length);
        return stateArray[index];
    }

    /**
     * Creates brand new state!
     * @return A brand new state.
     */
    private String createNewState() {
        return "s" + states.size();
    }
}

package hmmpostagging.hmm;

import hmmpostagging.hmm.structures.Observation;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * An interface for the HMM, representing the structure holding the likelihoods
 * that a State can go to a given Observation.
 * 
 * @author Taylor
 */
public interface ObsersationLikelihoods<E extends Observation> {
    /**
     * Adds a word to the likelihoods. Should use the word's built-in tag and word
     * to add it.
     * @param currWord The word to analyze and add
     */
    public void add(E currWord);

    /**
     * Returns the most likely word for a given tag.
     * @param tag The state to check
     * @return The most likely word from that state
     */
    public String getMostLikelyWord(String tag);

    /**
     * Returns a random word word for a given tag.
     * @param tag The state to check
     * @return A random word from that state
     */
    public String getRandomWord(String tag);

    /**
     * The probability that the state will go to a given word.
     * @param tag The state to check
     * @param word The word to check for.
     * @return The probability that, given the tag, you will get the word
     */
    public double probability(String tag, String word);

    /**
     * Writes this likelihood structure to the passed-in Writer.
     * @param writer Where to write the structure
     * @throws IOException
     */
    public void save(BufferedWriter writer) throws IOException;

    /**
     * Adds the word to the likelihoods, but uses the possibleState parameter
     * rather than word's built-in tag
     * @param currO The word to add
     * @param possibleState The state to associate that word with
     */
    public void unsupervisedAdd(E currO, String possibleState);

    /**
     * Whether or not the passed in word already is associated with a state
     * or not.
     * @param value The word to check for
     * @return true if the word already has a state, false if not
     */
    public boolean hasState(String value);

    /**
     * Returns the state that the word is associated with.
     * Will return the first encounter's state with the word if there are
     * multiple states with the word
     * @param value The word to look for
     * @return The state associated with that word
     */
    public String getState(String value);
}

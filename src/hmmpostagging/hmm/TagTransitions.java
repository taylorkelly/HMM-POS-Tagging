package hmmpostagging.hmm;

import hmmpostagging.hmm.structures.Observation;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author taylor
 */
public interface TagTransitions<E extends Observation> {

    public void add(E lastWord, E currWord);

    public void startState(E currWord);

    public void endState(E currWord);

    public String getMostLikelyTag(String fromTag);

    public String getRandomTag(String fromTag);

    public double probability(String fromTag, String toTag);

    public void save(BufferedWriter writer) throws IOException;
}

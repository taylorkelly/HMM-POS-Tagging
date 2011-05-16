package hmmpostagging.hmm;

import hmmpostagging.hmm.structures.Observation;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author taylor
 */
public interface ObsersationLikelihoods<E extends Observation> {
    public void add(E currWord);

    public String getMostLikelyWord(String tag);

    public String getRandomWord(String tag);

    public double probability(String tag, String word);

    public void save(BufferedWriter writer) throws IOException;
}

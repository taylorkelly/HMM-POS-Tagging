package hmmpostagging.hmm.structures;

import java.util.List;

/**
 * A super-general structure.
 * It only has to hold a bunch of ObservationSequences.
 * (In this case it's DataSets holding Sentences)
 * @author Taylor
 * @param <E> The type of ObservationSequence that the structure holds
 */
public interface ObservationSequenceStructure<E extends ObservationSequence> {
    public List<E> getSequences();
}

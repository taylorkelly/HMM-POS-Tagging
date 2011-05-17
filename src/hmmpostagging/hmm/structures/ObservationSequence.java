package hmmpostagging.hmm.structures;

/**
 * Represents a general sequence of observations that will be shoved into the
 * HMM. In this project's case, that is a Sentence.
 * @author Taylor
 * @param <E> The type of Observation that the sequence will hold.
 */
public interface ObservationSequence<E extends Observation> {

    public int length();
    public E get(int index);
    
}

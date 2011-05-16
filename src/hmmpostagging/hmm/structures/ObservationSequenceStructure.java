/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmmpostagging.hmm.structures;

import java.util.List;

/**
 *
 * @author taylor
 */
public interface ObservationSequenceStructure<E extends ObservationSequence> {
    public List<E> getSequences();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmmpostagging.hmm.structures;

/**
 *
 * @author taylor
 */
public interface ObservationSequence<E extends Observation> {

    public int length();
    public E get(int index);
    
}

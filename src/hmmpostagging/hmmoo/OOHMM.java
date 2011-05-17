package hmmpostagging.hmmoo;

import hmmpostagging.hmm.HMM;

/**
 * An implementation of the HMM using Objects (HashMaps) rather than tables
 * to represent the internals of the HMM.
 * @author Taylor
 */
public class OOHMM extends HMM {
    public OOHMM() {
        likelihoods = new OOObservationLikelihoods();
        transitions = new OOTagTransitions();
    }

    /**
     * Allows you to initialize the OOHMM with ready-to-go internals
     * @param trans The TagTransitions to use
     * @param likeli The ObservationLikelihoods to use
     */
    public OOHMM(OOTagTransitions trans, OOObservationLikelihoods likeli) {
        likelihoods = likeli;
        transitions = trans;
        this.states = likeli.getStates(false);
    }
}

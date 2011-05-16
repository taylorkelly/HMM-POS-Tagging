package hmmpostagging.hmmoo;

import hmmpostagging.hmm.HMM;
import hmmpostagging.hmm.ObsersationLikelihoods;
import hmmpostagging.hmm.TagTransitions;
import java.util.ArrayList;

/**
 *
 * @author taylor
 */
public class OOHMM extends HMM {
    public OOHMM() {
        likelihoods = new OOObservationLikelihoods();
        transitions = new OOTagTransitions();
    }
}

package hmmpostagging.hmm.structures;

/**
 * A general structure for the HMM representing an 'observation', which has
 * a state and value. The value can be null or ignored for testing or
 * unsupervised training
 * 
 * @author Taylor
 */
public interface Observation {

    public String getState();
    public String getValue();
    
}

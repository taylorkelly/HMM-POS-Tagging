package hmmpostagging;

import java.io.File;

/**
 * The DataSet used for Training. Clearly does not need its own class at this
 * point, but in case it needs it for the future...
 * 
 * @author Taylor
 */
public class TrainingSet extends DataSet {
    public TrainingSet(File file) {
        super(file);
    }
}

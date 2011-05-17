package hmmpostagging;

import java.io.File;
import java.io.FilenameFilter;

/**
 * FilenameFilter to only get POS files that are for train.
 * 
 * @author Taylor
 */
public class TrainFilter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        if (name.startsWith("train_") && name.endsWith(".pos")) {
            return true;
        } else {
            return false;
        }
    }
}

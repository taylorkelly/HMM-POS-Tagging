package hmmpostagging;

import java.io.File;
import java.io.FilenameFilter;

/**
 * FilenameFilter to only get POS files that are for testing
 * @author taylor
 */
public class TestFilter implements FilenameFilter {

    @Override
    public boolean accept(File file, String name) {
        if (name.startsWith("test_") && name.endsWith(".pos")) {
            return true;
        } else {
            return false;
        }
    }
}

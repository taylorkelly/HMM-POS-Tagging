
package hmmpostagging.io;

import hmmpostagging.Main;
import hmmpostagging.hmm.HMM;
import hmmpostagging.hmmoo.OOHMM;
import hmmpostagging.hmmoo.OOObservationLikelihoods;
import hmmpostagging.hmmoo.OOTagTransitions;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Loads HMMs from saved files. Uses the OOHMM implementation. That's it.
 * @see hmmpostagging.io.Saver
 * @author Taylor
 */
public class Loader {
    /**
     * Pretty much just checks if a file exists
     * @param fileName The filename to check
     * @return true if exists, false if not
     */
    public static boolean saveExists(String fileName) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        return file.exists();
    }

    /**
     * Loads the HMM from a given file. Returns null if the file can't be found
     * @param fileName The file to load the HMM from
     * @return The loaded HMM!
     */
    public static HMM load(String fileName) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        try {
            Scanner sc = new Scanner(file);
            String tagTransitions = sc.nextLine();
            String observationLikelihoods = sc.nextLine();

            OOTagTransitions trans = new OOTagTransitions(tagTransitions);
            OOObservationLikelihoods likeli = new OOObservationLikelihoods(observationLikelihoods);
            return new OOHMM(trans, likeli);
        } catch (FileNotFoundException ex) {
            System.out.println("Save doesn't exist to load");
            return null;
        }
    }
}

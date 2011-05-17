package hmmpostagging;

import hmmpostagging.scoring.Scorable;
import hmmpostagging.scoring.Scorer;
import hmmpostagging.hmm.HMM;
import hmmpostagging.hmmoo.OOHMM;
import hmmpostagging.hmmoo.OOObservationLikelihoods;
import hmmpostagging.io.Loader;
import hmmpostagging.io.Saver;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The guy running the show. The head-honcho.
 * This will be creating the HMMs for both parts, as well as running
 * the required tests. It currently expects the .pos files to be in a folder
 * Project2_Data located in the same directory.
 *
 * It will report its status to the command line, as well as saves the results
 * to a Results folder inside the Project2_Data folder.
 * 
 * @author Taylor
 */
public class Main {
    public static final String FOLDER = "Project2_Data";
    public static final String RESULTS_FOLDER = "Results";

    public static void main(String[] args) {
        // TODO GUI?

        File file = new File(FOLDER);
        if (!file.exists()) {
            System.out.println("Cannot find Project2_Data folder!");
            return;
        } else {
            File results = new File(FOLDER, RESULTS_FOLDER);
            results.mkdir();
        }
        HMM model = new OOHMM();

        System.out.println("Part 1");
        // Check if a previous hmm snapshot exists
        // if so, load it, if not, try to build one.
        if (Loader.saveExists("hmm_part1.save")) {
            System.out.println(" - Past trained HMM found, loading...");
            model = Loader.load("hmm_part1.save");
            System.out.println("   HMM loaded.");
        } else {
            // Supervised training of the HMM
            System.out.println(" - No saved HMM found. Training with train files...");
            File[] training = file.listFiles(new TrainFilter());
            for (File trainFile : training) {
                TrainingSet set = new TrainingSet(trainFile);
                model.train(set);
            }
            System.out.println("   Successfully trained. Saving HMM...");
            Saver.save("hmm_part1.save", model);
            System.out.println("   HMM saved.");
        }

        File[] testing = file.listFiles(new TestFilter());
        TestSet[] testSets = new TestSet[testing.length];
        System.out.println(" - Loading Test Files...");
        for (int i = 0; i < testing.length; i++) {
            File testFile = testing[i];
            testSets[i] = new TestSet(testFile);
        }

        // Prediction
        // Likelihood estimation using Forward-algorithm (or backward)
        System.out.println(" - Running Prediction Tests...");
        for (int i = 0; i < testing.length; i++) {
            Map<Sentence, Double> likelihoods = model.likelihoodForward(testSets[i]);
            Saver.store("pred_" + testing[i].getName(), likelihoods);
            Saver.predictionResults("results_pred_" + testing[i].getName() + ".txt", likelihoods);
        }

        // Decoding
        // POS-Tag prediction using Viterbi
        System.out.println(" - Running Decoding Tests...");
        for (int i = 0; i < testing.length; i++) {
            TestSet set = testSets[i];
            Map<Sentence, Sentence> predictedTags = model.predictTags(set);
            Map<Entry<? extends Scorable, ? extends Scorable>, Double> scores = Scorer.score(predictedTags);
            Saver.store("decode_" + testing[i].getName(), scores);
            Saver.decodingResults("results_decode_" + testing[i].getName() + ".txt", scores);
        }

        System.out.println(" - Done!");


        System.out.println("Part 2");
        model = new OOHMM();

        System.out.println(" - Unsupervised training with train files...");
        File[] training = file.listFiles(new TrainFilter());
        for (File trainFile : training) {
            TrainingSet set = new TrainingSet(trainFile);
            model.unsupervisedTrain(set, 10);
        }
        System.out.println("   Completed Training.");
        System.out.println(" - Saving HMM...");
        Saver.save("hmm_part2.save", model);
        System.out.println("   HMM saved.");

        System.out.println(" - Running Decoding Tests...");
        for (int i = 0; i < testing.length; i++) {
            TestSet set = testSets[i];
            Map<Sentence, Sentence> predictedTags = model.predictTags(set);
            Saver.storeValues("decode2_" + testing[i].getName(), predictedTags);
        }
        System.out.println(" - Done!");

    }
}

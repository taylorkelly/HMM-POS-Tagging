package hmmpostagging;

import hmmpostagging.scoring.Scorable;
import hmmpostagging.scoring.Scorer;
import hmmpostagging.hmm.HMM;
import hmmpostagging.hmmoo.OOHMM;
import hmmposttagging.output.Saver;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
    public static final String FOLDER = "Project2_Data";

    public static void main(String[] args) {
        // TODO GUI Folder launcher?

        File file = new File(FOLDER);
        HMM model = new OOHMM();

        // Supervised training of the HMM
        File[] training = file.listFiles(new TrainFilter());
        for (File trainFile : training) {
            TrainingSet set = new TrainingSet(trainFile);
            model.train(set);
        }
        Saver.save(model);

        // Prediction
        // Likelihood estimation using Forward-algorithm (or backward)
        File[] testing = file.listFiles(new TestFilter());
        TestSet[] testSets = new TestSet[testing.length];
        for (int i = 0; i < testing.length; i++) {
            File testFile = testing[i];
            testSets[i] = new TestSet(testFile);
            Map<Sentence, Double> likelihoods = model.likelihoodForward(testSets[i]);
            Saver.store("pred_" + testing[i].getName(), likelihoods);
            //TODO better saves
        }

        // Decoding
        // POS-Tag prediction using Viterbi
        for (int i = 0; i < testing.length; i++) {
            TestSet set = testSets[i];
            Map<Sentence, Sentence> predictedTags = model.predictTags(set);
            Map<Entry<? extends Scorable, ? extends Scorable>, Double> scores = Scorer.score(predictedTags);
            Saver.store("decode_" + testing[i].getName(), scores);
            //TODO better saves
        }

        Sentence sentence = new Sentence();
        sentence.addWord(new Word("he", "prp"));
        sentence.addWord(new Word("was", "vbd"));
        sentence.addWord(new Word("the", "dt"));
        sentence.addWord(new Word("man", "nn"));
        sentence.addWord(new Word(".", "."));

        System.out.println(model.predictTags(sentence).toString(false));
        System.out.println(sentence.toString(false));
        System.out.println(sentence.toString(true));


        //TODO unsupervised learning - fml

        // TODO
        // Decoding
        // POS-like state prediction

    }
}

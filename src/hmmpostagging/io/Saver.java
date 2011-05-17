package hmmpostagging.io;

import hmmpostagging.Main;
import hmmpostagging.Sentence;
import hmmpostagging.hmm.HMM;
import hmmpostagging.scoring.Scorable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Has various Saving methods to save Maps, Results, as well as HMMs.
 * @author Taylor
 */
public class Saver {
    /**
     * Saves an HMM to a specified filename.
     * @see hmmpostagging.io.Loader
     * @see hmmpostagging.hmm.HMM.save()
     * @param fileName The file to save to
     * @param hmm The HMM to save.
     */
    public static void save(String fileName, HMM hmm) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            hmm.save(bwriter);
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (hmm.save)");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (hmm.save)");
            }
        }
    }

    /**
     * Saves likelihood results to a file from Predictions
     * @param fileName The file to save to
     * @param likelihoods The map of sentence to likelihood.
     */
    public static void predictionResults(String fileName, Map<Sentence, Double> likelihoods) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            double sum = 0;
            int count = 0;
            double min = Double.MAX_VALUE;
            Sentence minSentence = null;
            double max = Double.MIN_VALUE;
            Sentence maxSentence = null;
            for (Entry<Sentence, Double> entry : likelihoods.entrySet()) {
                Sentence sentence = entry.getKey();
                double likelihood = entry.getValue();

                sum += likelihood;
                count++;

                if (likelihood > max) {
                    maxSentence = sentence;
                    max = likelihood;
                }
                if (likelihood < min) {
                    minSentence = sentence;
                    min = likelihood;
                }
            }
            double average = sum / count;

            bwriter.write("Total sentences: ");
            bwriter.write(Integer.toString(count));
            bwriter.newLine();
            bwriter.write("Average likelihood: ");
            bwriter.write(Double.toString(average));
            bwriter.newLine();
            bwriter.write("Max likelihood: ");
            bwriter.write(Double.toString(max));
            bwriter.newLine();
            bwriter.write("Max likelihood sentence: ");
            bwriter.write(maxSentence.toString(true));
            bwriter.newLine();
            bwriter.write("Min likelihood: ");
            bwriter.write(Double.toString(min));
            bwriter.newLine();
            bwriter.write("Min likelihood sentence: ");
            bwriter.write(minSentence.toString(true));
            bwriter.newLine();
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (" + fileName + ")");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (" + fileName + ")");
            }
        }
    }

    /**
     * Saves decoding score results to a file from Decoding
     * @param fileName The file to save to
     * @param likelihoods The map of sentences to score
     */
    public static void decodingResults(String fileName, Map<Entry<? extends Scorable, ? extends Scorable>, Double> likelihoods) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            double sum = 0;
            int count = 0;
            int oneCount = 0;
            double min = Double.MAX_VALUE;
            Sentence minSentence = null;
            double max = Double.MIN_VALUE;
            Sentence maxSentence = null;
            for (Entry<Entry<? extends Scorable, ? extends Scorable>, Double> entry :
                    likelihoods.entrySet()) {
                Sentence sentence = (Sentence) entry.getKey().getKey();
                double likelihood = entry.getValue();

                sum += likelihood;
                count++;

                if (likelihood == 1.0) {
                    oneCount++;
                }
                if (likelihood > max) {
                    maxSentence = sentence;
                    max = likelihood;
                }
                if (likelihood < min) {
                    minSentence = sentence;
                    min = likelihood;
                }
            }
            double average = sum / count;

            bwriter.write("Total sentences: ");
            bwriter.write(Integer.toString(count));
            bwriter.newLine();
            bwriter.write("Average score: ");
            bwriter.write(Double.toString(average));
            bwriter.newLine();
            bwriter.write("Number of 1.0s: ");
            bwriter.write(Integer.toString(oneCount));
            bwriter.newLine();
            if (maxSentence != null) {
                bwriter.write("Max score: ");
                bwriter.write(Double.toString(max));
                bwriter.newLine();
                bwriter.write("Max score sentence: ");
                bwriter.write(maxSentence.toString(true));
                bwriter.newLine();
            }
            if (minSentence != null) {
                bwriter.write("Min score: ");
                bwriter.write(Double.toString(min));
                bwriter.newLine();
                bwriter.write("Min score sentence: ");
                bwriter.write(minSentence.toString(true));
            }
            bwriter.newLine();
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (" + fileName + ")");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (" + fileName + ")");
            }
        }
    }

    /**
     * Stores a Generic Map to a given filename.
     * It's saved in the format "value - key" followed by new lines.
     * In the case that the key is a map as well, it will save it as
     * "value - keykey: keyvalue"
     * @param fileName The filename to save to
     * @param map The map to save
     */
    public static void store(String fileName, Map<?, ?> map) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            for (Entry entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                bwriter.write(value.toString());
                bwriter.write(" - ");
                if (key instanceof Entry) {
                    bwriter.write(((Entry) key).getKey().toString());
                    bwriter.write(": ");
                    bwriter.write(((Entry) key).getValue().toString());
                } else {
                    bwriter.write(key.toString());

                }
                bwriter.newLine();
            }
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (" + fileName + ")");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (" + fileName + ")");
            }
        }
    }

    /**
     * A generic method, saving only the values of a map to the file
     * @param fileName The file to save to
     * @param map The map of whose values to save
     */
    public static void storeValues(String fileName, Map<?, ?> map) {
        File file = new File(new File(Main.FOLDER, Main.RESULTS_FOLDER), fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            for (Entry entry : map.entrySet()) {
                Object value = entry.getValue();
                bwriter.write(value.toString());
                bwriter.newLine();
            }
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (" + fileName + ")");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (" + fileName + ")");
            }
        }
    }
}

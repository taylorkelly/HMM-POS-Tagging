package hmmpostagging;

import hmmpostagging.hmm.structures.ObservationSequenceStructure;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This can read and will be the internal representation of the .pos files
 * It can parse through, forming Sentence objects based on the words and their
 * part-of-speech tags.
 * It then acts as a information holder for the list of sentences.
 * 
 * @author Taylor
 */
public abstract class DataSet implements ObservationSequenceStructure<Sentence> {
    public static final String COMMENT_INDIC = "*x*";
    public static final String NEW_PARA = "===";
    public static final String NOUN_PHRASE_INDIC = "[";
    protected List<Sentence> sentences;

    /**
     * Creates a new DataSet based around the .pos file.
     * @param trainFile The .pos file to analyze Sentences from.
     */
    public DataSet(File trainFile) {
        sentences = new ArrayList<Sentence>();
        Scanner sc;
        try {
            sc = new Scanner(trainFile);
            Sentence currSentence = new Sentence();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.isEmpty()) continue;
                if (line.startsWith(COMMENT_INDIC)) continue;
                if (line.startsWith(NEW_PARA)) continue;

                if (line.startsWith(NOUN_PHRASE_INDIC))
                    line = removeNPAnnotations(line);

                Scanner lineScan = new Scanner(line);

                while (lineScan.hasNext()) {
                    Word word = Word.process(lineScan.next());
                    if (word != null) {
                        if (currSentence.isComplete() && !word.isSentenceTerminator()) {
                            sentences.add(currSentence);
                            currSentence = new Sentence();
                        }
                        currSentence.addWord(word);
                    }
                }
            }
            if (currSentence.isComplete()) sentences.add(currSentence);

        } catch (FileNotFoundException ex) {
            //TODO Better error-ing?
            System.out.println("File not found");
        }
    }

    /**
     * Removes the NP notations ([]) from the line
     * @param line The original line
     * @return The line with NP notations removed
     */
    private static String removeNPAnnotations(String line) {
        return line.substring(1, line.length() - 1).trim();
    }

    /**
     * @see hmmpostagging.hmm.structures.ObservationSequenceStructure
     */
    public List<Sentence> getSequences() {
        return sentences;
    }
}

package hmmpostagging;

import hmmpostagging.hmm.structures.ObservationSequence;
import hmmpostagging.scoring.Scorable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author taylor
 */
public class Sentence implements Iterable<Word>, ObservationSequence<Word>, Scorable<Sentence> {
    private LinkedList<Word> words;
    private Word lastWord;

    public Sentence() {
        words = new LinkedList<Word>();
        lastWord = null;
    }

    public void addWord(Word word) {
        words.add(word);
        lastWord = word;
    }

    public Word getLastWord() {
        return lastWord;
    }

    public boolean isComplete() {
        return lastWord != null && lastWord.isSentenceTerminator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Word word: words) {
            builder.append(word.toString(true));
            builder.append('/');
            builder.append(word.toString(false));
            builder.append(' ');
        }

        return builder.toString();

        //return toString(true);
    }

    public String toString(boolean useWords) {
        StringBuilder builder = new StringBuilder();

        for (Word word : words) {
            builder.append(word.toString(useWords));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public Iterator<Word> iterator() {
        return words.iterator();
    }

    public int length() {
        return words.size();
    }

    public Word get(int index) {
        if (index < 0 || index >= this.length()) {
            return null;
        }
        return words.get(index);
    }

    public int numCorrectComparedTo(Sentence other) {
        if (this.length() != other.length()) {
            return 0;
        }

        int total = 0;
        for (int i = 0; i < this.length(); i++) {
            if (this.get(i).equals(other.get(i))) total++;
        }
        return total;
    }

    public int numTotalComparedTo(Sentence other) {
        if (this.length() != other.length()) {
            return 0;
        } else {
            return this.length();
        }
    }
}

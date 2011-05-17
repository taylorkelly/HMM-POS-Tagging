package hmmpostagging;

import hmmpostagging.hmm.structures.ObservationSequence;
import hmmpostagging.scoring.Scorable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This is a data structure (with an internal List) representing a Sentence.
 * It can hold a list of Words, and that (as well as functions on those words)
 * is its main purpose.
 * It is also able to be scored against other sentences (so as to compare
 * derived sentences against gold).
 *
 * @author Taylor
 */
public class Sentence implements Iterable<Word>, ObservationSequence<Word>, Scorable<Sentence> {
    private LinkedList<Word> words;
    private Word lastWord;

    /**
     * Creates a new, blank sentence
     */
    public Sentence() {
        words = new LinkedList<Word>();
        lastWord = null;
    }

    /**
     * Adds a word to the sentence.
     * @param word The word to add
     */
    public void addWord(Word word) {
        words.add(word);
        lastWord = word;
    }

    /**
     * Checks whether the sentence is "complete".
     * The criteria for "completeness" is:
     *  - A non-empty sentence
     *  - The last word is a "sentence terminator"
     * @return true if complete, false if not
     */
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

    /**
     * A secondary toString, mainly for debugging and quick-checking use.
     * Will return a string with the sentence only using words, or only using
     * tags.
     * @param useWords true if using words, false if using tags
     * @return The sentence in specified format
     */
    public String toString(boolean useWords) {
        StringBuilder builder = new StringBuilder();

        for (Word word : words) {
            builder.append(word.toString(useWords));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Returns the iterator (for foreach loops)
     * @return The Word iterator
     */
    public Iterator<Word> iterator() {
        return words.iterator();
    }

    /**
     * The number of words currently in the sentence
     * @return the length of the sentence
     */
    public int length() {
        return words.size();
    }

    /**
     * Returns the specified Word from the sentence (based on index)
     * Fights IndexOutOfBoundExceptions by giving you nulls. Muahaha.
     * @param index The index of the word in the sentence to grab
     * @return The specified word.
     */
    public Word get(int index) {
        if (index < 0 || index >= this.length()) {
            return null;
        }
        return words.get(index);
    }

    /**
     * Compares this instance of a sentence with the one passed as a parameter.
     * If the lengths of the two sentences are different, then there are no
     * correct.
     * If they're the same, it compares each word in the sentence.
     * If both words' 'value' and 'tag' match, then that counts as a 'correct'.
     * @param other The sentence to compare to
     * @return The number of similar words
     */
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

    /**
     * Compares this instance of a sentence with the one passed as a parameter.
     * If the lengths of the two sentences are different, then the total is 0.
     * If they're the same, the total is the length of the sentences.
     * @param other The sentence to compare to
     * @return The length of both sentences, 0 if different lengths
     */
    public int numTotalComparedTo(Sentence other) {
        if (this.length() != other.length()) {
            return 0;
        } else {
            return this.length();
        }
    }
}

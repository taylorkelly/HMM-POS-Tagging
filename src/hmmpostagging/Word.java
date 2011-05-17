package hmmpostagging;

import hmmpostagging.hmm.structures.Observation;

/**
 * The structure representing a Word. It can hold its value (aka word) and the
 * state (aka tag or POS-tag).
 *
 * @author Taylor
 */
public class Word implements Observation {
    public static final String WORD_TAG_SEP = "/";
    private String word;
    private String tag;

    /**
     * Create a Word with the specified word and tag.
     * In this project's case, they should both always be lowercase.
     * This is because the HMM is case-sensitive, but I don't want it to be.
     *
     * @param word The value of the word
     * @param tag The POS tag
     */
    public Word(String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    /**
     * Creates a word based on the format in the .pos files (word/tag).
     * @param next The String holding the word and tag
     * @return The Word representing this tagged-word.
     */
    public static Word process(String next) {
        next = next.trim();
        String[] wordTagPair = next.split(WORD_TAG_SEP);
        if (wordTagPair.length != 2) {
            return null;
        }
        String word = wordTagPair[0].toLowerCase();
        String tag = wordTagPair[1].toLowerCase();
        if (tag.contains("|")) tag = tag.substring(tag.lastIndexOf("|") + 1);
        return new Word(word, tag);

    }

    /**
     * Checks if this word is used to terminate sentence (aka a period, question
     * mark, etc). Bases it on the word's tag (looking for '.')
     * @return true if it is, false if not
     */
    public boolean isSentenceTerminator() {
        return tag.equals(".");
    }

    /**
     * Returns the value or word
     * @return the internal word
     */
    public String getValue() {
        return word;
    }

    /**
     * Returns the state or tag
     * @return the internal tag
     */
    public String getState() {
        return tag;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * A secondary toString, as to whether it should return the word or tag
     * @param useWord if true, returns the word, false returns the tag
     * @return The specified part of the word
     */
    public String toString(boolean useWord) {
        if (useWord) {
            return word;
        } else {
            return tag;
        }
    }

    /**
     * Checks for comparison based on the tag and word.
     * If the other Object is not a Word, it is instantly not equal.
     * If it is and the other tag and word are equal to the instance's,
     * it is equal; otherwise no.
     * @param o The Object to compare to
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Word) {
            Word otherWord = (Word) o;
            return otherWord.tag.equals(this.tag) && otherWord.word.equals(this.word);
        } else {
            return false;
        }
    }
}

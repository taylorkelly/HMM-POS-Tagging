package hmmpostagging;

import hmmpostagging.hmm.structures.Observation;

/**
 *
 * @author taylor
 */
public class Word implements Observation {
    public static final String WORD_TAG_SEP = "/";
    private String word;
    private String tag;

    public Word(String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    public static Word process(String next) {
        next = next.trim();
        String[] wordTagPair = next.split(WORD_TAG_SEP);
        if (wordTagPair.length != 2) {
            return null;
        }
        String word = wordTagPair[0].toLowerCase();
        String tag = wordTagPair[1].toLowerCase();
        if(tag.contains("|")) tag = tag.substring(tag.lastIndexOf("|")+1);
        return new Word(word, tag);

    }

    public boolean isSentenceTerminator() {
        return tag.equals(".");
    }

    public String getWord() {
        return word;
    }

    public String getState() {
        return tag;
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean useWord) {
        if (useWord) {
            return word;
        } else {
            return tag;
        }
    }

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

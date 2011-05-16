package hmmpostagging.scoring;

public interface Scorable<E extends Scorable> {

    public int numCorrectComparedTo(E other);
    public int numTotalComparedTo(E other);

}

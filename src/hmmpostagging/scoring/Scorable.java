package hmmpostagging.scoring;

/**
 * An interface representing a 'Scorable' object. It can be compared against
 * other scorables to
 * @author taylor
 * @param <E>
 */
public interface Scorable<E extends Scorable> {

    /**
     * The number of 'correct points' that the two scorables will get compared
     * to eachother.
     *
     * The result of this method should be the same whether A is compare to B
     * or B is compared to A.
     *
     * @param other The Scorable to compare to
     * @return The number of correct points.
     */
    public int numCorrectComparedTo(E other);

    /**
     * The number of total possible 'points' that the two scorables can get
     * compared to eachother.
     *
     * @param other The Scorable to compare to
     * @return The total possible points
     */
    public int numTotalComparedTo(E other);

}

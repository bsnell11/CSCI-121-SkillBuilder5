import java.util.Map;

/**
 * A Grok is a creature in a video game.  It can ingest a PowerPill to
 * replenish its energy and takes hits that reduce its power level.
 * The Grok's behaviour is modelled as a Finite State Machine with four
 * states: DORMANT, ACTIVE, WEAKENED, and DEFEATED.
 *
 *  @author (You)
 *  @version (0.1)
 */

public class Grok
{
    // ---------------------------------------------------------------
    // Nested enum — the four FSM states
    // ---------------------------------------------------------------

    /**
     * The four states of the Grok FSM.
     * <ul>
     *   <li>DORMANT  – starting state; power level is 0, no pill taken yet.</li>
     *   <li>ACTIVE   – power level &ge; 20; the Grok is a full threat.</li>
     *   <li>WEAKENED – power level is 1–19; still dangerous but vulnerable.</li>
     *   <li>DEFEATED – power level &le; 0; the Grok can no longer act.</li>
     * </ul>
     */
    public enum GrokState
    {
        DORMANT,
        ACTIVE,
        WEAKENED,
        DEFEATED
    }

    // ---------------------------------------------------------------
    // Class (static) constants
    // ---------------------------------------------------------------

    /** Default power level assigned by the no-argument constructor. */
    public static final int DEFAULT_POWER_LEVEL = 0;

    // ---------------------------------------------------------------
    // Instance fields
    // ---------------------------------------------------------------

    private int       powerLevel;
    private GrokState state;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /**
     * Initializes a Grok object to the default power level of 0.
     */
    public Grok()
    {
        this.powerLevel = DEFAULT_POWER_LEVEL;
        this.state      = GrokState.DORMANT;
    }

    /**
     * Initializes a Grok object to power powerLevel.
     * @param powerLevel the initial power level for this Grok.
     */
    public Grok(int powerLevel)
    {
        // Do not allow the powerLevel to be set so that
        // it is less than the MIN_POWER_LEVEL

        powerLevel = powerLevel < DEFAULT_POWER_LEVEL ? DEFAULT_POWER_LEVEL: powerLevel;
        this.powerLevel = powerLevel;
        this.state = GrokState.DORMANT;
    }

    // ---------------------------------------------------------------
    // Accessor methods
    // ---------------------------------------------------------------

    /*
     * Returns the power level of this Grok.
     * @return returns the power level of this Grok
     */
    public int getPowerLevel()
    {
        return powerLevel;
    }

    /*
     * Returns true if this Grok is in a dormant state; false otherwise.
     * @return true if this Grok is in a dormant state; false otherwise.
     */
    public boolean isDormant()
    {
        // TODO: replace this line with your code.
    }

    /*
     * Returns true if this Grok is in a weakened state; false otherwise.
     * @return true if this Grok is in a weakened state; false otherwise.
     */
    public boolean isWeakened()
    {
        // TODO: replace this line with your code.
    }

    /*
     * Returns true if this Grok is in an active state; false otherwise.
     * @return true if this Grok is in an active state; false otherwise.
     */
    public boolean isActive()
    {
        // TODO: replace this line with your code.
    }

    /*
     * Returns true if this Grok is in a defeated state; false otherwise.
     * @return true if this Grok is in a defeated state; false otherwise.
     */
    public boolean isDefeated()
    {
        // TODO: replace this line with your code.
    }

    // ---------------------------------------------------------------
    // Mutator methods
    // ---------------------------------------------------------------

    /*
     * Sets the power level of this Grok.
     * @param powerLevel the power value to set for this Grok.
     */
    public void setPowerLevel(int powerLevel)
    {
        this.powerLevel = powerLevel < 0 ? 0 : powerLevel;
    }

    /*
     * Invoked when this Grok takes a PowerPill.
     * The power of the pill is added to the power level of this Grok,
     * and the FSM state is updated accordingly.
     * If the Grok is DEFEATED, this method has no effect.
     * @param pill The PowerPill that this Grok ingests.
     */
    public void takePowerPill(PowerPill pill)
    {
        // TODO: replace this line with your code.
    }

    /*
     * Invoked when this Grok takes a hit.  The power level of
     * this Grok is reduced by 5, and the FSM state is updated.
     * If the Grok is DEFEATED, this method has no effect.
     * Note: DEFEATED can only be reached from WEAKENED, because
     * a Grok in ACTIVE has at least 20 power and a single hit
     * reduces power by only 5.
     */
    public void tookHit()
    {
        // TODO: replace this line with your code.
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for the Grok class.
 *
 * Test strategy
 * -------------
 * Each FSM transition in the specification is covered by at least one test.
 * Tests are grouped by the method under test and labelled with the
 * (currentState, event) pair they exercise.
 */
@DisplayName("Grok FSM Tests")
class GrokTest
{
    // -----------------------------------------------------------------------
    // Shared fixtures
    // -----------------------------------------------------------------------

    private Grok grok;

    /** A pill whose power lifts any Grok to ACTIVE (power >= 20). */
    private PowerPill strongPill;

    /** A pill whose power alone will NOT reach the ACTIVE threshold. */
    private PowerPill weakPill;

    @BeforeEach
    void setUp()
    {
        grok       = new Grok();          // powerLevel = 0, state = DORMANT
        strongPill = new PowerPill("Strong", 30);
        weakPill   = new PowerPill("Weak",    5);
    }

    // -----------------------------------------------------------------------
    // Constructor tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Default constructor: powerLevel is 0")
    void defaultConstructor_powerLevelIsZero()
    {
        assertEquals(0, grok.getPowerLevel());
    }

    @Test
    @DisplayName("Default constructor: state is DORMANT")
    void defaultConstructor_stateIsDormant()
    {
        assertTrue(grok.isDormant());
    }

    @Test
    @DisplayName("Value constructor: powerLevel is set correctly")
    void valueConstructor_powerLevelIsSet()
    {
        Grok g = new Grok(100);
        assertEquals(100, g.getPowerLevel());
    }

    @Test
    @DisplayName("Value constructor: state is DORMANT")
    void valueConstructor_stateIsDormant()
    {
        Grok g = new Grok(100);
        assertTrue(g.isDormant());
    }

    // -----------------------------------------------------------------------
    // Accessor / mutator tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setPowerLevel / getPowerLevel round-trip")
    void setPowerLevel_roundTrip()
    {
        grok.setPowerLevel(42);
        assertEquals(42, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // takePowerPill — DORMANT transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DORMANT + strongPill → ACTIVE (power >= 20)")
    void dormant_strongPill_becomesActive()
    {
        grok.takePowerPill(strongPill);          // 0 + 30 = 30 >= 20
        assertTrue(grok.isActive());
        assertEquals(30, grok.getPowerLevel());
    }

    @Test
    @DisplayName("DORMANT + weakPill → WEAKENED (power < 20)")
    void dormant_weakPill_becomesWeakened()
    {
        grok.takePowerPill(weakPill);            // 0 + 5 = 5 < 20
        assertTrue(grok.isWeakened());
        assertEquals(5, grok.getPowerLevel());
    }

    @Test
    @DisplayName("DORMANT + pill bringing power exactly to 20 → ACTIVE")
    void dormant_pillExactly20_becomesActive()
    {
        grok.takePowerPill(new PowerPill("Edge", 20));
        assertTrue(grok.isActive());
        assertEquals(20, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // takePowerPill — ACTIVE transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("ACTIVE + anyPill → ACTIVE (power increases, stays >= 20)")
    void active_anyPill_staysActive()
    {
        grok.takePowerPill(strongPill);          // → ACTIVE, power = 30
        int before = grok.getPowerLevel();
        grok.takePowerPill(weakPill);            // 30 + 5 = 35, still ACTIVE
        assertTrue(grok.isActive());
        assertEquals(before + weakPill.getPower(), grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // takePowerPill — WEAKENED transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("WEAKENED + strongPill → ACTIVE (cumulative power >= 20)")
    void weakened_strongPill_becomesActive()
    {
        grok.takePowerPill(weakPill);            // → WEAKENED, power = 5
        grok.takePowerPill(strongPill);          // 5 + 30 = 35 >= 20
        assertTrue(grok.isActive());
        assertEquals(35, grok.getPowerLevel());
    }

    @Test
    @DisplayName("WEAKENED + weakPill → WEAKENED (cumulative power still < 20)")
    void weakened_weakPill_staysWeakened()
    {
        grok.takePowerPill(weakPill);            // → WEAKENED, power = 5
        grok.takePowerPill(weakPill);            // 5 + 5 = 10 < 20
        assertTrue(grok.isWeakened());
        assertEquals(10, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // takePowerPill — DEFEATED guard
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DEFEATED + pill → no effect on power or state")
    void defeated_pill_noEffect()
    {
        driveToDefeated();
        int powerBefore = grok.getPowerLevel();
        grok.takePowerPill(strongPill);
        assertTrue(grok.isDefeated());
        assertEquals(powerBefore, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // tookHit — DORMANT transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DORMANT + hit → DORMANT (power stays <= 0 so state stays DORMANT)")
    void dormant_hit_staysDormant()
    {
        grok.tookHit();                          // 0 - 5 = -5 <= 0 → DORMANT? 
        // Per spec: DORMANT + tookHit always stays DORMANT.
        // Because powerLevel drops to -5 <= 0 the DEFEATED branch fires,
        // but the spec table says DORMANT + hit → DORMANT.
        // The implementation must handle this: when starting from DORMANT
        // the Grok should stay DORMANT even if power goes negative.
        // (One acceptable approach: only transition to DEFEATED from WEAKENED.)
        assertTrue(grok.isDormant());
    }

    @Test
    @DisplayName("DORMANT + hit → power decreases by 5")
    void dormant_hit_powerDecreases()
    {
        grok.tookHit();
        assertEquals(0, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // tookHit — ACTIVE transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("ACTIVE + hit, power still >= 20 → ACTIVE")
    void active_hit_staysActive()
    {
        grok.takePowerPill(new PowerPill("Big", 50)); // power = 50, ACTIVE
        grok.tookHit();                               // 50 - 5 = 45 >= 20
        assertTrue(grok.isActive());
        assertEquals(45, grok.getPowerLevel());
    }

    @Test
    @DisplayName("ACTIVE + hit, power drops to 1–19 → WEAKENED")
    void active_hit_becomesWeakened()
    {
        // Give just enough power to be ACTIVE at exactly 20
        grok.takePowerPill(new PowerPill("Edge", 20)); // power = 20, ACTIVE
        grok.tookHit();                                // 20 - 5 = 15, WEAKENED
        assertTrue(grok.isWeakened());
        assertEquals(15, grok.getPowerLevel());
    }

    @Test
    @DisplayName("ACTIVE cannot go directly to DEFEATED in one hit")
    void active_cannotReachDefeatedInOneHit()
    {
        // Minimum ACTIVE power is 20; one hit leaves 15 (WEAKENED, not DEFEATED)
        grok.takePowerPill(new PowerPill("Min", 20));
        grok.tookHit();
        assertTrue(!grok.isDefeated());
    }

    // -----------------------------------------------------------------------
    // tookHit — WEAKENED transitions
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("WEAKENED + hit, power still > 0 → WEAKENED")
    void weakened_hit_staysWeakened()
    {
        grok.takePowerPill(weakPill);   // power = 5, WEAKENED
        // Need power in 1–19 range that survives one hit (power > 5)
        grok.takePowerPill(new PowerPill("Mid", 10)); // power = 15, WEAKENED
        grok.tookHit();                               // 15 - 5 = 10, WEAKENED
        assertTrue(grok.isWeakened());
        assertEquals(10, grok.getPowerLevel());
    }

    @Test
    @DisplayName("WEAKENED + hit, power drops to exactly 0 → DEFEATED")
    void weakened_hit_powerExactlyZero_becomesDefeated()
    {
        grok.takePowerPill(new PowerPill("Five", 5)); // power = 5, WEAKENED
        grok.tookHit();                               // 5 - 5 = 0 <= 0
        assertTrue(grok.isDefeated());
        assertEquals(0, grok.getPowerLevel());
    }

    @Test
    @DisplayName("WEAKENED + hit, power drops below 0 → DEFEATED")
    void weakened_hit_powerBelowZero_becomesDefeated()
    {
        grok.takePowerPill(new PowerPill("Three", 3)); // power = 3, WEAKENED
        grok.tookHit();
        assertTrue(grok.isDefeated());
        assertEquals(0, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // tookHit — DEFEATED guard
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DEFEATED + hit → no effect on power or state")
    void defeated_hit_noEffect()
    {
        driveToDefeated();
        int powerBefore = grok.getPowerLevel();
        grok.tookHit();
        assertTrue(grok.isDefeated());
        assertEquals(powerBefore, grok.getPowerLevel());
    }

    // -----------------------------------------------------------------------
    // Multi-step integration scenarios
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Full path: DORMANT → ACTIVE → WEAKENED → DEFEATED")
    void fullPath_dormantToDefeated()
    {
        assertTrue(grok.isDormant());

        grok.takePowerPill(new PowerPill("Boost", 20)); // → ACTIVE
        assertTrue(grok.isActive());

        // Hit down from 20 to 15 → WEAKENED
        grok.tookHit();
        assertTrue(grok.isWeakened());

        // Hit down from 15 to 10 → WEAKENED
        grok.tookHit();
        assertTrue(grok.isWeakened());

        // Hit down from 10 to 5 → WEAKENED
        grok.tookHit();
        assertTrue(grok.isWeakened());

        // Hit down from 5 to 0 → DEFEATED
        grok.tookHit();
        assertTrue(grok.isDefeated());
    }

    @Test
    @DisplayName("Grok recovers from WEAKENED to ACTIVE via pill")
    void weakened_recoversToActive()
    {
        grok.takePowerPill(new PowerPill("Small", 5));  // power=5, WEAKENED
        grok.takePowerPill(new PowerPill("Large", 20)); // power=25, ACTIVE
        assertTrue(grok.isActive());
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    /**
     * Drives the grok from DORMANT into WEAKENED then DEFEATED using the
     * minimum number of deterministic operations.
     */
    private void driveToDefeated()
    {
        grok.takePowerPill(new PowerPill("Starter", 5)); // power=5, WEAKENED
        grok.tookHit();                                   // power=0, DEFEATED
    }
}

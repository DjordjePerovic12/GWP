package com.global_water_polo.play_abroad.core.utils

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** v29's okEmail and uniEmail. */
class EmailRulesTest {

    @Test
    fun universityAddressesPass() {
        assertTrue(EmailRules.isUniversityEmail("coach@stanford.edu"))
        assertTrue(EmailRules.isUniversityEmail("j.lee@athletics.ucla.edu"))
        assertTrue(EmailRules.isUniversityEmail("coach@ox.ac.uk"))
        assertTrue(EmailRules.isUniversityEmail("trener@uni.edu.rs"))
        assertTrue(EmailRules.isUniversityEmail(" Coach@Stanford.EDU "))
    }

    @Test
    fun otherAddressesDoNot() {
        assertFalse(EmailRules.isUniversityEmail("coach@gmail.com"))
        assertFalse(EmailRules.isUniversityEmail("coach@edu.com"))
        assertFalse(EmailRules.isUniversityEmail("coach.edu"))
    }

    @Test
    fun theHintWaitsForAWholeAddress() {
        assertFalse(EmailRules.showUniversityHint("coach@gmail"))
        assertTrue(EmailRules.showUniversityHint("coach@gmail.com"))
        assertFalse(EmailRules.showUniversityHint("coach@stanford.edu"))
    }

    @Test
    fun plainEmailCheck() {
        assertTrue(EmailRules.isEmail("fan@example.com"))
        assertFalse(EmailRules.isEmail("fan@example"))
        assertFalse(EmailRules.isEmail("fan example.com"))
    }
}

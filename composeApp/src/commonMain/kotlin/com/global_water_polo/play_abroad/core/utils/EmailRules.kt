package com.global_water_polo.play_abroad.core.utils

/**
 * v29's email checks (okEmail, uniEmail). Only official university addresses can open a college coach account:
 * .edu, or ac.xx / edu.xx country domains. GWP still reviews every account.
 */
object EmailRules {

    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val UNIVERSITY = Regex("^[^@\\s]+@([a-z0-9-]+\\.)+(edu|ac\\.[a-z]{2,}|edu\\.[a-z]{2,})$", RegexOption.IGNORE_CASE)

    fun isEmail(value: String): Boolean = EMAIL.matches(value.trim())

    fun isUniversityEmail(value: String): Boolean = UNIVERSITY.matches(value.trim())

    /** Show the university hint once it looks like an address ("@" and a dot) but isn't a university one. */
    fun showUniversityHint(value: String): Boolean {
        val v = value.trim()
        return "@" in v && "." in v.substringAfter("@") && !isUniversityEmail(v)
    }
}

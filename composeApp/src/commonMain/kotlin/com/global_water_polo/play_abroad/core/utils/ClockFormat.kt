package com.global_water_polo.play_abroad.core.utils

import com.global_water_polo.play_abroad.domain.model.ClockCountdown

fun Int.pad2(): String = toString().padStart(2, '0')

/** "412d 05:12:33" as the prototype's clock chip shows it. */
fun clockText(countdown: ClockCountdown): String =
    "${countdown.days}d ${countdown.hours.pad2()}:${countdown.minutes.pad2()}:${countdown.seconds.pad2()}"

/** "05:12:33" under the big day count. */
fun hmsText(countdown: ClockCountdown): String =
    "${countdown.hours.pad2()}:${countdown.minutes.pad2()}:${countdown.seconds.pad2()}"

/** "2027-28" season label. */
fun seasonLabel(year: Int): String = "$year-${(year + 1).toString().takeLast(2)}"

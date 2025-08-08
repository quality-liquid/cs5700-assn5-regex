package org.example.floatState

import org.example.DIGITS_NO_ZERO
import org.example.State

class LookingForZeroFirst: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '0') {
            PeriodRequired()
        } else if (character in DIGITS_NO_ZERO) {
            WaitingForPeriod()
        } else {
            NotFloat()
        }
    }
}
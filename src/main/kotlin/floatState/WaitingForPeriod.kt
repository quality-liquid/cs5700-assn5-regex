package org.example.floatState

import org.example.DIGITS
import org.example.State

class WaitingForPeriod: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in DIGITS) {
            this
        } else if (character == '.') {
            DigitRequired()
        } else {
            NotFloat()
        }
    }
}
package org.example.floatState

import org.example.State

class PeriodRequired: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '.') {
            DigitRequired()
        } else {
            NotFloat()
        }
    }
}
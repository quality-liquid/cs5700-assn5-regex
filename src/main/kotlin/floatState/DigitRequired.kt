package org.example.floatState

import org.example.DIGITS
import org.example.State

class DigitRequired: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in DIGITS) {
            FloatingPoint()
        } else {
            NotFloat()
        }
    }
}
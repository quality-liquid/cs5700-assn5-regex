package org.example.floatState

import org.example.DIGITS
import org.example.State

class FloatingPoint: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in DIGITS) {
            this
        } else {
            NotFloat()
        }
    }
}
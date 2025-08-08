package org.example.integerState

import org.example.DIGITS_NO_ZERO
import org.example.State

class LookingForDigit: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in DIGITS_NO_ZERO) {
            Integer()
        } else {
            NonInteger()
        }
    }
}

package org.example.complexPasswordState

import org.example.SPECIAL_CHARACTERS
import org.example.State

class ValidPassword: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in SPECIAL_CHARACTERS) {
            LookingForNonSpecialChar()
        } else this
    }
}
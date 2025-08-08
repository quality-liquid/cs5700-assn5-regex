package org.example.complexPasswordState

import org.example.SPECIAL_CHARACTERS
import org.example.State

class LookingForNonSpecialChar: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in SPECIAL_CHARACTERS) {
            this
        } else ValidPassword()
    }
}
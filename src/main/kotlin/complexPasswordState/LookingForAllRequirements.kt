package org.example.complexPasswordState

import org.example.CAPITALS
import org.example.SPECIAL_CHARACTERS
import org.example.State

class LookingForAllRequirements: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in SPECIAL_CHARACTERS) {
            LookingForCapital()
        } else if (character in CAPITALS) {
            LookingForSpecialCharacter()
        } else this
    }
}
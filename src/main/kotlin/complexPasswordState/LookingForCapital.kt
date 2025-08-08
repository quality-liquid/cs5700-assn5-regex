package org.example.complexPasswordState

import org.example.CAPITALS
import org.example.State

class LookingForCapital: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in CAPITALS) {
            ValidPassword()
        } else this
    }
}
package org.example.emailState

import org.example.State

class WaitingForPeriod: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in "@ ") {
            InvalidEmail()
        } else if (character == '.') {
            LookingForFirstCharPartC()
        } else this
    }
}
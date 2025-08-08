package org.example.emailState

import org.example.State

class LookingForFirstCharPartA: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in "@ ") {
            InvalidEmail()
        } else {
            WaitingForAtSymbol()
        }
    }
}
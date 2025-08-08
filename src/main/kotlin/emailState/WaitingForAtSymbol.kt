package org.example.emailState

import org.example.State

class WaitingForAtSymbol: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '@') {
            LookingForFirstCharPartB()
        } else if (character == ' ') {
            return InvalidEmail()
        } else this
    }
}
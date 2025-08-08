package org.example.emailState

import org.example.State

class InvalidEmail: State {
    override fun consumeCharacter(character: Char): State {
        return this
    }
}
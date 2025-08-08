package org.example.integerState

import org.example.State

class NonInteger: State {
    override fun consumeCharacter(character: Char): State {
        return this
    }
}
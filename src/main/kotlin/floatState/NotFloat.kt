package org.example.floatState

import org.example.State

class NotFloat: State {
    override fun consumeCharacter(character: Char): State {
        return this
    }
}
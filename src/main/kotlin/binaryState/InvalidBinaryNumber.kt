package org.example.binaryState

import org.example.State

class InvalidBinaryNumber: State {
    override fun consumeCharacter(character: Char): State {
        return this
    }
}
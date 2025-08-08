package org.example.binaryState

import org.example.State

class LookingForOneFirst: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '1') {
            ValidBinaryNumber()
        } else {
            return InvalidBinaryNumber()
        }
    }
}
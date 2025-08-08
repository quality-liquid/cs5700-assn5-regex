package org.example.binaryState

import org.example.State

class Incomplete: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '0') {
            this
        } else if (character == '1') {
            ValidBinaryNumber()
        } else {
            InvalidBinaryNumber()
        }
    }
}

package org.example.binaryState

import org.example.State

class ValidBInaryNumber: State {
    override fun consumeCharacter(character: Char): State {
        return if (character == '1') {
            this
        } else if (character == '0') {
            Incomplete()
        } else {
            InvalidBinaryNumber()
        }
    }
}
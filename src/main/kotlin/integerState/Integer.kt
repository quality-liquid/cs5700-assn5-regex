package org.example.integerState

import org.example.DIGITS
import org.example.State

class Integer: State {
    override fun consumeCharacter(character: Char): State {
        return if (character in DIGITS) {
            Integer()
        } else {
            NonInteger()
        }
    }
}
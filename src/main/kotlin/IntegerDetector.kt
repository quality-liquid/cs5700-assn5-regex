package org.example

import org.example.integerState.Integer
import org.example.integerState.LookingForDigit

class IntegerDetector {
    var state: State = LookingForDigit()

    fun detectInteger(candidate: String): Boolean {
        state = LookingForDigit()
        for (char in candidate) {
            state = state.consumeCharacter(char)
        }

        return (state is Integer)
    }
}
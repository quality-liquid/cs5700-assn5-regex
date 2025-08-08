package org.example

import org.example.floatState.FloatingPoint
import org.example.floatState.LookingForZeroFirst
import org.example.integerState.Integer
import org.example.integerState.LookingForDigit

class FloatDetector {
    var state: State = LookingForZeroFirst()

    fun detectInteger(candidate: String): Boolean {
        state = LookingForZeroFirst()
        for (char in candidate) {
            state = state.consumeCharacter(char)
        }

        return (state is FloatingPoint)
    }
}
package org.example

import org.example.binaryState.LookingForOneFirst
import org.example.binaryState.ValidBinaryNumber

class BinStartsEndsWithZeroDetector {
    var state: State = LookingForOneFirst()

    fun detect(candidate: String): Boolean {
        state = LookingForOneFirst()
        for (char in candidate) {
            state = state.consumeCharacter(char)
        }

        return (state is ValidBinaryNumber)
    }
}
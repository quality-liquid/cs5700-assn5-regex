package org.example

import org.example.complexPasswordState.LookingForAllRequirements
import org.example.complexPasswordState.ValidPassword

class PasswordDetector {
    var state: State = LookingForAllRequirements()

    fun detect(candidate: String): Boolean {
        state = LookingForAllRequirements()
        for (char in candidate) {
            state = state.consumeCharacter(char)
        }

        return (state is ValidPassword)
    }
}
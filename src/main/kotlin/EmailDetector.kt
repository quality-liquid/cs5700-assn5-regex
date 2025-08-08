package org.example

import org.example.emailState.LookingForFirstCharPartA
import org.example.emailState.ValidEmail

class EmailDetector {
    var state: State = LookingForFirstCharPartA()

    fun detect(candidate: String): Boolean {
        state = LookingForFirstCharPartA()
        for (char in candidate) {
            state = state.consumeCharacter(char)
        }

        return (state is ValidEmail)
    }
}
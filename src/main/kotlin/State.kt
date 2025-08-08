package org.example

interface State {
    fun consumeCharacter(character: Char): State
}

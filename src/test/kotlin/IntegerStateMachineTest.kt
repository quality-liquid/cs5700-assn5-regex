package org.example

import org.example.integerState.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class IntegerStateMachineTest {
    
    private lateinit var detector: IntegerDetector
    
    @BeforeEach
    fun setUp() {
        detector = IntegerDetector()
    }

    @Test
    fun `IntegerDetector should detect valid single digit integers`() {
        for (digit in '1'..'9') {
            assertTrue(detector.detectInteger(digit.toString()), "Should detect $digit as valid integer")
        }
    }

    @Test
    fun `IntegerDetector should reject zero as invalid integer`() {
        assertFalse(detector.detectInteger("0"), "Should reject '0' as invalid integer")
    }

    @Test
    fun `IntegerDetector should detect valid multi-digit integers`() {
        val validIntegers = listOf("1", "12", "123", "999", "1000", "7654321", "99999")
        
        for (integer in validIntegers) {
            assertTrue(detector.detectInteger(integer), "Should detect '$integer' as valid integer")
        }
    }

    @Test
    fun `IntegerDetector should reject integers starting with zero`() {
        val invalidIntegers = listOf("0", "01", "007", "0123")
        
        for (integer in invalidIntegers) {
            assertFalse(detector.detectInteger(integer), "Should reject '$integer' as invalid integer")
        }
    }

    @Test
    fun `IntegerDetector should reject strings with non-digit characters`() {
        val invalidStrings = listOf("1a", "a1", "12.3", "1-2", "1+2", "1 2", "abc", "1x9")
        
        for (string in invalidStrings) {
            assertFalse(detector.detectInteger(string), "Should reject '$string' as invalid integer")
        }
    }

    @Test
    fun `IntegerDetector should reject empty string`() {
        assertFalse(detector.detectInteger(""), "Should reject empty string as invalid integer")
    }

    @Test
    fun `IntegerDetector should reject strings starting with non-digits`() {
        val invalidStrings = listOf("a123", " 123", ".123", "-123", "+123")
        
        for (string in invalidStrings) {
            assertFalse(detector.detectInteger(string), "Should reject '$string' as invalid integer")
        }
    }

    @Test
    fun `IntegerDetector should handle special characters`() {
        val invalidStrings = listOf("\n", "\t", " ", "!", "@", "#", "$", "%")
        
        for (string in invalidStrings) {
            assertFalse(detector.detectInteger(string), "Should reject '$string' as invalid integer")
        }
    }

    @Test
    fun `IntegerDetector state should reset between calls`() {
        // First call with invalid input
        assertFalse(detector.detectInteger("0abc"))
        
        // Second call with valid input should work
        assertTrue(detector.detectInteger("123"))
        
        // Third call with another valid input
        assertTrue(detector.detectInteger("456"))
    }

    // Keep some state-level tests for comprehensive coverage
    @Test
    fun `LookingForDigit state transitions work correctly`() {
        val state = LookingForDigit()
        
        assertTrue(state.consumeCharacter('1') is Integer)
        assertTrue(state.consumeCharacter('0') is NonInteger)
        assertTrue(state.consumeCharacter('a') is NonInteger)
    }

    @Test
    fun `Integer state transitions work correctly`() {
        val state = Integer()
        
        assertTrue(state.consumeCharacter('0') is Integer)
        assertTrue(state.consumeCharacter('5') is Integer)
        assertTrue(state.consumeCharacter('a') is NonInteger)
    }

    @Test
    fun `NonInteger state always stays NonInteger`() {
        val state = NonInteger()
        
        assertTrue(state.consumeCharacter('0') is NonInteger)
        assertTrue(state.consumeCharacter('a') is NonInteger)
        assertSame(state, state.consumeCharacter('x'))
    }

    @Test
    fun `test single digit integers`() {
        for (digit in '1'..'9') {
            var state: State = LookingForDigit()
            state = state.consumeCharacter(digit)
            assertTrue(state is Integer, "Single digit $digit should result in Integer state")
        }
    }

    @Test
    fun `test multi-digit integers`() {
        val testNumbers = listOf("123", "999", "1000", "7654321")
        
        for (number in testNumbers) {
            var state: State = LookingForDigit()
            
            for (char in number) {
                state = state.consumeCharacter(char)
            }
            
            assertTrue(state is Integer, "Number $number should end in Integer state")
        }
    }
}

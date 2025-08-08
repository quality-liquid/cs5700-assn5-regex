package org.example

import org.example.floatState.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class FloatStateMachineTest {
    
    private lateinit var detector: FloatDetector
    
    @BeforeEach
    fun setUp() {
        detector = FloatDetector()
    }

    @Test
    fun `FloatDetector should detect valid floats starting with zero`() {
        val validFloats = listOf("0.1", "0.5", "0.123", "0.999", "0.0")
        
        for (float in validFloats) {
            assertTrue(detector.detect(float), "Should detect '$float' as valid float")
        }
    }

    @Test
    fun `FloatDetector should detect valid floats starting with non-zero`() {
        val validFloats = listOf("1.0", "1.5", "12.34", "999.001", "123.456789")
        
        for (float in validFloats) {
            assertTrue(detector.detect(float), "Should detect '$float' as valid float")
        }
    }

    @Test
    fun `FloatDetector should reject integers without decimal point`() {
        val integers = listOf("0", "1", "12", "123", "999")
        
        for (integer in integers) {
            assertFalse(detector.detect(integer), "Should reject '$integer' as not a float")
        }
    }

    @Test
    fun `FloatDetector should reject floats with leading zeros`() {
        val invalidFloats = listOf("01.5", "007.123", "00.1")
        
        for (float in invalidFloats) {
            assertFalse(detector.detect(float), "Should reject '$float' as invalid float")
        }
    }

    @Test
    fun `FloatDetector should reject incomplete floats`() {
        val incompleteFloats = listOf("1.", "0.", "123.", ".")
        
        for (float in incompleteFloats) {
            assertFalse(detector.detect(float), "Should reject '$float' as incomplete float")
        }
    }

    @Test
    fun `FloatDetector should reject strings with non-digit characters`() {
        val invalidStrings = listOf("1.a", "a.1", "1.2.3", "1-2", "1+2", "1 .2", "abc")
        
        for (string in invalidStrings) {
            assertFalse(detector.detect(string), "Should reject '$string' as invalid float")
        }
    }

    @Test
    fun `FloatDetector should reject empty string`() {
        assertFalse(detector.detect(""), "Should reject empty string as invalid float")
    }

    @Test
    fun `FloatDetector state should reset between calls`() {
        // First call with invalid input
        assertFalse(detector.detect("0abc"))
        
        // Second call with valid input should work
        assertTrue(detector.detect("1.23"))
        
        // Third call with another valid input
        assertTrue(detector.detect("0.456"))
    }

    // Test individual state classes
    @Test
    fun `LookingForZeroFirst should transition correctly`() {
        val state = LookingForZeroFirst()
        
        assertTrue(state.consumeCharacter('0') is PeriodRequired, "Should transition to PeriodRequired on '0'")
        assertTrue(state.consumeCharacter('1') is WaitingForPeriod, "Should transition to WaitingForPeriod on '1'")
        assertTrue(state.consumeCharacter('9') is WaitingForPeriod, "Should transition to WaitingForPeriod on '9'")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should transition to NotFloat on non-digit")
    }

    @Test
    fun `PeriodRequired should only accept period`() {
        val state = PeriodRequired()
        
        assertTrue(state.consumeCharacter('.') is DigitRequired, "Should transition to DigitRequired on '.'")
        assertTrue(state.consumeCharacter('0') is NotFloat, "Should transition to NotFloat on digit")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should transition to NotFloat on letter")
    }

    @Test
    fun `WaitingForPeriod should handle digits and period`() {
        val state = WaitingForPeriod()
        
        assertTrue(state.consumeCharacter('0') is WaitingForPeriod, "Should stay in WaitingForPeriod on digit")
        assertTrue(state.consumeCharacter('5') is WaitingForPeriod, "Should stay in WaitingForPeriod on digit")
        assertTrue(state.consumeCharacter('.') is DigitRequired, "Should transition to DigitRequired on '.'")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should transition to NotFloat on non-digit")
    }

    @Test
    fun `DigitRequired should require a digit`() {
        val state = DigitRequired()
        
        assertTrue(state.consumeCharacter('0') is FloatingPoint, "Should transition to FloatingPoint on digit")
        assertTrue(state.consumeCharacter('9') is FloatingPoint, "Should transition to FloatingPoint on digit")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should transition to NotFloat on non-digit")
        assertTrue(state.consumeCharacter('.') is NotFloat, "Should transition to NotFloat on period")
    }

    @Test
    fun `FloatingPoint should accept more digits`() {
        val state = FloatingPoint()
        
        assertTrue(state.consumeCharacter('0') is FloatingPoint, "Should stay in FloatingPoint on digit")
        assertTrue(state.consumeCharacter('9') is FloatingPoint, "Should stay in FloatingPoint on digit")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should transition to NotFloat on non-digit")
        assertTrue(state.consumeCharacter('.') is NotFloat, "Should transition to NotFloat on period")
    }

    @Test
    fun `NotFloat should always stay NotFloat`() {
        val state = NotFloat()
        
        assertTrue(state.consumeCharacter('0') is NotFloat, "Should stay in NotFloat")
        assertTrue(state.consumeCharacter('a') is NotFloat, "Should stay in NotFloat")
        assertTrue(state.consumeCharacter('.') is NotFloat, "Should stay in NotFloat")
        assertSame(state, state.consumeCharacter('x'), "Should return same NotFloat instance")
    }

    @Test
    fun `test complete float parsing sequences`() {
        // Test "1.23"
        var state: State = LookingForZeroFirst()
        state = state.consumeCharacter('1')
        assertTrue(state is WaitingForPeriod, "Should be in WaitingForPeriod after '1'")
        
        state = state.consumeCharacter('.')
        assertTrue(state is DigitRequired, "Should be in DigitRequired after '.'")
        
        state = state.consumeCharacter('2')
        assertTrue(state is FloatingPoint, "Should be in FloatingPoint after '2'")
        
        state = state.consumeCharacter('3')
        assertTrue(state is FloatingPoint, "Should stay in FloatingPoint after '3'")
    }

    @Test
    fun `test zero-prefixed float sequence`() {
        // Test "0.5"
        var state: State = LookingForZeroFirst()
        state = state.consumeCharacter('0')
        assertTrue(state is PeriodRequired, "Should be in PeriodRequired after '0'")
        
        state = state.consumeCharacter('.')
        assertTrue(state is DigitRequired, "Should be in DigitRequired after '.'")
        
        state = state.consumeCharacter('5')
        assertTrue(state is FloatingPoint, "Should be in FloatingPoint after '5'")
    }

    @Test
    fun `test invalid sequences`() {
        // Test "01.5" (invalid leading zero)
        var state: State = LookingForZeroFirst()
        state = state.consumeCharacter('0')
        state = state.consumeCharacter('1')
        assertTrue(state is NotFloat, "Should be in NotFloat after invalid sequence")
        
        // Test "1." (incomplete)
        state = LookingForZeroFirst()
        state = state.consumeCharacter('1')
        state = state.consumeCharacter('.')
        assertTrue(state is DigitRequired, "Should be in DigitRequired, not final state")
    }
}

package org.example

import org.example.binaryState.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class BinaryStateMachineTest {
    
    private lateinit var detector: BinStartsEndsWithZeroDetector
    
    @BeforeEach
    fun setUp() {
        detector = BinStartsEndsWithZeroDetector()
    }

    @Test
    fun `BinaryDetector should accept single '1' as valid`() {
        assertTrue(detector.detect("1"), "Should accept '1' as valid binary")
    }

    @Test
    fun `BinaryDetector should accept valid binary numbers starting and ending with 1`() {
        val validBinaries = listOf("11", "101", "111", "1001", "10101", "110011", "1111")
        
        for (binary in validBinaries) {
            assertTrue(detector.detect(binary), "Should accept '$binary' as valid binary")
        }
    }

    @Test
    fun `BinaryDetector should reject binary numbers ending with zero`() {
        val invalidBinaries = listOf("10", "110", "1110", "1100", "11110", "101110", "11111110")
        
        for (binary in invalidBinaries) {
            assertFalse(detector.detect(binary), "Should reject '$binary' as invalid binary (ends with 0)")
        }
    }

    @Test
    fun `BinaryDetector should reject binary numbers not starting with one`() {
        val invalidBinaries = listOf("0", "01", "010", "0110", "00110", "011")
        
        for (binary in invalidBinaries) {
            assertFalse(detector.detect(binary), "Should reject '$binary' as invalid binary (doesn't start with 1)")
        }
    }

    @Test
    fun `BinaryDetector should reject strings with non-binary characters`() {
        val invalidStrings = listOf("12", "1a0", "1 0", "1.0", "abc", "1x0", "10a")
        
        for (string in invalidStrings) {
            assertFalse(detector.detect(string), "Should reject '$string' as invalid binary")
        }
    }

    @Test
    fun `BinaryDetector should reject empty string`() {
        assertFalse(detector.detect(""), "Should reject empty string as invalid binary")
    }

    @Test
    fun `BinaryDetector state should reset between calls`() {
        // First call with invalid input
        assertFalse(detector.detect("0abc"))
        
        // Second call with valid input should work
        assertTrue(detector.detect("1101"))
        
        // Third call with another valid input
        assertTrue(detector.detect("1"))
    }

    // Test individual state classes
    @Test
    fun `LookingForOneFirst should only accept '1' as first character`() {
        val state = LookingForOneFirst()
        
        assertTrue(state.consumeCharacter('1') is ValidBinaryNumber, "Should transition to ValidBinaryNumber on '1'")
        assertTrue(state.consumeCharacter('0') is InvalidBinaryNumber, "Should transition to InvalidBinaryNumber on '0'")
        assertTrue(state.consumeCharacter('a') is InvalidBinaryNumber, "Should transition to InvalidBinaryNumber on non-binary")
    }

    @Test
    fun `ValidBinaryNumber should handle binary digits correctly`() {
        val state = ValidBinaryNumber()
        
        assertTrue(state.consumeCharacter('1') is ValidBinaryNumber, "Should stay in ValidBinaryNumber on '1'")
        assertTrue(state.consumeCharacter('0') is Incomplete, "Should transition to Incomplete on '0'")
        assertTrue(state.consumeCharacter('a') is InvalidBinaryNumber, "Should transition to InvalidBinaryNumber on non-binary")
    }

    @Test
    fun `Incomplete should handle transitions correctly`() {
        val state = Incomplete()
        
        assertTrue(state.consumeCharacter('0') is Incomplete, "Should stay in Incomplete on '0'")
        assertTrue(state.consumeCharacter('1') is ValidBinaryNumber, "Should transition back to ValidBinaryNumber on '1'")
        assertTrue(state.consumeCharacter('a') is InvalidBinaryNumber, "Should transition to InvalidBinaryNumber on non-binary")
    }

    @Test
    fun `InvalidBinaryNumber should always stay InvalidBinaryNumber`() {
        val state = InvalidBinaryNumber()
        
        assertTrue(state.consumeCharacter('0') is InvalidBinaryNumber, "Should stay in InvalidBinaryNumber")
        assertTrue(state.consumeCharacter('1') is InvalidBinaryNumber, "Should stay in InvalidBinaryNumber")
        assertTrue(state.consumeCharacter('a') is InvalidBinaryNumber, "Should stay in InvalidBinaryNumber")
        assertSame(state, state.consumeCharacter('x'), "Should return same InvalidBinaryNumber instance")
    }

    @Test
    fun `test complete binary parsing sequences`() {
        // Test "101" (valid: starts with 1, ends with 1)
        var state: State = LookingForOneFirst()
        state = state.consumeCharacter('1')
        assertTrue(state is ValidBinaryNumber, "Should be in ValidBinaryNumber after '1'")
        
        state = state.consumeCharacter('0')
        assertTrue(state is Incomplete, "Should be in Incomplete after '0'")
        
        state = state.consumeCharacter('1')
        assertTrue(state is ValidBinaryNumber, "Should be back in ValidBinaryNumber after final '1'")
    }

    @Test
    fun `test sequence ending in ValidBinaryNumber`() {
        // Test "1" (valid single character)
        var state: State = LookingForOneFirst()
        state = state.consumeCharacter('1')
        assertTrue(state is ValidBinaryNumber, "Should be in ValidBinaryNumber after '1'")
    }

    @Test
    fun `test sequence ending with zero should be invalid`() {
        // Test "110" (should end in Incomplete, not valid)
        var state: State = LookingForOneFirst()
        state = state.consumeCharacter('1')
        state = state.consumeCharacter('1')
        state = state.consumeCharacter('0')
        assertTrue(state is Incomplete, "Should be in Incomplete after ending with '0'")
    }

    @Test
    fun `test invalid starting character sequences`() {
        // Test starting with '0'
        var state: State = LookingForOneFirst()
        state = state.consumeCharacter('0')
        assertTrue(state is InvalidBinaryNumber, "Should be in InvalidBinaryNumber after starting with '0'")
        
        // Once invalid, should stay invalid
        state = state.consumeCharacter('1')
        assertTrue(state is InvalidBinaryNumber, "Should stay in InvalidBinaryNumber")
    }

    @Test
    fun `test complex valid patterns`() {
        val validPatterns = listOf("10", "110", "1110", "11110", "101110")
        
        for (pattern in validPatterns) {
            var state: State = LookingForOneFirst()
            for (char in pattern) {
                state = state.consumeCharacter(char)
            }
            // Valid patterns should end in Incomplete (ending with 0)
            assertTrue(state is Incomplete, "Pattern '$pattern' should end in Incomplete state")
        }
    }

    @Test
    fun `test patterns that should end in ValidBinaryNumber`() {
        // Patterns ending with 1 should end in ValidBinaryNumber
        val validPatterns = listOf("1", "11", "111", "101", "1001", "10101")
        
        for (pattern in validPatterns) {
            var state: State = LookingForOneFirst()
            for (char in pattern) {
                state = state.consumeCharacter(char)
            }
            assertTrue(state is ValidBinaryNumber, "Pattern '$pattern' should end in ValidBinaryNumber state")
        }
    }

    @Test
    fun `test patterns that should end in Incomplete`() {
        // Patterns ending with 0 should end in Incomplete (invalid final state)
        val incompletePatterns = listOf("10", "110", "1010", "1100")
        
        for (pattern in incompletePatterns) {
            var state: State = LookingForOneFirst()
            for (char in pattern) {
                state = state.consumeCharacter(char)
            }
            assertTrue(state is Incomplete, "Pattern '$pattern' should end in Incomplete state")
        }
    }
}

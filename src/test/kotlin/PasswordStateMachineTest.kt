package org.example

import org.example.complexPasswordState.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class PasswordStateMachineTest {
    
    private lateinit var detector: PasswordDetector
    
    @BeforeEach
    fun setUp() {
        detector = PasswordDetector()
    }

    @Test
    fun `PasswordDetector should accept valid complex passwords`() {
        val validPasswords = listOf(
            "aaaaH!aa",
            "1234567*9J",
            "asdpoihj;loikjasdf;ijp;lij2309jasd;lfkm20ij@aH",
            "Password123@valid",
            "!Special123A",
            "MyPass@word1"
        )
        
        for (password in validPasswords) {
            if (password.length >= 8) {
                assertTrue(detector.detect(password), "Should accept '$password' as valid password")
            }
        }
    }

    @Test
    fun `PasswordDetector should reject passwords ending with special character`() {
        val invalidPasswords = listOf(
            "aaaaaaa!",
            "Abbbbbbb!",
            "Password123@",
            "ValidPass*",
            "MyPassword#",
            "TestingA&"
        )
        
        for (password in invalidPasswords) {
            assertFalse(detector.detect(password), "Should reject '$password' (ends with special char)")
        }
    }

    @Test
    fun `PasswordDetector should reject passwords without capital letter`() {
        val invalidPasswords = listOf(
            "password123!test",
            "mypass@word123",
            "testing#password",
            "nocase!password"
        )
        
        for (password in invalidPasswords) {
            assertFalse(detector.detect(password), "Should reject '$password' (no capital letter)")
        }
    }

    @Test
    fun `PasswordDetector should reject passwords without special character`() {
        val invalidPasswords = listOf(
            "aaaHaaaaa",
            "Password123",
            "MyPasswordA",
            "TestingABC"
        )
        
        for (password in invalidPasswords) {
            assertFalse(detector.detect(password), "Should reject '$password' (no special character)")
        }
    }

    @Test
    fun `PasswordDetector should reject passwords missing everything`() {
        val invalidPasswords = listOf(
            "a",
            "abc",
            "1234567",
            "password",
            "short"
        )
        
        for (password in invalidPasswords) {
            assertFalse(detector.detect(password), "Should reject '$password' (missing requirements)")
        }
    }

    @Test
    fun `PasswordDetector should reject empty string`() {
        assertFalse(detector.detect(""), "Should reject empty string as invalid password")
    }

    @Test
    fun `PasswordDetector state should reset between calls`() {
        // First call with invalid input
        assertFalse(detector.detect("invalid!"))
        
        // Second call with valid input should work
        assertTrue(detector.detect("ValidPass123@word"))
        
        // Third call with another valid input
        assertTrue(detector.detect("AnotherPass!123"))
    }

    // Test individual state classes
    @Test
    fun `LookingForAllRequirements should transition correctly`() {
        val state = LookingForAllRequirements()
        
        assertTrue(state.consumeCharacter('!') is LookingForCapital, "Should transition to LookingForCapital on special char")
        assertTrue(state.consumeCharacter('@') is LookingForCapital, "Should transition to LookingForCapital on special char")
        assertTrue(state.consumeCharacter('A') is LookingForSpecialCharacter, "Should transition to LookingForSpecialCharacter on capital")
        assertTrue(state.consumeCharacter('Z') is LookingForSpecialCharacter, "Should transition to LookingForSpecialCharacter on capital")
        assertTrue(state.consumeCharacter('a') is LookingForAllRequirements, "Should stay in LookingForAllRequirements on lowercase")
        assertTrue(state.consumeCharacter('1') is LookingForAllRequirements, "Should stay in LookingForAllRequirements on digit")
    }

    @Test
    fun `LookingForCapital should transition correctly`() {
        val state = LookingForCapital()
        
        assertTrue(state.consumeCharacter('A') is ValidPassword, "Should transition to ValidPassword on capital")
        assertTrue(state.consumeCharacter('Z') is ValidPassword, "Should transition to ValidPassword on capital")
        assertTrue(state.consumeCharacter('a') is LookingForCapital, "Should stay in LookingForCapital on lowercase")
        assertTrue(state.consumeCharacter('1') is LookingForCapital, "Should stay in LookingForCapital on digit")
        assertTrue(state.consumeCharacter('!') is LookingForCapital, "Should stay in LookingForCapital on special char")
    }

    @Test
    fun `LookingForSpecialCharacter should transition correctly`() {
        val state = LookingForSpecialCharacter()
        
        assertTrue(state.consumeCharacter('!') is LookingForNonSpecialChar, "Should transition to LookingForNonSpecialChar on special char")
        assertTrue(state.consumeCharacter('@') is LookingForNonSpecialChar, "Should transition to LookingForNonSpecialChar on special char")
        assertTrue(state.consumeCharacter('a') is LookingForSpecialCharacter, "Should stay in LookingForSpecialCharacter on lowercase")
        assertTrue(state.consumeCharacter('A') is LookingForSpecialCharacter, "Should stay in LookingForSpecialCharacter on capital")
        assertTrue(state.consumeCharacter('1') is LookingForSpecialCharacter, "Should stay in LookingForSpecialCharacter on digit")
    }

    @Test
    fun `LookingForNonSpecialChar should transition correctly`() {
        val state = LookingForNonSpecialChar()
        
        assertTrue(state.consumeCharacter('a') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('A') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('1') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('!') is LookingForNonSpecialChar, "Should stay in LookingForNonSpecialChar on special char")
        assertTrue(state.consumeCharacter('@') is LookingForNonSpecialChar, "Should stay in LookingForNonSpecialChar on special char")
    }

    @Test
    fun `ValidPassword should handle characters correctly`() {
        val state = ValidPassword()
        
        assertTrue(state.consumeCharacter('a') is ValidPassword, "Should stay in ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('A') is ValidPassword, "Should stay in ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('1') is ValidPassword, "Should stay in ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('!') is InvalidPassword, "Should transition to InvalidPassword on special char")
        assertTrue(state.consumeCharacter('@') is InvalidPassword, "Should transition to InvalidPassword on special char")
    }

    @Test
    fun `InvalidPassword should transition back to ValidPassword`() {
        val state = InvalidPassword()
        
        assertTrue(state.consumeCharacter('a') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('A') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('1') is ValidPassword, "Should transition to ValidPassword on non-special char")
        assertTrue(state.consumeCharacter('!') is InvalidPassword, "Should stay in InvalidPassword on special char")
        assertTrue(state.consumeCharacter('@') is InvalidPassword, "Should stay in InvalidPassword on special char")
    }

    @Test
    fun `test complete password parsing sequences`() {
        // Test "aaaaH!aa" (valid)
        var state: State = LookingForAllRequirements()
        
        // Process "aaaa"
        for (i in 1..4) {
            state = state.consumeCharacter('a')
            assertTrue(state is LookingForAllRequirements, "Should stay in LookingForAllRequirements")
        }
        
        // Process "H"
        state = state.consumeCharacter('H')
        assertTrue(state is LookingForSpecialCharacter, "Should be in LookingForSpecialCharacter after capital")
        
        // Process "!"
        state = state.consumeCharacter('!')
        assertTrue(state is LookingForNonSpecialChar, "Should be in LookingForNonSpecialChar after special char")
        
        // Process "aa"
        state = state.consumeCharacter('a')
        assertTrue(state is ValidPassword, "Should be in ValidPassword after non-special char")
        
        state = state.consumeCharacter('a')
        assertTrue(state is ValidPassword, "Should stay in ValidPassword")
    }

    @Test
    fun `test password ending with special character`() {
        // Test "Abbbbbbb!" (invalid - ends with special char)
        var state: State = LookingForAllRequirements()
        
        state = state.consumeCharacter('A')
        assertTrue(state is LookingForSpecialCharacter, "Should be in LookingForSpecialCharacter")
        
        // Process "bbbbbbb"
        for (i in 1..7) {
            state = state.consumeCharacter('b')
            assertTrue(state is LookingForSpecialCharacter, "Should stay in LookingForSpecialCharacter")
        }
        
        // Process "!"
        state = state.consumeCharacter('!')
        assertTrue(state is LookingForNonSpecialChar, "Should be in LookingForNonSpecialChar")
        // This ends in LookingForNonSpecialChar, not ValidPassword, so it's invalid
    }

    @Test
    fun `test all special characters are recognized`() {
        val specialChars = "!@#$%&*"
        val state = LookingForAllRequirements()
        
        for (char in specialChars) {
            val result = state.consumeCharacter(char)
            assertTrue(result is LookingForCapital, "Special character '$char' should be recognized")
        }
    }

    @Test
    fun `test all capital letters are recognized`() {
        val state = LookingForAllRequirements()
        
        for (char in 'A'..'Z') {
            val result = state.consumeCharacter(char)
            assertTrue(result is LookingForSpecialCharacter, "Capital letter '$char' should be recognized")
        }
    }
}

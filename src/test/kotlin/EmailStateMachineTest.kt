package org.example

import org.example.emailState.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

class EmailStateMachineTest {
    
    private lateinit var detector: EmailDetector
    
    @BeforeEach
    fun setUp() {
        detector = EmailDetector()
    }

    @Test
    fun `EmailDetector should accept valid simple email addresses`() {
        val validEmails = listOf("a@b.c", "x@y.z", "1@2.3")
        
        for (email in validEmails) {
            assertTrue(detector.detect(email), "Should accept '$email' as valid email")
        }
    }

    @Test
    fun `EmailDetector should accept valid complex email addresses`() {
        val validEmails = listOf(
            "joseph.ditton@usu.edu",
            "{}*$.&$*(@*$%&.*&*",
            "user123@domain.com",
            "test-email@example.org",
            "complex_email+tag@domain-name.net"
        )
        
        for (email in validEmails) {
            assertTrue(detector.detect(email), "Should accept '$email' as valid email")
        }
    }

    @Test
    fun `EmailDetector should reject emails with empty part1`() {
        val invalidEmails = listOf("@b.c", "@domain.com", "@test.org")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (empty part1)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with empty part2`() {
        val invalidEmails = listOf("a@.c", "user@.com", "test@.org")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (empty part2)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with empty part3`() {
        val invalidEmails = listOf("a@b.", "user@domain.", "test@example.")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (empty part3)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with multiple @ symbols`() {
        val invalidEmails = listOf("a@b@c.com", "user@@domain.com", "test@ex@ample.org")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (multiple @ symbols)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with multiple periods after @`() {
        val invalidEmails = listOf("a.b@b.b.c", "user@domain.sub.com", "test@example.co.uk")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (multiple periods after @)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with space characters`() {
        val invalidEmails = listOf(
            "joseph ditton@usu.edu",
            "a @b.c",
            "a@ b.c",
            "a@b .c",
            "a@b. c",
            "a@b.c ",
            " a@b.c"
        )
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (contains space)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with no @ symbol`() {
        val invalidEmails = listOf("abc.def", "user.domain.com", "nodomain")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (no @ symbol)")
        }
    }

    @Test
    fun `EmailDetector should reject emails with no period after @`() {
        val invalidEmails = listOf("a@b", "user@domain", "test@example")
        
        for (email in invalidEmails) {
            assertFalse(detector.detect(email), "Should reject '$email' (no period after @)")
        }
    }

    @Test
    fun `EmailDetector should reject empty string`() {
        assertFalse(detector.detect(""), "Should reject empty string as invalid email")
    }

    @Test
    fun `EmailDetector state should reset between calls`() {
        // First call with invalid input
        assertFalse(detector.detect("invalid@@email.com"))
        
        // Second call with valid input should work
        assertTrue(detector.detect("valid@email.com"))
        
        // Third call with another valid input
        assertTrue(detector.detect("another@test.org"))
    }

    // Test individual state classes
    @Test
    fun `LookingForFirstCharPartA should handle first character correctly`() {
        val state = LookingForFirstCharPartA()
        
        assertTrue(state.consumeCharacter('a') is WaitingForAtSymbol, "Should transition to WaitingForAtSymbol on valid char")
        assertTrue(state.consumeCharacter('1') is WaitingForAtSymbol, "Should transition to WaitingForAtSymbol on digit")
        assertTrue(state.consumeCharacter('.') is WaitingForAtSymbol, "Should transition to WaitingForAtSymbol on period")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should transition to InvalidEmail on @")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `WaitingForAtSymbol should handle characters correctly`() {
        val state = WaitingForAtSymbol()
        
        assertTrue(state.consumeCharacter('a') is WaitingForAtSymbol, "Should stay in WaitingForAtSymbol on valid char")
        assertTrue(state.consumeCharacter('.') is WaitingForAtSymbol, "Should stay in WaitingForAtSymbol on period")
        assertTrue(state.consumeCharacter('@') is LookingForFirstCharPartB, "Should transition to LookingForFirstCharPartB on @")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `LookingForFirstCharPartB should handle first character after @ correctly`() {
        val state = LookingForFirstCharPartB()
        
        assertTrue(state.consumeCharacter('a') is WaitingForPeriod, "Should transition to WaitingForPeriod on valid char")
        assertTrue(state.consumeCharacter('1') is WaitingForPeriod, "Should transition to WaitingForPeriod on digit")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should transition to InvalidEmail on @")
        assertTrue(state.consumeCharacter('.') is InvalidEmail, "Should transition to InvalidEmail on period")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `WaitingForPeriod should handle characters correctly`() {
        val state = WaitingForPeriod()
        
        assertTrue(state.consumeCharacter('a') is WaitingForPeriod, "Should stay in WaitingForPeriod on valid char")
        assertTrue(state.consumeCharacter('1') is WaitingForPeriod, "Should stay in WaitingForPeriod on digit")
        assertTrue(state.consumeCharacter('.') is LookingForFirstCharPartC, "Should transition to LookingForFirstCharPartC on period")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should transition to InvalidEmail on @")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `LookingForFirstCharPartC should handle first character after period correctly`() {
        val state = LookingForFirstCharPartC()
        
        assertTrue(state.consumeCharacter('a') is ValidEmail, "Should transition to ValidEmail on valid char")
        assertTrue(state.consumeCharacter('1') is ValidEmail, "Should transition to ValidEmail on digit")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should transition to InvalidEmail on @")
        assertTrue(state.consumeCharacter('.') is InvalidEmail, "Should transition to InvalidEmail on period")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `ValidEmail should handle additional characters correctly`() {
        val state = ValidEmail()
        
        assertTrue(state.consumeCharacter('a') is ValidEmail, "Should stay in ValidEmail on valid char")
        assertTrue(state.consumeCharacter('1') is ValidEmail, "Should stay in ValidEmail on digit")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should transition to InvalidEmail on @")
        assertTrue(state.consumeCharacter('.') is InvalidEmail, "Should transition to InvalidEmail on period")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should transition to InvalidEmail on space")
    }

    @Test
    fun `InvalidEmail should always stay InvalidEmail`() {
        val state = InvalidEmail()
        
        assertTrue(state.consumeCharacter('a') is InvalidEmail, "Should stay in InvalidEmail")
        assertTrue(state.consumeCharacter('@') is InvalidEmail, "Should stay in InvalidEmail")
        assertTrue(state.consumeCharacter('.') is InvalidEmail, "Should stay in InvalidEmail")
        assertTrue(state.consumeCharacter(' ') is InvalidEmail, "Should stay in InvalidEmail")
        assertSame(state, state.consumeCharacter('x'), "Should return same InvalidEmail instance")
    }

    @Test
    fun `test complete email parsing sequences`() {
        // Test "a@b.c"
        var state: State = LookingForFirstCharPartA()
        state = state.consumeCharacter('a')
        assertTrue(state is WaitingForAtSymbol, "Should be in WaitingForAtSymbol after 'a'")
        
        state = state.consumeCharacter('@')
        assertTrue(state is LookingForFirstCharPartB, "Should be in LookingForFirstCharPartB after '@'")
        
        state = state.consumeCharacter('b')
        assertTrue(state is WaitingForPeriod, "Should be in WaitingForPeriod after 'b'")
        
        state = state.consumeCharacter('.')
        assertTrue(state is LookingForFirstCharPartC, "Should be in LookingForFirstCharPartC after '.'")
        
        state = state.consumeCharacter('c')
        assertTrue(state is ValidEmail, "Should be in ValidEmail after 'c'")
    }

    @Test
    fun `test special characters in email parts`() {
        val specialChars = listOf('!', '#', '$', '%', '&', '*', '+', '-', '/', '=', '?', '^', '_', '`', '{', '|', '}', '~')
        
        for (char in specialChars) {
            val email = "test${char}user@domain${char}name.co${char}m"
            assertTrue(detector.detect(email), "Should accept email with special character '$char'")
        }
    }
}

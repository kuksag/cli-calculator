import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ParserTest {
    companion object {
        @JvmStatic
        fun integersToParse() = listOf(
            Arguments.of("42", 42),
            Arguments.of("+123", 123),
            Arguments.of("456", 456),
            Arguments.of("0", 0),
            Arguments.of("12a3", 12),
        )

        @JvmStatic
        fun simpleBrackets() = listOf(
            Arguments.of("(42)", 42),
            Arguments.of("(+123)", 123),
            Arguments.of("(((456)))", 456),
            Arguments.of("    ( ( (1         ) )                               )", 1)
        )

        @JvmStatic
        fun plusMinus() = listOf(
            Arguments.of("10 + 32", 42),
            Arguments.of("10 + +32", 42),
            Arguments.of("10 - 10", 0),
            Arguments.of("10 + -10", 0),
            Arguments.of("-10 + -10", -20),
            Arguments.of("+5++6++7", 18),
        )

        @JvmStatic
        fun multiplyDivide() = listOf(
            Arguments.of("10 * 32", 320),
            Arguments.of("10 * -32", -320),
            Arguments.of("10 * 0", 0),
            Arguments.of("10 * -0", 0),
            Arguments.of("-10 * -10", 100),
            Arguments.of("2*2*3/4", 3),
        )

        @JvmStatic
        fun everyOpWithBrackets() = listOf(
            Arguments.of("(3+4)*5", 35),
            Arguments.of("(3+5)*((100 + (4 * 5)) / 60)", 16),
            Arguments.of("1 + (2 * (3 - (4 * (5 / 5))))", -1),
        )

        @JvmStatic
        fun variables() = listOf(
            Arguments.of("let x = 1", 1),
            Arguments.of("let x = 2 + 3", 5),
            Arguments.of("let x = (3+5)*((100 + (4 * 5)) / 60)", 16),
            Arguments.of("let x = 1 + (2 * (3 - (4 * (5 / 5))))", -1),
        )
    }

    @ParameterizedTest
    @MethodSource("integersToParse")
    fun `parse integers`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(expected, parser.parse(data))
    }

    @ParameterizedTest
    @MethodSource("simpleBrackets")
    fun `simple brackets`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(expected, parser.parse(data))
    }

    @ParameterizedTest
    @MethodSource("plusMinus")
    fun `plus, minus`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(expected, parser.parse(data))
    }

    @ParameterizedTest
    @MethodSource("multiplyDivide")
    fun `multiply, divide`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(expected, parser.parse(data))
    }

    @ParameterizedTest
    @MethodSource("everyOpWithBrackets")
    fun `everything with brackets`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(expected, parser.parse(data))
    }

    @ParameterizedTest
    @MethodSource("variables")
    fun `variables assigment`(data: String, expected: Int) {
        val parser = Parser()
        assertEquals(null, parser.parse(data))
        assertEquals(expected, parser.variables["x"])
    }

    @Test
    fun `variable reassignment`() {
        val parser = Parser()
        assertEquals(null, parser.parse("let x = 1"))
        assertEquals(null, parser.parse("let x = 2"))
        assertEquals(2, parser.variables["x"])
    }

    @Test
    fun `variable usage`() {
        val parser = Parser()
        assertEquals(null, parser.parse("let x = (3+4)*5"))
        assertEquals(null, parser.parse("let y = (3+5)*((100 + (4 * 5)) / 60)"))
        assertEquals(null, parser.parse("let z = 1 + (2 * (3 - (4 * (5 / 5))))"))
        assertEquals(null, parser.parse("let w = x * z * w"))
        assertEquals(-35, parser.parse("w"))
    }
}

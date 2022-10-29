// Recursive descent parser implementation
class Parser {
    /*
     * Grammar:
     * <expr-1> = at least one <expr-2> separated by {+, -}
     * <expr-2> = at least one <atom> separated by {*, /}
     * <atom> = (<expr-1) | <number>
     *
     * Variable assignment:
     * let <charSequence> = <atom>
     */

    private var data: String = ""
    private var pos: Int = 0
    var variables: MutableMap<String, Int> = mutableMapOf()

    companion object {
        const val PLUS = '+'
        const val MINUS = '-'

        const val LET = "let"

        const val EQUAL = '='

        const val OPEN_BRACKET = '('
        const val CLOSE_BRACKET = ')'

        val PRIORITY = mapOf(
            '+' to 1,
            '-' to 1,
            '/' to 2,
            '*' to 2,
        )

        const val HIGHEST_PRIORITY = 3
    }

    private fun isUnarySign(char: Char): Boolean {
        return char == PLUS || char == MINUS
    }

    private fun skipSpaces() {
        while (pos < data.length && data[pos].isWhitespace()) {
            pos++
        }
    }

    private fun parseInt(): Int {
        check(pos < data.length)
        val start = pos
        if (isUnarySign(data[pos])) {
            pos++
        }
        while (pos < data.length && data[pos].isDigit()) {
            pos++
        }
        return data.substring(start, pos).toInt()
    }

    private fun handleAssignment(): Pair<String, Int> {
        check(data.startsWith(LET, pos))
        pos += LET.length
        skipSpaces()
        val name = StringBuilder()
        while (pos < data.length && data[pos].isLetter()) {
            name.append(data[pos])
            pos++
        }
        skipSpaces()
        check(data[pos] == EQUAL) { "Expected '=' char" }
        pos++
        val value = calculateExpression()
        return name.toString() to value
    }

    private fun parseVariable(): Int {
        check(pos < data.length)
        val name = StringBuilder()
        while (pos < data.length && data[pos].isLetter()) {
            name.append(data[pos])
            pos++
        }
        return variables.getOrElse(name.toString()) {
            pos -= name.length
            error("There is no such variable")
        }
    }

    private fun calculateAtom(): Int {
        skipSpaces()
        check(pos < data.length)
        if (data[pos].isDigit() || isUnarySign(data[pos])) {
            return parseInt()
        }
        if (data[pos].isLetter()) {
            return parseVariable()
        }
        if (data[pos] == OPEN_BRACKET) {
            pos++
            val result = calculateExpression()
            check(pos < data.length && data[pos] == CLOSE_BRACKET) { "Expected ')' char" }
            pos++
            return result
        }
        error("Unexpected char")
    }

    private fun calculateExpression(priority: Int = 1): Int {
        if (priority == HIGHEST_PRIORITY) {
            return calculateAtom()
        }
        var lhs = calculateExpression(priority + 1)

        skipSpaces()
        while (pos < data.length && PRIORITY[data[pos]] == priority) {
            val operator = data[pos]
            pos++
            val rhs = calculateExpression(priority + 1)
            when (operator) {
                '+' -> {
                    lhs += rhs
                }

                '-' -> {
                    lhs -= rhs
                }

                '*' -> {
                    lhs *= rhs
                }

                '/' -> {
                    lhs /= rhs
                }
            }
        }

        return lhs
    }

    fun parse(data: String): Int? {
        this.data = data
        this.pos = 0
        try {
            skipSpaces()
            val result: Int?
            if (data.startsWith(LET, pos)) {
                val (name, value) = handleAssignment()
                variables[name] = value
            }
            result = calculateExpression()
            check(pos == data.length) { "Unexpected input" }
            return result
        } catch (e: Exception) {
            error(
                """
                $e\n
                    $data
                    ${" ".repeat(pos)}^
            """.trimIndent()
            )
        }
    }

}

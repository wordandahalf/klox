package scanner

import scanner.Token.Type.*
import exceptions.ScanningException
import exceptions.UnterminatedBlockCommentException
import exceptions.UnterminatedStringException

data class Scanner(
    val source: String,
) {
    private var start = 0
    private var current = 0
    private var line = 1

    private val tokens = arrayListOf<Token<*>>()
    private val finishedScanning: Boolean
        get() = current >= source.length

    private fun advance() = source[current++]
    private fun peek() = if (finishedScanning) 0.toChar() else source[current]
    private fun peekNext() =
        if (current + 1 >= source.length) 0.toChar()
        else source[current + 1]

    private fun match(expected: Char): Boolean {
        if (finishedScanning) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun addTokenIfNext(next: Char, yes: Token.Type, no: Token.Type) {
        if (match(next))
            addToken(yes)
        else
            addToken(no)
    }

    private fun addToken(type: Token.Type) { addToken(type, Unit) }

    private fun <T: Any> addToken(type: Token.Type, value: T) {
        tokens.add(
            Token(
                type,
                source.substring(start, current),
                value,
                line
            )
        )
    }

    fun scanTokens(): List<Token<*>> {
        while(!finishedScanning) {
            // We are at the beginning of the next lexeme
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", Unit, line))
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addTokenIfNext('=', BANG_EQUAL, BANG)
            '=' -> addTokenIfNext('=', EQUAL_EQUAL, EQUAL)
            '<' -> addTokenIfNext('=', LESS_EQUAL, LESS)
            '>' -> addTokenIfNext('=', GREATER_EQUAL, GREATER)
            '/' -> {
                // If we find a comment, discard the remaining text on the line
                if (match('/'))
                    while (!finishedScanning && peek() != '\n') advance()
                // If we find a block comment, discard everything until the closing
                // delimiter is found
                else if (match('*')) {
                    while (!finishedScanning && peek() != '*' && peekNext() != '/')
                        advance()

                    // Consume the closing delimiter, ensuring it was found
                    if (current < source.length - 1 && peek() == '*'  && peekNext() == '/')
                        current += 2
                    else
                    // Otherwise, indicate the comment was never closed.
                        throw UnterminatedBlockCommentException(line)
                } else
                    addToken(SLASH)
            }
            ' ', '\r', '\t' -> {}
            '\n' -> line++
            '"' -> string()
            else -> {
                when {
                    c.isDigit() -> number()
                    c.isLetter() || c == '_' -> identifier()
                    else -> throw ScanningException(line, c)
                }
            }
        }
    }

    private fun number() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        // Consume characters until we hit a terminating double quote,
        // or we run out of characters. String literals continue across newlines.
        while (peek() != '"' && !finishedScanning) {
            if (peek() == '\n') line++
            advance()
        }

        // If we run out of characters, raise an exception.
        if (finishedScanning)
            throw UnterminatedStringException(line)

        // Otherwise, consume the closing double quote.
        advance()

        // Add the literal token. TODO: add escape sequences.
        addToken(STRING, source.substring(start + 1, current - 1))
    }

    private fun identifier() {
        while (peek().isLetterOrDigit()) advance()

        val text = source.substring(start, current)

        if (text in KEYWORDS)
            addToken(KEYWORDS.getValue(text))
        else
            addToken(IDENTIFIER, text)
    }

    fun getTokens(): List<Token<*>> = tokens

    companion object {
        private val KEYWORDS =
            Token.Type.entries.filter { it.keyword }.map { it.name.lowercase() to it }
                .toMap()
    }
}
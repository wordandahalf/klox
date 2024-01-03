package scanner

data class Token<T: Any>(
    val type: Type,
    val lexeme: String,
    val value:  T,
    val line:   Int
) {
    enum class Type(val keyword: Boolean = false) {
        // Single-character tokens
        LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
        COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

        // One- or two-character tokens
        BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL,
        GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

        // Literals
        IDENTIFIER, STRING, NUMBER,

        // Keywords
        AND(true), CLASS(true), ELSE(true), FALSE(true),
        FUN(true), FOR(true), IF(true), NIL(true), OR(true),
        PRINT(true), RETURN(true), SUPER(true), THIS(true),
        TRUE(true), VAR(true), WHILE(true),

        EOF
    }

    override fun toString() =
        "[$line] $type '$lexeme' ($value)"
}
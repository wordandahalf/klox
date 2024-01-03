import exceptions.*
import scanner.Scanner
import scanner.Token
import scanner.Token.Type.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class Scanner {
    private fun tokenize(input: String) =
        Scanner(input).scanTokens()

    private fun Token(type: Token.Type, lexeme: String, line: Int) =
        Token(type, lexeme, Unit, line)

    @Test
    fun testScannerBasic() {
        val tokens = tokenize("var x = 1.0;")

        assertContentEquals(tokens,
            listOf(
                Token(VAR, "var", 1),
                Token(IDENTIFIER, "x", "x", 1),
                Token(EQUAL, "=", 1),
                Token(NUMBER, "1.0", 1.0, 1),
                Token(SEMICOLON, ";", 1),
                Token(EOF, "", 1)
            )
        )
    }

    @Test
    fun testScannerUnterminatedString() {
        assertFailsWith<UnterminatedStringException> {
            tokenize("var str = \"Hello, world!;")
        }
    }

    @Test
    fun testScannerMultilineString() {
        val content = """Hello,
            |world!
        """.trimMargin()
        val tokens = tokenize("var helloWorld = \"$content\";")

        assertContentEquals(tokens,
            listOf(
                Token(VAR, "var", 1),
                Token(IDENTIFIER, "helloWorld", "helloWorld", 1),
                Token(EQUAL, "=", 1),
                Token(STRING, "\"$content\"", content, 2),
                Token(SEMICOLON, ";", 2),
                Token(EOF, "", 2)
            )
        )
    }

    @Test
    fun testScannerComment() {
        val tokens = tokenize("// var average = (max + min) / 2.0;")

        assertContentEquals(tokens, listOf(Token(EOF, "", 1)))
    }

    @Test
    fun testScannerBlockComment() {
        val code = """
            /*
            This code will print "Hello, world!" to the console.
            */
            print "Hello, world"
        """.trimIndent()

        assertContentEquals(tokenize(code),
            listOf(
                Token(PRINT, "print", 2),
                Token(STRING, "\"Hello, world\"", "Hello, world", 2),
                Token(EOF, "", 2)
            )
        )
    }

    @Test
    fun testScannerUnterminatedBlockComment() {
        val code = """
            /*
            This code will print "Hello, world!" to the console.
            * /
            print "Hello, world"
        """.trimIndent()

        assertFailsWith<UnterminatedBlockCommentException> {
            tokenize(code).also { println(it) }
        }
    }
}
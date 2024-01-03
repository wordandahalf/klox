package exceptions

class UnterminatedBlockCommentException(line: Int) : LoxException("Unterminated block comment on line $line")
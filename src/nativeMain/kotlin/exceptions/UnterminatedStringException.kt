package exceptions

class UnterminatedStringException(line: Int) : LoxException("Unterminated string on line $line")
package exceptions

class ScanningException(line: Int, char: Char) :
    LoxException("Unexpected character '$char' on line $line")
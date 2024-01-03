import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fprintf
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
fun report(line: Int, where: String, message: String) {
    fprintf(stderr, "[line $line] Error$where: $message")
}

fun error(line: Int, message: String) {
    report(line, "", message)
}

@OptIn(ExperimentalForeignApi::class)
fun error(message: String) {
    fprintf(stderr, message)
}
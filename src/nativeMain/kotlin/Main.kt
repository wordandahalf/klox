import exceptions.LoxException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.exit

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: klox [script]")
        exit(64)
    } else if (args.size == 1) {
        runFile(args[0].toPath())
    } else {
        runRepl()
    }
}

fun runFile(file: Path) {
    run(FileSystem.SYSTEM.read(file) { readUtf8() })
}

fun runRepl() {
    while(true) {
        print("> ")
        run(readlnOrNull() ?: return)
    }
}

fun run(source: String) {
    try {
        val scanner = scanner.Scanner(source)
        val tokens = scanner.scanTokens()

        println(tokens.joinToString(separator = "\n"))
    } catch (e: LoxException) {
        // todo: report error
        error(e.message ?: "Unknown error")
        exit(65)
    }
}
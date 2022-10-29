fun main() {
    val parser = Parser()
    while (true) {
        print(">>> ")
        val data = readLine() ?: break
        try {
            parser.parse(data)?.let {
                println(it)
            }
        } catch (e: Exception) {
            println(e)
        }
    }
}

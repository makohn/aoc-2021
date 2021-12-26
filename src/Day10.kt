fun main() {

    val day = "10"

    val open = listOf('(','[','{','<')

    fun part1(input: List<String>): Int {
        var sum = 0
        for (line in input) {
            val stack = mutableListOf<Char>()
            for (ch in line) {
                val ret = when(ch) {
                    in open -> stack.add(0, ch).let { ch }
                    '}' -> if (stack.indexOfFirst{ it == '{' } == 0) stack.removeFirst() else ch
                    ')' -> if (stack.indexOfFirst{ it == '(' } == 0) stack.removeFirst() else ch
                    ']' -> if (stack.indexOfFirst{ it == '[' } == 0) stack.removeFirst() else ch
                    '>' -> if (stack.indexOfFirst{ it == '<' } == 0) stack.removeFirst() else ch
                    else -> error("Impossible")
                }
                sum += when(ret) {
                    '}' -> 1197
                    ')' -> 3
                    ']' -> 57
                    '>' -> 25137
                    else -> 0
                }
                if (ret !in open) break
            }
        }
        return sum
    }

    fun part2(input: List<String>): Long {
        val scores = mutableListOf<Long>()
        score@for (line in input) {
            var sum = 0L
            val stack = mutableListOf<Char>()
            for (ch in line) {
                val ret = when(ch) {
                    in open -> stack.add(0, ch).let { ch }
                    '}' -> if (stack.indexOfFirst{ it == '{' } == 0) stack.removeFirst() else ch
                    ')' -> if (stack.indexOfFirst{ it == '(' } == 0) stack.removeFirst() else ch
                    ']' -> if (stack.indexOfFirst{ it == '[' } == 0) stack.removeFirst() else ch
                    '>' -> if (stack.indexOfFirst{ it == '<' } == 0) stack.removeFirst() else ch
                    else -> error("Impossible")
                }
                if (ret !in open) continue@score
            }
            sum += stack.fold(0L) { acc, e -> 5 * acc + (open.indexOf(e) + 1) }
            scores += sum
        }
        return scores.sorted()[scores.size/2]
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
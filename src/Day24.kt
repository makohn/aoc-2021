fun main() {

    val day = "24"

    fun part1(input: List<String>): Long {
        val number = IntArray(14)
        val stack = mutableListOf<Pair<Int, Int>>()
        val instructions = input.map { it.split(" ") }
        for (i in 0 until 14) {
            val x = instructions[18*i + 5][2].toInt()
            val y = instructions[18*i + 15][2].toInt()
            if (x > 0) {
                stack.add(0, (y to i))
            } else {
                val (y1, pos) = stack.removeFirst()
                var j = 9
                while (j + y1 + x > 9) j--
                number[pos] = j
                number[i] = j + y1 + x
            }
        }
        return number.joinToString("").toLong()
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
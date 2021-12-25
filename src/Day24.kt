fun main() {

    val day = "24"

    fun part1(input: List<String>): Long {
        return findSerialNumber(input)
    }

    fun part2(input: List<String>): Long {
        return findSerialNumber(input, smallest = true)
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun findSerialNumber(input: List<String>, smallest: Boolean = false): Long {
    val number = IntArray(14)
    val currentNumbers = mutableListOf<Pair<Int, Int>>()
    val instructions = input.map { it.split(" ") }
    for (i in 0 until 14) {
        val x = instructions[18*i + 5][2].toInt()
        val y = instructions[18*i + 15][2].toInt()
        if (x > 0) {
            currentNumbers.add(0, (y to i))
        } else {
            val (y1, pos) = currentNumbers.removeFirst()
            var j = if (smallest) 1 else 9
            if (smallest) {
                while (j + y1 + x < 1) j++
            } else {
                while (j + y1 + x > 9) j--
            }
            number[pos] = j
            number[i] = j + y1 + x
        }
    }
    return number.joinToString("").toLong()
}
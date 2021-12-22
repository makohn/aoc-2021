fun main() {

    val day = "22"

    fun part1(input: List<String>): Int {
        val steps = parse(input).filter { it.second.none { v -> ((v < -50) || (v > 50)) } }
        return reboot(steps).count()
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 590784)
//    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
//    println(part2(input))
}

operator fun <T> List<T>.component6(): T = this[5]

private fun parse(input: List<String>): List<Pair<Int, List<Int>>> {
    val coords = input.map { "(-?\\d)+".toRegex().findAll(it).map { s -> s.value.toInt() }.toList() }
    val states = input.map { "(on|off)".toRegex().findAll(it).map { s -> 1 - (s.value.length - 2) }.first() }
    return states.zip(coords)
}

private fun reboot(steps: List<Pair<Int, List<Int>>>): Set<Triple<Int, Int, Int>> {
    val cuboids = mutableSetOf<Triple<Int, Int, Int>>()
    for (step in steps) {
        val (x1, x2, y1, y2, z1, z2) = step.second
        for (x in (x1 .. x2)) {
            for (y in (y1 .. y2)) {
                for (z in (z1 .. z2)) {
                    if (step.first == 1) {
                        cuboids.add(Triple(x, y, z))
                    } else {
                        cuboids.remove(Triple(x, y, z))
                    }
                }
            }
        }
    }
    return cuboids
}
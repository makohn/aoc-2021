import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val area = parse(input.first())
        val (maxY, _) = simulate(area)
        return maxY
    }

    fun part2(input: List<String>): Int {
        val area = parse(input.first())
        val (_, count) = simulate(area)
        return count
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}

private fun parse(input: String): Quadruple<Int> {
    val (minX, maxX, minY, maxY) = "(-?\\d+)".toRegex().findAll(input).map { it.value.toInt() }.toList()
    return Quadruple(minX, maxX, minY, maxY)
}

private fun simulate(area: Quadruple<Int>): Pair<Int, Int> {
    var yMax = 0
    var count = 0
    val (_, maxX, minY, maxY) = area
    val yRange = maxOf(abs(minY), abs(maxY))
    for (vx in 0 .. maxX) {
        for (vy in -yRange .. yRange) {
            val res = shot(vx, vy, area)
            if (res >= 0) count++
            if (res > yMax) yMax = res
        }
    }
    return yMax to count
}

private fun shot(vx: Int, vy: Int, area: Quadruple<Int>): Int {
    val (minX, maxX, minY, maxY) = area
    var state = Quadruple(0, 0, vx, vy)
    var yMax = 0
    while (true) {
        state = step(state)
        val (x, y, svx, _) = state
        if (y > yMax) yMax = y
        if (x > maxX) return -1
        if (svx == 0 && x !in (minX .. maxX)) return -1
        if (svx == 0 && y < minY) return -1
        if ((x in (minX .. maxX)) && y in (minY .. maxY)) return yMax
    }
}

private fun step(state: Quadruple<Int>): Quadruple<Int> {
    var (x, y, vx, vy) = state
    x += vx
    y += vy
    if (vx > 0) vx-- else if (vx < 0) vx++
    vy--
    return Quadruple(x, y, vx, vy)
}
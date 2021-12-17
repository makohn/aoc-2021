fun main() {
    fun part1(input: List<String>): Int {
        Day17Helper.parse(input.first())
        val (maxY, _) = Day17Helper.simulate()
        return maxY
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
//    check(part2(testInput) == 3)

    val input = readInput("Day17")
    println(part1(input))
//    println(part2(input))
}

data class Vector(val x: Int, val y: Int, val vx: Int, val vy: Int)

object Day17Helper {

    private var minX = 0
    private var maxX = 0
    private var minY = 0
    private var maxY = 0

    private const val maxTries = 1000

    fun parse(input: String) {
        val res = "(-?\\d+)".toRegex().findAll(input).map { it.value.toInt() }.toMutableList()
        minX = res.removeFirst()
        maxX = res.removeFirst()
        minY = res.removeFirst()
        maxY = res.removeFirst()
    }

    fun simulate(): Pair<Int, Int> {
        var mY = 0
        var count = 0
        for (vy in -maxTries .. maxTries) {
            var vx = 1
            while (true) {
                val res = shot(vx, vy)
                if (res >= 0) count++
                if (res > mY) mY = res
                if (res == -3) break
                if (vx >= maxTries) break
                vx++
            }
        }
        return mY to count
    }

    private fun shot(vx: Int, vy: Int): Int {
        var vec = Vector(0, 0, vx, vy)
        var mY = 0
        while (true) {
            vec = step(vec)
            val (x, y, _, _) = vec
            if (y > mY) mY = y
            if ((x <= maxX) && (x >= minX) && (y <= maxY) && (y >= minY)) return mY
            if ((y <= maxY) and (x > maxX)) return -3
            if (y < minY)
                return if (x < minX) -1
                else -2
        }
    }

    private fun step(vec: Vector): Vector {
        var (x, y, vx, vy) = vec
        x += vx
        y += vy
        vx = if (vx > 0) vx - 1 else if (vx < 0) vx + 1 else vx
        vy -= 1
        return Vector(x, y, vx, vy)
    }
}

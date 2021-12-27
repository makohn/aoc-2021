fun main() {

    val day = "11"

    fun part1(input: List<String>): Int {
        val grid = input.map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
        val n = grid.size
        val m = grid[0].size
        var flashes = 0
        repeat(100) { flashes += run(grid, n, m).size }
        return flashes
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
        val n = grid.size
        val m = grid[0].size
        var i = 0
        while(true) {
            i++
            val flashed = run(grid, n, m).size
            if (flashed == (n*m)) return i
        }
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun run(grid: Array<IntArray>, n: Int, m: Int): MutableSet<Pair<Int, Int>> {
    val flashed = mutableSetOf<Pair<Int, Int>>()
    for (i in (0 until n)) for (j in (0 until m)) {
        grid[i][j] += 1
    }
    for (i in (0 until n)) for (j in (0 until m)) {
        if (grid[i][j] > 9) flashed += flash(i, j, grid, flashed)
    }
    return flashed
}

private fun flash(i: Int, j: Int, grid: Array<IntArray>,
                  flashed: MutableSet<Pair<Int, Int>>): MutableSet<Pair<Int, Int>> {
    if ((i to j) in flashed) return flashed
    flashed.add(i to j)
    grid[i][j] = 0
    for (k in -1..1) for (l in -1 .. 1) {
        if (k == 0 && l == 0) continue
        val ii = i + k
        val jj = j + l
        if (ii in 0..grid.lastIndex && jj in 0 .. grid[0].lastIndex) {
            if ((ii to jj) !in flashed) grid[ii][jj] += 1
            if (grid[ii][jj] > 9) flash(ii, jj, grid, flashed)
        }
    }
    return flashed
}
fun main() {

    val day = "09"

    fun part1(input: List<String>): Int {
        val grid = input.map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
        var sum = 0
        for (i in grid.indices) for (j in grid[0].indices) {
            val up = if (i > 0) grid[i-1][j] else Int.MAX_VALUE
            val down = if (i < grid.lastIndex) grid[i+1][j] else Int.MAX_VALUE
            val left = if (j > 0) grid[i][j-1] else Int.MAX_VALUE
            val right = if (j < grid[0].lastIndex) grid[i][j+1] else Int.MAX_VALUE
            if (grid[i][j] < minOf(up, down, left, right)) sum += grid[i][j] + 1
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val grid = input.map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()
        val sum = mutableListOf<Int>()
        for (i in grid.indices) for (j in grid[0].indices) {
            val up = if (i > 0) grid[i-1][j] else Int.MAX_VALUE
            val down = if (i < grid.lastIndex) grid[i+1][j] else Int.MAX_VALUE
            val left = if (j > 0) grid[i][j-1] else Int.MAX_VALUE
            val right = if (j < grid[0].lastIndex) grid[i][j+1] else Int.MAX_VALUE
            if (grid[i][j] < minOf(up, down, left, right)) sum += getBasin(i, j, grid).size
        }
        return sum.sorted().takeLast(3).reduce { a, b -> a * b }
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

private fun getBasin(i: Int, j: Int, grid: Array<IntArray>,
                     visited: MutableSet<Pair<Int, Int>> = mutableSetOf())
: MutableSet<Pair<Int, Int>> {
    if (i !in (0 .. grid.lastIndex) ||
        j !in (0 .. grid[0].lastIndex) ||
        grid[i][j] == 9 || (i to j) in visited) {
        return visited
    }
    visited.add((i to j))
    getBasin(i-1, j, grid, visited)
    getBasin(i+1, j, grid, visited)
    getBasin(i, j-1, grid, visited)
    getBasin(i, j+1, grid, visited)
    return visited
}
import java.util.*

fun main() {

    fun part1(input: List<String>): Int {
        val board = parse(input)
        val n = board.size
        val m = board[0].size
        return solve(board, n, m)
    }

    fun part2(input: List<String>): Int {
        val board = parse(input)
        val n = board.size
        val m = board[0].size
        val map = Array(n*5) { IntArray(m*5) }
        for (i in board.indices) {
            for (j in board[i].indices) {
                for (k in 0..4) {
                    val l = j + k*m
                    map[i][l] = (((board[i][j] + 1*k -1)) % 9) + 1
                }
            }
            for (x in 1..4) {
                map[i + x*n] = map[i].map { ((it + x*1 -1)) % 9 + 1 }.toIntArray()
            }
        }
        return solve(map, n*5, m*5)
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

data class Tuple(val cost: Int, val pos: Pos)
typealias Pos = Pair<Int, Int>

private fun parse(input: List<String>) =
    input.map { it.map { c -> c.digitToInt() }.toIntArray() }.toTypedArray()

private fun solve(board: Array<IntArray>, n: Int, m: Int): Int {

    fun adjacent(u: Pos) = sequence {
        val (i, j) = u
        for ((ii, jj) in listOf(Pos(i-1, j), Pos(i+1, j), Pos(i, j-1), Pos(i, j+1))) {
            if ((0 <= ii) && (ii < m) && (0 <= jj) && (jj < n)) {
                yield(Pos(ii, jj) to board[ii][jj])
            }
        }
    }

    return dijkstra(::adjacent, Pos(0, 0), Pos(m-1, n-1))
}

private fun dijkstra(adjacent: (Pos) -> Sequence<Pair<Pos, Int>>, start: Pos, end: Pos): Int {
    val distances = mutableMapOf(start to 0)

    val queue = PriorityQueue(compareBy(Tuple::cost))
    queue.add(Tuple(0, start))

    while (queue.isNotEmpty()) {
        val (cost, u) = queue.remove()
        if (cost <= distances[u]!!) {
           for ((v, weight) in adjacent(u)) {
              val alt = cost + weight
               if (!distances.contains(v) || (alt < distances[v]!!)) {
                   distances[v] = alt
                   queue.add(Tuple(alt, v))
               }
           }
        }
    }
    return distances[end]!!
}

import java.util.*

fun main() {

    fun part1(input: List<String>): Int {
        val (board, m, n) = Day15Helper.parse(input)

        fun adjacent(u: Pos) = sequence {
            val (i, j) = u
            for ((ii, jj) in listOf(Pos(i-1, j), Pos(i+1, j), Pos(i, j-1), Pos(i, j+1))) {
                if ((0 <= ii) && (ii < m) && (0 <= jj) && (jj < n)) {
                    yield(Pos(ii, jj) to board[ii][jj])
                }
            }
        }

        return Day15Helper.dijkstra(::adjacent, Pos(0, 0), Pos(m-1, n-1))
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
//    check(part2(testInput) == 2)

    val input = readInput("Day15")
    println(part1(input))
//    println(part2(input))
}

data class Tuple<T: Comparable<T>, V>(val first: T, val second: V): Comparable<Tuple<T, V>> {
    override fun compareTo(other: Tuple<T, V>): Int = this.first.compareTo(other.first)
}

typealias Heap<T, V> = PriorityQueue<Tuple<T, V>>
typealias Pos = Pair<Int, Int>

object Day15Helper {

    fun parse(input: List<String>): Triple<Array<IntArray>, Int, Int> {
        val board = Array(input.size) { IntArray(input.first().length) }
        (board.indices).forEach { i ->
            (board[i].indices).forEach { j ->
                board[i][j] = input[i][j].digitToInt()
            }
        }
        return Triple(board, board.size, board[0].size)
    }

    fun dijkstra(adjacent: (Pos) -> Sequence<Pair<Pos, Int>>, start: Pos, end: Pos): Int {
        val distances = mutableMapOf(start to 0)

        val heap = Heap<Int, Pos>().apply { add(Tuple(0, start)) }

        while (heap.isNotEmpty()) {
            val (cost, u) = heap.remove()
            if (cost <= distances[u]!!) {
               for ((v, weight) in adjacent(u)) {
                  val alt = cost + weight
                   if (!distances.contains(v) || (alt < distances[v]!!)) {
                       distances[v] = alt
                       heap.add(Tuple(alt, v))
                   }
               }
            }
        }
        return distances[end]!!
    }
}

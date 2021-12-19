import Day19.getBeaconCount
import Day19.parse
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {

    val day = "19"

    fun part1(input: List<String>): Int {
        val points = parse(input).values.toMutableList()
        return getBeaconCount(points)
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

typealias Point = Triple<Double, Double, Double>

operator fun Point.minus(other: Point) = Point(
    first - other.first,
    second - other.second,
    third - other.third)

operator fun Point.plus(other: Point) = Point(
    first + other.first,
    second + other.second,
    third + other.third)

object Day19 {

    fun parse(input: List<String>): Map<Int, List<Point>> {
        val scanners = mutableMapOf<Int, MutableList<Point>>()
        var current = 0
        input.filter { it.isNotEmpty() }.forEach { line ->
            if (line.startsWith("--")) {
                val scanner = line.replace("[^\\d]".toRegex(), "").toInt()
                scanners.putIfAbsent(scanner, mutableListOf())
                current = scanner
            } else {
                val (x, y, z) = line.split(",").map { it.toDouble() }
                scanners[current]?.add(Triple(x, y, z))
            }
        }
        return scanners
    }

    fun getBeaconCount(points: MutableList<List<Point>>): Int {
        for (i in (0 .. points.lastIndex)) {
            val scanner = points[i]
            for (j in (i + 1 .. points.lastIndex)) {
                val comparedScanner = points[j].toMutableList()
                val overlaps = getOverlaps(scanner, comparedScanner)
                if (overlaps.isNotEmpty()) {
                    comparedScanner.removeAll(overlaps.keys)
                    points[j] = comparedScanner
                }
            }
        }
        return points.flatten().count()
    }

    private fun dist(p1: Point, p2: Point): Double {
        return sqrt((p2.first - p1.first).pow(2)    +
                       (p2.second - p1.second).pow(2)  +
                       (p2.third - p1.third).pow(2))
    }

    private fun getDistanceMap(list: List<Point>): Map<Double, Pair<Point, Point>> {
        val distances = mutableMapOf<Double, Pair<Point, Point>>()
        for (i in (list.indices)) {
            for (j in (i + 1 .. list.lastIndex)) {
                val p1 = list[i]
                val p2 = list[j]
                val distance = dist(p1, p2)
                distances.putIfAbsent(distance, p1 to p2 )
            }
        }
        return distances
    }

    private fun getOverlaps(l1: List<Point>, l2: List<Point>): Map<Point, Point> {
        val overlaps = mutableMapOf<Point, Point>()
        val distances = getDistanceMap(l1)

        for (i in (l2.indices)) {
            for (j in (i + 1 .. l2.lastIndex)) {
                val p1 = l2[i]
                val p2 = l2[j]
                val distance = dist(p1, p2)
                if (distances.contains(distance)) {
                    val (q1, q2) = distances[distance]!!
                    overlaps.putIfAbsent(p1, q1)
                    overlaps.putIfAbsent(p2, q2)
                }
            }
        }
        return overlaps
    }
}
import kotlin.math.abs

fun main() {

    val day = "19"

    fun part1(input: List<String>): Int {
        val scanners = parse(input)
        return solve(scanners.map { it.toMutableSet() }).first
    }

    fun part2(input: List<String>): Int {
        val scanners = parse(input)
        return solve(scanners.map { it.toMutableSet() }).second
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

data class Vector3(val x: Int, val y: Int, val z: Int)

private operator fun Vector3.minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
private operator fun Vector3.plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

private fun roll(v: Vector3) = Vector3(v.x, v.z, -v.y)
private fun turn(v: Vector3) = Vector3(-v.y, v.x, v.z)

// Credit to https://stackoverflow.com/a/16467849/5095444
private fun getRotations(v: Vector3) = sequence {
    var w = v
    repeat(2) {
        repeat(3) {             // Yield RTTT 3 times
            w = roll(w)
            yield(w)                  // Yield R
            repeat(3) {         // Yield TTT
                w = turn(w)
                yield(w)
            }
        }
        w = roll(turn(roll(w)))  // Do RTR
    }
}

private fun parse(input: List<String>): List<Set<Vector3>> {
    val scanners = mutableListOf<MutableSet<Vector3>>()
    for (line in input.filter { it.isNotEmpty() }) {
       if (line.startsWith("--")) {
           scanners.add(mutableSetOf())
       } else {
           val (x, y, z) = line.split(',').map { it.toInt() }
           scanners.last().add(Vector3(x, y, z))
       }
    }
    return scanners
}

private fun findOverlaps(s1: Set<Vector3>, s2: Set<Vector3>): Pair<Int, Vector3>? {
    val distances = mutableMapOf<Vector3, MutableSet<Vector3>>()
    val rotations = s2.map { getRotations(it).toList() }
    var r = 11
    for (beacon in s1) {
        for (rotation in rotations) {
            for ((i, otherBeacon) in rotation.withIndex()) {
                val distance = beacon - otherBeacon
                distances.getOrPut(distance) { mutableSetOf() }.add(otherBeacon)
                if (distances[distance]!!.size >= 12) r = i
            }
        }
    }
    val overlaps = distances.filter { it.value.size >= 12 }
    overlaps.entries.firstOrNull()?.let {
        return r to it.key
    }
    return null
}

fun translate(vectors: Set<Vector3>, rotation: Int, offset: Vector3): MutableSet<Vector3> {
    val rotations = vectors.map { getRotations(it).toList() }
    val rotated = rotations.map { it[rotation] }
    return rotated.map { it + offset }.toMutableSet()
}

fun manhattanDistance(v: Vector3, w: Vector3) = abs(v.x - w.x) + abs(v.y - w.y) + abs(v.z - w.z)

typealias Translations = MutableMap<Pair<Int, Int>, Pair<Int, Vector3>>
typealias Translation = Map.Entry<Pair<Int, Int>, Pair<Int, Vector3>>

private fun solve(scanners: List<MutableSet<Vector3>>): Pair<Int, Int> {
    val translations = mutableMapOf<Pair<Int, Int>, Pair<Int, Vector3>>()
    for (i in 0 .. scanners.lastIndex) {
        for (j in 0 .. scanners.lastIndex) {
            if (i == j) continue
            val res = findOverlaps(scanners[i], scanners[j])
            if (res != null) {
                translations[i to j] = res.first to res.second
            }
        }
    }

    fun findTranslations(from: Int, to: Int, t: Translations = translations, path: MutableSet<Translation> = mutableSetOf()): Set<Translation> {
        if (t.isEmpty() || path.any { it.key.first == from || it.key.second == to }) return mutableSetOf()
        val candidates = t.filter { it.key.first == from  }
        val res = candidates.entries.firstOrNull { it.key.second == to }
        val newTranslations = t.filter { it.key !in candidates }.toMutableMap()
        if (res != null) path.add(res)
        else {
            for (candidate in candidates) {
                val tt = findTranslations(candidate.key.second, to, newTranslations, path)
                if (tt.isNotEmpty()) path.add(candidate)
            }
        }
        return path
    }

    val beacons = mutableSetOf<Vector3>()
    val offsets = mutableMapOf( 0 to Vector3(0, 0, 0))
    beacons.addAll(scanners[0])
    for (i in 1..scanners.lastIndex) {
        val translation = findTranslations(0, i)
        var translated = scanners[i]
        var offset = Vector3(0, 0, 0)
        for (t in translation) {
            translated = translate(translated, t.value.first, t.value.second)
            offset = getRotations(offset).toList()[t.value.first] + t.value.second
        }
        offsets[i] = offset
        beacons.addAll(translated)
    }

    val distances = mutableListOf<Int>()
    for (i in 0 until offsets.size-1) {
        for (j in i+1 until offsets.size) {
            distances.add(manhattanDistance(offsets[i]!!, offsets[j]!!))
        }
    }
    return beacons.size to distances.maxOf { it }
}
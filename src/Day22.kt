fun main() {

    val day = "22"

    fun part1(input: List<String>): Long {
        val steps = parse(input).filter { it.inRange(-50, 50) }
        return rebootBetter(steps).sumOf { it.volume() }
    }

    fun part2(input: List<String>, filter: Boolean = false): Long {
        val steps = if (filter) parse(input).filter { it.inRange(-50, 50) } else parse(input)
        val cuboids = rebootBetter(steps)
        return cuboids.sumOf { it.volume() }
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 590784L)
    val testInput2 = readInput("Day${day}_test2")
    check(part2(testInput2, true) == 474140L)
    check(part2(testInput2) == 2758514936282235)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

data class Cuboid(val x1: Long, val x2: Long, val y1: Long, val y2: Long, val z1: Long, val z2: Long, val on: Boolean)

operator fun Cuboid.contains(other: Cuboid) =
    x1 <= other.x1 && x2 >= other.x2 &&
    y1 <= other.y1 && y2 >= other.y2 &&
    z1 <= other.z1 && z2 >= other.z2

fun Cuboid.inRange(min: Int, max: Int) = listOf(x1, x2, y1, y2, z1, z2).fold(true) { s, i -> s && i in (min .. max+1) }

fun Cuboid.volume(): Long = (x2 - x1) * (y2 - y1) * (z2 - z1)

fun Cuboid.split(at: Cuboid) = listOf (
    if (at.x1 in (x1 .. x2)) listOf(this.copy(x1 = at.x1), this.copy(x2 = at.x1)) else listOf(this)).flatten()
    .flatMap { if (at.x2 in (it.x1 .. it.x2)) listOf(it.copy(x1 = at.x2), it.copy(x2 = at.x2)) else listOf(it) }
    .flatMap { if (at.y1 in (it.y1 .. it.y2)) listOf(it.copy(y1 = at.y1), it.copy(y2 = at.y1)) else listOf(it) }
    .flatMap { if (at.y2 in (it.y1 .. it.y2)) listOf(it.copy(y1 = at.y2), it.copy(y2 = at.y2)) else listOf(it) }
    .flatMap { if (at.z1 in (it.z1 .. it.z2)) listOf(it.copy(z1 = at.z1), it.copy(z2 = at.z1)) else listOf(it) }
    .flatMap { if (at.z2 in (it.z1 .. it.z2)) listOf(it.copy(z1 = at.z2), it.copy(z2 = at.z2)) else listOf(it) }


operator fun <T> List<T>.component6(): T = this[5]

private fun parse(input: List<String>): List<Cuboid> {
    val coords = input.map { "(-?\\d)+".toRegex().findAll(it).mapIndexed { i, s -> s.value.toLong() + (i % 2) }.toList() }
    val states = input.map { "(on|off)".toRegex().findAll(it).map { s -> s.value == "on" }.first() }
    return states.zip(coords).map { (s, c) -> Cuboid(c[0], c[1], c[2], c[3], c[4], c[5], s) }
}

private fun rebootBetter(steps: List<Cuboid>): List<Cuboid> {
    // assuming the first step is always "on"
    var cuboids = listOf(steps.first())
    for (step in steps.drop(1)) {
        cuboids = cuboids
            .flatMap { it.split(step) }
            .filterTo(mutableListOf()) { it !in step }
            .apply { if (step.on) add(step) }
//        println(cuboids)
    }
    return cuboids
}

// Naive brute-force solution
private fun reboot(steps: List<Cuboid>): Set<Triple<Long, Long, Long>> {
    val cuboids = mutableSetOf<Triple<Long, Long, Long>>()
    for (step in steps) {
        val (x1, x2, y1, y2, z1, z2) = listOf(step.x1, step.x2, step.y1, step.y2, step.z1, step.z2)
        for (x in (x1 .. x2)) {
            for (y in (y1 .. y2)) {
                for (z in (z1 .. z2)) {
                    if (step.on) {
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
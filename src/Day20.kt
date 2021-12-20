import Day20.enhance

fun main() {

    val day = "20"

    fun part1(input: List<String>): Int {
        val algorithm = input.first().also { check(it.length == 512) }
        var image = input.drop(2).flatMapIndexed { i, row ->
            row.indicesOf('#').map { i to it }
        }.toSet()

        var bounds = image.bounds().apply(padding = 6)
        repeat(2) {
            image = enhance(image, algorithm, bounds)
            bounds = bounds.apply(padding = -3)
        }

        return image.count()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day${day}_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 0)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}

typealias Image = Set<Pair<Int, Int>>
typealias Bounds = List<Int>

fun String.indicesOf(char: Char) = mapIndexed { i, c -> if (c == char) i else null }.filterNotNull()
fun Image.bounds(): Bounds {
    val minX = this@bounds.minOf { it.first }
    val maxX = this@bounds.maxOf { it.first }
    val minY = this@bounds.minOf { it.second }
    val maxY = this@bounds.maxOf { it.second }
    return listOf(minX, maxX, minY, maxY)
}
fun Bounds.apply(padding: Int) = listOf(
    this@apply[0] - padding,
    this@apply[1] + padding,
    this@apply[2] - padding,
    this@apply[3] + padding
)

object Day20 {

    fun visualize(image: Image, padding: Int = 0) {
        val (minX, maxX, minY, maxY) = image.bounds()
        println("=".repeat(maxX - minX + padding*2 + 1))
        (minX - padding .. maxX + padding).map { x ->
            (minY - padding .. maxY+ padding).map { y ->
                x to y }.toList() }
            .forEach {
                it.forEach { p -> print(if (image.contains(p)) "#" else ".")}
                println()
            }
        }

    fun enhance(image: Image, algorithm: String, bounds: Bounds): Image {
        val (minX, maxX, minY, maxY) = bounds
        val outputImage = mutableSetOf<Pair<Int, Int>>()
        (minX .. maxX).forEach { x ->
            (minY ..maxY).forEach { y ->
                var sum = 0
                for (i in (-1..1)) {
                    for (j in (-1..1)) {
                        sum = (sum shl 1) + ((x + i to y + j) in image).compareTo(false)
                    }
                }
                if (algorithm[sum] == '#') outputImage.add(x to y)
            }
        }
        return outputImage
    }
}
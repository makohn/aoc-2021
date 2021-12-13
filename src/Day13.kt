fun main() {

    fun part1(input: List<String>): Int {
        val (dotInstructions, foldInstructions) = Day13Helper.parse(input)
        return Day13Helper.fold(dotInstructions, foldInstructions, times = 1, visualize = false)
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
//    check(part2(testInput) == 1924)

    val input = readInput("Day13")
    println(part1(input))
//    println(part2(input))
}

object Day13Helper {
    private const val prefix = "fold along "
    private const val neutral = 0

    fun parse(input: List<String>): Pair<List<MutableList<Int>>, List<Pair<Int, Int>>> {
        val splitIndex = input.indexOf("")
        val dotInstructions = input.subList(0, splitIndex)
            .map { it.split(",").map(String::toInt).toMutableList() }
        val foldInstructions = input.subList(splitIndex+1, input.size)
            .map { it.removePrefix(prefix).split("=") }
            .map {
                if (it.first() == "y") neutral to it.last().toInt()
                else it.last().toInt() to neutral
            }
        return dotInstructions to foldInstructions
    }

    fun fold(dotInstructions: List<MutableList<Int>>, foldInstructions: List<Pair<Int, Int>>,
             times: Int = foldInstructions.size, visualize: Boolean = true): Int {
        var transparent = dotInstructions.toMutableSet()
        var width = transparent.maxOf { it.first() }
        var height = transparent.maxOf { it.last() }
        if (visualize) visualize(transparent, width, height)
        foldInstructions.forEachIndexed { i, fold ->
            if (i >= times) return@fold transparent.count()
            transparent = transparent.map { dot ->
                dot[0] = if ((fold.first != 0) and (dot.first() > fold.first)) width - dot[0] - (fold.first - width/2) else dot[0]
                dot[1] = if ((fold.second != 0) and (dot.last() > fold.second)) height - dot[1] - (fold.second - height/2) else dot[1]
                dot
            }.filter { dot ->
                (dot.first() >= 0) and (dot.last() >= 0)
            }.toMutableSet()
            width -= if (fold.first != 0) (width - fold.first + 1) else 0
            height -= if (fold.second != 0) (height - fold.second + 1) else 0
            if (visualize) visualize(transparent, width, height)
        }
        return -1
    }

    private fun visualize(dots: Set<MutableList<Int>>, width: Int, height: Int) {
        val transparent = mutableListOf<MutableList<Char>>()
        (0 .. height).forEach { _ -> transparent.add(".".repeat(width+1).toMutableList()) }
        dots.forEach { dot -> transparent[dot.last()][dot.first()] = '#' }
        println("_".repeat(width+1))
        transparent.forEach{ println(it.joinToString("") ) }
        println("_".repeat(width+1))
        println()
    }
}

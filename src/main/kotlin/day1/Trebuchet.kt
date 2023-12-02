package day1

typealias ArtisticLine = String
typealias CalibrationDoc = List<ArtisticLine>

fun ArtisticLine.parseCoordinates() =
    this.filter { it.isDigit() }.map { it.digitToInt() }.concatFirstAndLastNumber()

fun ArtisticLine.parseSpelledOutCoordinates() =
    findAllNumbersList(this).concatFirstAndLastNumber()

fun List<Int>.concatFirstAndLastNumber() = "${this.first()}${this.last()}".toInt()


fun findAllNumbers(str: ArtisticLine) : String = findAllNumbersList(str).fold("") {acc, i -> acc + i}
fun findAllNumbersList(str: ArtisticLine) : List<Int> = parseLiteralsToNumbers(str).filter { it.isDigit() }.map { it.digitToInt() }
fun parseLiteralsToNumbers(str: ArtisticLine) : String = parseAllNumbersAsSequence(str.asSequence()).foldToString()
fun parseAllNumbersAsSequence(input: Sequence<Char>): Sequence<Char> = sequence {
    var rest : Sequence<Char>? = input
    while (rest != null) {
        val firstLiteral = rest.firstOrNull() ?: return@sequence
        val once = parseOnce(rest) ?: Pair(firstLiteral, rest.drop(1))

        yield(once.first)
        rest = once.second
    }
}


fun Sequence<Char>.foldToString() = fold("") { str, c -> str + c }

fun parseOnce(input: Sequence<Char>): Pair<Char, Sequence<Char>>? =
    replaceFirstIfRestIsNumber(input)?.toCharMonadish()

fun replaceFirstIfRestIsNumber(input: Sequence<Char>): Pair<Int, Sequence<Char>>? = numberMap.firstNotNullOfOrNull { (str, nr) ->
    if (input.startsWith(str)) Pair(nr, input.drop(1)) else null
}

fun Pair<Int, Sequence<Char>>.toCharMonadish() = Pair(this.first.firstDigit(), this.second)

fun Int.firstDigit() = this.toString()[0]
fun Sequence<Char>.startsWith(str: String) =
    this.take(str.length).zip(str.asSequence()).map { (a, b) -> a == b }.map { if (it) 1 else 0 }.sum() == str.length

val numberMap = mapOf<String, Int>(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)

fun CalibrationDoc.sumOfCalibrationValues(spelledOut : Boolean) =
    if (spelledOut)
        this.sumOf { it.parseSpelledOutCoordinates()}
    else
        this.sumOf { it.parseCoordinates() }




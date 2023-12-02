package day1

// Data
typealias ArtisticLine = String
typealias CalibrationDoc = List<ArtisticLine>

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

val digitMap = (1..9).map{ it.toString() to it}

val allDigits = numberMap + digitMap

// Solvers
fun ArtisticLine.parseArtisticCoordinates() =
    Pair(this.findFirstDigit(), this.findLastDigit()).concatFirstAndLastNumber()

fun ArtisticLine.parseSpelledOutCoordinates() =
    Pair(this.findFirstWithSpelledOutDigit()!!, this.findLastWithSpelledOutDigit()!!).concatFirstAndLastNumber()

fun CalibrationDoc.sumOfCalibrationValues(spelledOut : Boolean) =
    if (spelledOut)
        this.sumOf { it.parseSpelledOutCoordinates()}
    else
        this.sumOf { it.parseArtisticCoordinates() }

// Impl

fun Pair<Int, Int>.concatFirstAndLastNumber() = "${this.first}${this.second}".toInt()

fun ArtisticLine.findFirstDigit() = this.filter { it.isDigit() }.map { it.digitToInt() }[0]

fun ArtisticLine.findLastDigit() = this.reversed().findFirstDigit()

fun ArtisticLine.findFirstWithSpelledOutDigit() = parseAllBasedOnMap(this.asSequence(), allDigits).firstOrNull()

fun ArtisticLine.findLastWithSpelledOutDigit() = parseAllBasedOnMap(
        this.reversed().asSequence(),
        allDigits.mapKeys { it.key.reversed() }
    ).firstOrNull()



// Parsing
fun parseAllBasedOnMap(input: Sequence<Char>, map : Map<String, Int>): Sequence<Int> = sequence {
    var rest : Sequence<Char>? = input
    while (rest?.firstOrNull() != null) {
        val once = parseFirsIfInMap(rest, map)

        if (once != null) {
            yield(once.first)
        }
        rest = rest.drop(1)
    }
}

fun parseFirsIfInMap(input: Sequence<Char>, map : Map<String, Int>): Pair<Int, Sequence<Char>>? = map.firstNotNullOfOrNull { (str, nr) ->
    if (input.startsWith(str)) Pair(nr, input.drop(str.length)) else null
}



// Print & helpers
fun findAllNumbers(str: ArtisticLine) : String = parseAllBasedOnMap(str.asSequence(), allDigits).foldIntToString()

fun parseAndExtractNumberBasedOnMap(str: String, map: Map<String, Int>) : Int? =
    parseFirsIfInMap(str.asSequence(), map)?.first



// Sequence
fun Sequence<Int>.foldIntToString() = fold("") { str, c -> str + c }
fun Sequence<Char>.foldToString() = fold("") { str, c -> str + c }
fun Sequence<Char>.startsWith(str: String) =
    this.take(str.length).zip(str.asSequence()).map { (a, b) -> a == b }.map { if (it) 1 else 0 }.sum() == str.length






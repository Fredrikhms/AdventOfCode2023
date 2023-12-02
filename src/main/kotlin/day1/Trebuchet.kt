package day1

typealias ArtisticLine = String
typealias CalibrationDoc = List<ArtisticLine>



fun ArtisticLine.parseCoordinates() =
    this
        .filter { it.isDigit() }
        .map { it.digitToInt() }
        .let { "${it.first()}${it.last()}".toInt() }

fun CalibrationDoc.sumOfCalibrationValues() = this.sumOf { it.parseCoordinates() }


package day1

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.testng.annotations.Test
import java.io.File

class TrebuchetPart2Test {

    val folder = File("resources/day1")

    private val sample = File(folder, "sampleInputPart2")

    private val actual = File(folder, "input")

    @Test
    fun `Files test files exist`() {
        assertThat(folder).isDirectory()
        assertThat(sample).exists()
        assertThat(actual).exists()
    }


    @Test
    fun `Check parsing of numbers`() {
        assertThat(replaceFirstIfRestIsNumber("two1nine".asSequence())?.toReadable()).isEqualTo(Pair(2, "wo1nine"))
    }

    @Test
    fun `Check parsing of number four`() {
        assertThat(replaceFirstIfRestIsNumber("four1nine".asSequence())?.toReadable()).isEqualTo(Pair(4, "our1nine"))
    }

    @Test
    fun `Check parsing of all numbers`() {
        assertThat(parseLiteralsToNumbers("two1abc"))
            .isEqualTo("2wo1abc")
    }

    @Test
    fun `Check parsing parsing of two numbers`() {
        assertThat(findAllNumbers("two1nine"))
            .isEqualTo("219")
    }

    @Test
    fun `Given all valid numbers, check that they are converted correctly`() {
        assertThat(findAllNumbers("onetwothreefourfivesixseveneightnine")).isEqualTo("123456789")
    }

    @Test
    fun `Check parsing parsing of all examples`() {
        assertThat(findAllNumbers("two1nine")).isEqualTo("219")
        assertThat(findAllNumbers("eightwothree")).isEqualTo("823")
        assertThat(findAllNumbers("abcone2threexyz")).isEqualTo("123")
        assertThat(findAllNumbers("xtwone3four")).isEqualTo("2134")
        assertThat(findAllNumbers("4nineeightseven2")).isEqualTo("49872")
        assertThat(findAllNumbers("zoneight234")).isEqualTo("18234")
        assertThat(findAllNumbers("7pqrstsixteen")).isEqualTo("76")
    }

    @Test
    fun `Check parsing of coordinates of all examples`() {
        assertThat("two1nine".parseSpelledOutCoordinates()).isEqualTo(29)
        assertThat("eightwothree".parseSpelledOutCoordinates()).isEqualTo(83)
        assertThat("abcone2threexyz".parseSpelledOutCoordinates()).isEqualTo(13)
        assertThat("xtwone3four".parseSpelledOutCoordinates()).isEqualTo(24)
        assertThat("4nineeightseven2".parseSpelledOutCoordinates()).isEqualTo(42)
        assertThat("zoneight234".parseSpelledOutCoordinates()).isEqualTo(14)
        assertThat("7pqrstsixteen".parseSpelledOutCoordinates()).isEqualTo(76)
    }

    @Test
    fun `Parse spelled out coordinates`() {
        assertThat("two1nine".parseSpelledOutCoordinates()).isEqualTo(29)
        assertThat("dhpbgtkmjfourone6rsgnpvsbjtkfqsvrs9threethree".parseSpelledOutCoordinates()).isEqualTo(43)
        assertThat("3".parseSpelledOutCoordinates()).isEqualTo(33)
    }

    @Test
    fun `Paring sample two gives correct result`() {
        val spelledOut :CalibrationDoc = sample.readLines()
        assertThat(spelledOut.sumOfCalibrationValues(spelledOut=true)).isEqualTo(281)
    }

    @Test
    fun `Check parsing sample gives same amount`() {
        val spelledOut :CalibrationDoc = sample.readLines()
        assertThat(spelledOut.map { it.parseSpelledOutCoordinates() }.count())
            .isEqualTo(spelledOut.count())
    }

    @Test
    fun `Paring actual gives a sensible value`() {
        val spelledOut :CalibrationDoc = actual.readLines()
        assertThat(spelledOut.sumOfCalibrationValues(spelledOut=true)).isEqualTo(54980)
    }


    @Test
    fun `Startswith - works`() {
        assert("12345".asSequence().startsWith("123"))
    }

    fun Pair<Int, Sequence<Char>>.toReadable() = Pair(first, second.foldToString())

}
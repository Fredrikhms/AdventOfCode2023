package day2

import day2.CubeColors.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForClassTypes
import org.testng.annotations.Test
import java.io.File

class CubeConundrumTest {
    val folder = File("resources/day2")

    private val sample = File(folder, "sampleInput")

    private val actual = File(folder, "input")

    @Test
    fun `Files test files exist`() {
        AssertionsForClassTypes.assertThat(folder).isDirectory()
        AssertionsForClassTypes.assertThat(sample).exists()
        AssertionsForClassTypes.assertThat(actual).exists()
    }

    val line1 = """Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"""
    val line1Parsed = Game(id=1,
        setOf(
            listOf(Cubes(3, Blue), Cubes(4, Red)),
            listOf(Cubes(1, Red), Cubes(2, Green), Cubes(6, Blue)),
            listOf(Cubes(2, Green)),
            )
    )
    val sampleBag : Bag = mapOf(
        Red to 12,
        Green to 13,
        Blue to 14,
    )


    @Test
    fun `Parse id`() {
        assertThat(extractId(line1)).isEqualTo(1)
    }

    @Test
    fun `Extract observed sets`() {
        assertThat(extractObservedCubeSets(line1)).isEqualTo(line1Parsed.observed)

    }

    @Test
    fun `First game is possible with given bag`() {
        assertThat(line1.parseToGame().possibleWithBag(sampleBag)).isEqualTo(true)
    }

    @Test
    fun `All games are parsed sample`() {
        val games = sample.readLines().map { it.parseToGame() }

        val possible = listOf(1,2,5)
        val impossible = listOf(3,4)
        assertThat(games.count() == (possible + impossible).count())
    }
    @Test
    fun `All games match sample`() {
        val games = sample.readLines().map { it.parseToGame() }

        val possible = listOf(1,2,5)
        val impossible = listOf(3,4)
        games.filter { possible.contains(it.id) }.forEach { assertThat(it.possibleWithBag(sampleBag)).isTrue() }

        games.filter { impossible.contains(it.id) }.forEach { assertThat(it.possibleWithBag(sampleBag)).isFalse() }
    }

    @Test
    fun `Sum of samples is 8`() {
        val games = sample.readLines().map { it.parseToGame() }
        val sumOfPossible = games.filter { it.possibleWithBag(sampleBag) }.sumOf { it.id }

        assertThat(sumOfPossible).isEqualTo(8)
    }

    @Test
    fun `Sum of actual is sensible`() {
        val games = actual.readLines().map { it.parseToGame() }
        val sumOfPossible = games.filter { it.possibleWithBag(sampleBag) }.sumOf { it.id }

        assertThat(sumOfPossible).isEqualTo(3059)
    }


    @Test
    fun `Bag powers are calculated by multiplying all`() {
        val bag : Bag = mapOf(2 to Green, 4 to Red, 6 to Blue,).swap()
        assertThat(bag.power())
            .isEqualTo(48)
    }

    fun<K, V> Map<K, V>.swap() = this.map { it.value to it.key }.toMap()

    @Test
    fun `sample - Minimum bag powers is correct`() {
        val games = sample.readLines().map { it.parseToGame() }
        val minimumbags = games.map { it.minimumBag() }
        val powers = minimumbags.map { it.power() }

        println(minimumbags[0])

        assertThat(powers[0]).isEqualTo(48)
        assertThat(powers[1]).isEqualTo(12)
        assertThat(powers[2]).isEqualTo(1560)
        assertThat(powers[3]).isEqualTo(630)
        assertThat(powers[4]).isEqualTo(36)
    }

    @Test
    fun `sample - Minimum bag power sum of sample is correct`() {
        val games = sample.readLines().map { it.parseToGame() }
        assertThat(games.sumOf { it.minimumBag().power() })
            .isEqualTo(2286)
    }

    @Test
    fun `actual - Minimum bag power sum is sensible`() {
        val games = actual.readLines().map { it.parseToGame() }
        assertThat(games.sumOf { it.minimumBag().power() })
            .isEqualTo(65371)
    }


}
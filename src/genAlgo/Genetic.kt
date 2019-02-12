package genAlgo

import kotlin.math.floor


val N = 10
val population = mapOf(
    -1   to  5,
    -0.5 to  7,
    0    to  10,
    0.25 to  9,
    0.5  to  7,
    1    to  5,
    2    to  4,
    3    to  3,
    4    to  2,
    5    to  1
)


fun getProportionalRange(map: Map<Any, Int>): HashMap<Any, Pair<Int, Int>> {

    var sum = 0
    val probabilityMap:HashMap<Any, Double> = hashMapOf()
    val rangeMap:HashMap<Any, Pair<Int, Int>> = hashMapOf()
    map.forEach { (_, value) -> sum += value }
    map.forEach { (key, value) -> probabilityMap[key] = value.toDouble() / sum }

    var position = 0
    probabilityMap.forEach { (key, value) ->
        val bigIntValue = floor(value * 1000).toInt()
        rangeMap[key] = Pair(position ,position + bigIntValue)
        position += bigIntValue // this sets the start position for the next range
    }
    return rangeMap
}



fun main(){


    print(getProportionalRange(population))

}
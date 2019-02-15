package genAlgo

import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random

const val N = 10 //const refers to compile time
const val LOCI = 3
val alleleRange = Pair(-1.0,5.0)
data class Individual (var gene: MutableList<Double> = MutableList(LOCI){ Random.nextDouble(alleleRange.first, alleleRange.second)},
                       val fitness: Double = gene.reduce{acc, it-> acc + it.pow(2)} )
var generation = List(N){Individual()}
fun crossover( generation: List<Individual>, probOfCross: Double = 0.8 ): List<Individual> {
    require(probOfCross in 0.0..1.0)

    repeat(5) {
        if (Random.nextDouble(0.0,1.0) in 0.0..probOfCross ) {
            val a = generation[0].gene[0]
            val i = 2 * it
            generation[i].gene[0] = generation[i + 1].gene[0].also {//
                generation[i + 1].gene[0] = generation[i].gene[0] }
          //  require(a != generation[0].gene[0]) { "The value has not changed" }
        }
    }
    return generation
}
fun selection(generation: List<Individual>): List<Individual> {
    var newList = mutableListOf<Individual>()

    repeat(N){
        val a = generation.random();  val b = generation.random()
        newList.add( if ( a.fitness < b.fitness) a else b)
    }
    return newList
}

fun mutation(generation: List<Individual>, probOfMutation:Double = 0.1 ): List<Individual> {
    if (probOfMutation == 0.0) return generation
    val stop = 2 * (1 / probOfMutation ) - 1
    for (it in generation) {
        for (i  in 0..2) {
            val r = Random.nextDouble(-1.0,stop)
            if (r < 1) it.gene[i] += (0.05 * r)
        }
    }

    return generation
}

fun main(){


    println(generation)

    repeat(1000){
        generation = mutation(crossover(selection(generation)))
    }

    println(mutation(crossover(selection(generation))))

}
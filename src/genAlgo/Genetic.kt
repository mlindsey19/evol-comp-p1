package genAlgo

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

const val N = 10 //const refers to compile time
const val Runs = 30
const val LOCI = 3
val alleleRange = Pair(-1.0,5.0)
data class Individual (val gene: MutableList<Float> = MutableList(LOCI){ Random.nextDouble(alleleRange.first, alleleRange.second).toFloat()},
                       var fitness: Float? = null )
fun setFitness(individual: Individual) {
    var fit = 0.0
    for (loci in individual.gene)
        fit += loci.pow(2)

    individual.fitness = fit.toFloat()

}
fun crossover( generation: MutableList<Individual>, probOfCross: Double = 0.8 ): MutableList<Individual> {
    require(probOfCross in 0.0..1.0)

    repeat(5) {
        if (Random.nextDouble(0.0,1.0) in 0.0..probOfCross ) {
            val a = generation[0].gene[0]
            val i = 2 * it
            generation[i].gene[0] = generation[i + 1].gene[0].also {//
                generation[i + 1].gene[0] = generation[i].gene[0] }
            //require(a != generation[0].gene[0]) { "The value has not changed" }
        }
    }
    for (individual in generation) setFitness(individual)
    return generation
}
fun selection(generation: MutableList<Individual>): MutableList<Individual> {
    var newList = mutableListOf<Individual>()

    repeat(N){
        val a = Random.nextInt(0, 9)
        val b = Random.nextInt(0, 9)
        val temp: Individual = if ( generation[a].fitness!! < generation[b].fitness!!) generation[a].copy() else generation[b].copy()
        val x:Float = temp.gene[0]
        val y:Float = temp.gene[1]
        val z:Float = temp.gene[2]


        newList.add(Individual(mutableListOf(x,y,z)))

    }
    return newList
}

fun mutation(generation: MutableList<Individual>, probOfMutation:Double = 0.1 ): MutableList<Individual> {
    if (probOfMutation == 0.0) return generation
    val stop = 2 * (1 / probOfMutation ) - 1
    for (it in generation) {
        for (i  in 0..2) {
            val r = Random.nextDouble(-1.0,stop)
            if (r < 1) it.gene[i] += (.7 * r).toFloat()
        }
    }
    for (individual in generation) setFitness(individual)
    return generation
}
fun aggregateStats(generation: MutableList<Individual>,
                   mostFitPerGen: MutableList<Individual>,
                   worstFitPerGen: MutableList<Individual>,
                   avgFitPerGen:MutableList<Float>) {
    mostFitPerGen.add(generation.minBy { it -> it.fitness!! }!!)
    worstFitPerGen.add(generation.maxBy { it -> it.fitness!! }!!)
    var avg = 0.0
    for (individual in generation)
        avg += individual.fitness!! / 10
    avgFitPerGen.add(avg.toFloat())}

fun stDev(list: List<Float>, listAvgOfRun: List<Float>): List<Float> {
    val newList = mutableListOf<Float>()
    for (gen in (0..5)) {
        var sumForStdev = 0f
        for (i in (0..179).filter { i -> (i - gen) % 6 == 0 }) {
            sumForStdev += (list[i] - listAvgOfRun[gen]).pow(2)
        }
        newList.add( sqrt(sumForStdev / Runs) )
    }
    return newList
}


fun main() {

    val bestFitOfGen = mutableListOf<Individual>()
    val worstFitPerGen = mutableListOf<Individual>()
    val avgFitPerGen = mutableListOf<Float>()

    repeat(Runs) {
        var generation = MutableList(N) { Individual() }
        for (individual in generation) setFitness(individual)
        aggregateStats(generation, bestFitOfGen, worstFitPerGen, avgFitPerGen)
        repeat(5) {
            repeat(10) { generation = mutation(crossover(selection(generation))) }
            aggregateStats(generation, bestFitOfGen, worstFitPerGen, avgFitPerGen)
        }
    }
    val bestOfRuns = mutableListOf<Individual>()

    val mapOfBestFitOfGen: List<Float> = bestFitOfGen.map { individual -> individual.fitness!! }
    var bestOfEachGenPerRun: List<Individual>
    for (run in (0..29)){
        bestOfEachGenPerRun = bestFitOfGen.filterIndexed { index, _:Individual -> (index - run) % 30 == 0}
        bestOfRuns.add(bestOfEachGenPerRun.maxBy { individual ->individual.fitness!!  }!!)
    }

    val bestOfRunOfBestOfGen = mutableListOf<Individual>()
    val avgOfRunOfAvgOfGen = mutableListOf<Float>()
    val avgOfRunOfBestOfGen = mutableListOf<Float>()



    for (gen in (0..5)) {
        var bestTemp = Individual(mutableListOf(10f, 10f, 10f), 1000f)//placeholder
        var avgAvgPerRunTemp = 0f;
        var avgBestPerRunTemp = 0f;
        var stdDevTemp = 0f
        for (i in (0..179).filter { i -> (i - gen) % 6 == 0 }) {
            bestTemp = if (bestTemp.fitness!! < bestFitOfGen[i].fitness!!) bestTemp else bestFitOfGen[i]
            avgAvgPerRunTemp += avgFitPerGen[i] / Runs
            avgBestPerRunTemp += bestFitOfGen[i].fitness!! / Runs
        }
        avgOfRunOfAvgOfGen.add(avgAvgPerRunTemp)
        bestOfRunOfBestOfGen.add(bestTemp)
        avgOfRunOfBestOfGen.add(avgBestPerRunTemp)
    }

    val stDevOfRunOfAvgOfGen = stDev(avgFitPerGen,avgOfRunOfAvgOfGen)
    val stDevOfRunOfBestOfGen = stDev(mapOfBestFitOfGen,avgOfRunOfBestOfGen)

//
//    avgOfRunOfAvgOfGen.forEach { print("- $it") }
//    println()
//    stDevOfRunOfAvgOfGen.forEach { print("- $it") }
//    println()
//    avgOfRunOfBestOfGen.forEach { print("- $it") }
//    println()
//    stDevOfRunOfBestOfGen.forEach { print("- $it") }
//    println()




//    var stdvBestOfRun = 0f
//    val avgOfBestOfRunForStdv = bestOfRunOfBestOfGen.fold(0f) { _, individual: Individual -> individual.fitness!! / 6f }
//    var sumForStdv = 0f
//    bestOfRunOfBestOfGen.forEach {
//        var muFromItSqr = (it.fitness!! - avgOfBestOfRunForStdv).pow(2)
//        sumForStdv += muFromItSqr
//        println(it)
//    }
//    stdvBestOfRun = (sqrt(sumForStdv / 6f))
//    println("sd: $stdvBestOfRun")

    val columnTitleTmpt = "Gen\t%d:".padStart(26)
    val avg_tmpt = "avg:\t%8.4f".padStart(24)
    val best_tmpt = "best:\t%8.4f".padStart(24)
    val gene_tmpt = "(%.3f, %.3f, %.3f)"
    val wrst_tmpt = "worst:\t%8.4f".padStart(24)
    val stdv_tmpt = "s.d.:\t%8.4f".padStart(24)
    val rowTitleTmpt ="Run %d:".padEnd(220,'-')



    for(i in (0..5))print(columnTitleTmpt.format(i))
    print("Run Stats:".padStart(24))
    println()
    for(i in (0..29)) {
        println(rowTitleTmpt.format(i + 1))
        for (j in (0..5)) print(avg_tmpt.format(avgFitPerGen[(6 * j) + i]))
        println()
        for (j in (0..5)) print(best_tmpt.format(bestFitOfGen[(6 * j) + i].fitness))
        println()
        for (j in (0..5)) print("%28s".format(
            gene_tmpt.format(
                bestFitOfGen[(6 * j) + i].gene[0],
                bestFitOfGen[(6 * j) + i].gene[1],
                bestFitOfGen[(6 * j) + i].gene[2] ))
        )
        println()
        for (j in (0..5)) print(wrst_tmpt.format(worstFitPerGen[(6 * j) + i].fitness))
        println()
        for (j in (0..5)) print("%28s".format(
            gene_tmpt.format(
                worstFitPerGen[(6 * j) + i].gene[0],
                worstFitPerGen[(6 * j) + i].gene[1],
                worstFitPerGen[(6 * j) + i].gene[2]))
        )
        println()
    }
    println("Totals:".padEnd(220,'-'))
    for(j in (0..5)) print(avg_tmpt.format(avgOfRunOfAvgOfGen[j]))
    println()
    for(j in (0..5)) print(stdv_tmpt.format(stDevOfRunOfAvgOfGen[j]))
    println()
    for(j in (0..5)) print(best_tmpt.format(bestOfRunOfBestOfGen[j].fitness))
    println()
    for(j in (0..5)) print("%s".format(stdv_tmpt.format(stDevOfRunOfBestOfGen[j])))
    println()
    for(j in (0..5)) print(gene_tmpt.format(bestFitOfGen[ j ].gene[0],
        bestFitOfGen[j].gene[1],
        bestFitOfGen[j].gene[2]))




}

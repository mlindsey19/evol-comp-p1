package genAlgo

abstract class Evolution(val numbOfRuns: Int) {
    abstract val popSize: Int
    abstract val numbOfGens: Int
    abstract val initPop: List<Any>
    abstract var generatoin: MutableList<Any>

    abstract fun getBestofGen()  // return genotype and phenotype
    abstract fun getWorstOfGen()// return genotype and phenotype
    abstract fun getAvgOfGen()

    abstract fun getBestOfRun()
    abstract fun getAvgOfRun()

    fun getAvgofBestofRuns() {}
    fun getAvgOfAvgOfGen(){}

    abstract fun selection()
    abstract fun crossover()
    abstract fun mutation()



}
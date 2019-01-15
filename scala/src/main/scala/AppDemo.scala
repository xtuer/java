object AppDemo {
    def main(args: Array[String]): Unit = {
        println(List("Hello Alice", "Hello Biao", "Biao to Peking").par.flatMap(_.split(" ")).filter(_ != "to").groupBy(e=>e).mapValues(_.size))
    }
}

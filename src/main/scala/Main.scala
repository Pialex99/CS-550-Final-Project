import java.io.{ File, PrintWriter }
import sys.process._

object Main extends App {
  val error_sat_msg = """(error "Cannot get value unless immediately preceded by SAT/INVALID or UNKNOWN response.")"""
  val output_dir = "smt"
  val dir = new File(output_dir)
  if (!dir.exists()) {
    dir.mkdir()
  }

  def writeToFile(filename: String, content: String): Unit = {
    val pw = new PrintWriter(new File(filename))
    pw.write(content)
    pw.close
  }

  var res = 0

  chisel3.stage.ChiselStage
    .emitSMTLIB(
      // new adder.Adder                        // not OK because overflow
      // new memoryInduction.Induction          // OK
      // new multipliers.ShiftAddMultiplier(4)  // OK
      new pipeline.Pipeline                  // OK
      // new preconditions.MultiplyAndAdd       // OK
      // new topological.Topo                   // not OK because overflow
    )
    .foreach {
      case (name, content) =>
        writeToFile(s"$output_dir/$name.smt2", content)
        println(Console.CYAN + Console.BOLD + s"Running cvc4 on $output_dir/$name.smt2" + Console.RESET)
        (s"cvc4 --incremental $output_dir/$name.smt2" !!)
          .split("\n")
          .filter(_ != error_sat_msg)
          .map {
            case "sat"   => res += 1; Console.RED + Console.UNDERLINED + Console.BOLD + "not OK" + Console.RESET
            case "unsat" => Console.GREEN + "OK" + Console.RESET
            case str if str.startsWith("\"") && str.endsWith("\"") =>
              Console.BLUE + str.slice(1, str.length() - 1) + Console.RESET
            case str => str
          }
          .foreach(println)
    }
  sys.exit(res)
}

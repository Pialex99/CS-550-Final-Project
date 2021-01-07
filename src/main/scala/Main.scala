import java.io._
import sys.process._

object Main extends App {
  val error_sat_msg = """(error "Cannot get value unless immediately preceded by SAT/INVALID or UNKNOWN response.")"""
  def writeToFile(filename: String, content: String): Unit = {
    val pw = new PrintWriter(new File(filename))
    pw.write(content)
    pw.close
  }

  chisel3.stage.ChiselStage.emitSMTLIB(
    // new adder.Adder                        // not OK because overflow
    // new memoryInduction.Induction          // OK
    // new multipliers.ShiftAddMultiplier(4)  // OK
    new pipeline.Pipeline                  // OK
    // new preconditions.MultiplyAndAdd       // OK
    // new topological.Topo                   // not OK because overflow
  ).foreach {
    case (name, content) => 
      writeToFile(s"smt/$name.smt2", content)
      println(s"Running cvc4 on smt/$name.smt2")
      (s"cvc4 --incremental smt/$name.smt2" !!).split("\n").filter(_ != error_sat_msg).map {
        case "sat" => "not OK"
        case "unsat" => "OK"
        case str => str
      }.foreach(println)
  }
}

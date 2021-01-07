package preconditions

import chisel3._
import chisel3.experimental.verification.{ ensure, require }
import chisel3.experimental.Methodology

class MultiplyAndAdd extends Module {
  val io = IO(new Bundle {
    val a   = Input(UInt(8.W))
    val b   = Input(UInt(8.W))
    val c   = Input((UInt(8.W)))
    val out = Output(UInt(17.W))
  })
  require(io.a =/= 0.U && io.b =/= 0.U && io.c =/= 0.U)
  val intermediary = Wire(UInt(8.W))
  val innerAdder   = Module(new Adder)
  innerAdder.io.a := io.a
  innerAdder.io.b := io.b
  intermediary := innerAdder.io.out
  val innerMultiplier = Module(new Multiplier)
  innerMultiplier.io.a := intermediary
  innerMultiplier.io.b := io.c
  io.out := innerMultiplier.io.out
  ensure(io.out === (io.a + io.b) * io.c, mtd = Methodology.Combinatorial)
}

class Adder extends Module {
  val io = IO(new Bundle {
    val a   = Input(UInt(8.W))
    val b   = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })
  require(io.a =/= 0.U && io.b =/= 0.U)
  io.out := io.a + io.b
  ensure(io.out === io.a + io.b, mtd = Methodology.Combinatorial)
}

class Multiplier extends Module {
  val io = IO(new Bundle {
    val a   = Input(UInt(8.W))
    val b   = Input(UInt(8.W))
    val out = Output(UInt(16.W))
  })
  require(io.a =/= 0.U && io.b =/= 0.U)
  io.out := io.a * io.b
  ensure(io.out === io.a * io.b, mtd = Methodology.Combinatorial)
}

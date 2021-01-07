package pipeline

import chisel3._
import chisel3.experimental.verification.{ assert, Delay }
import chisel3.experimental.Methodology

class Pipeline extends Module {
  val io = IO(new Bundle {
    val in_a = Input(UInt(8.W))
    val in_b = Input(UInt(8.W))
    val out  = Output(UInt(17.W))
  })
  val a_2_reg = RegInit(0.U(16.W))
  val b_2_reg = RegInit(0.U(16.W))

  assert(io.out.in(1).in(1) === (io.in_a * io.in_a +& io.in_b * io.in_b).in(1), mtd = Methodology.BoundedModelCheck)

  a_2_reg := io.in_a * io.in_a
  b_2_reg := io.in_b * io.in_b

  io.out := a_2_reg +& b_2_reg
}

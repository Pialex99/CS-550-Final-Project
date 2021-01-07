package adder

import chisel3._
import chisel3.experimental.verification.{ assert, _ }
import chisel3.experimental.Methodology

class Adder extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(16.W))
    val in2 = Input(UInt(16.W))
    val out = Output(UInt(16.W))
  })

  val reg = Reg(UInt(16.W))
  reg := io.in1 + io.in2

  io.out := reg
  assert(io.out.in(1) >= io.in1, mtd = Methodology.BoundedModelCheck)
}

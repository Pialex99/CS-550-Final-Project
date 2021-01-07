package memoryInduction

import chisel3._
import chisel3.experimental.verification.assert
import chisel3.experimental.Methodology

class Induction extends Module {
  val io = IO(new Bundle {
    val in  = Input(Bool())
    val out = Output(Bool())
  })

  val reg1 = RegInit(0.U(4.W))
  val reg2 = RegInit(0.U(4.W))

  reg1 := (reg1 << 1) | io.in
  reg2 := (reg2 << 1) | io.in

  io.out := reg1(3) ^ reg2(3)

  assert(reg1 === reg2, mtd = Methodology.MemoryInduction)
}

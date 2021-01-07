package topological

import chisel3._
import chisel3.experimental.verification.{assert, Delay}
import chisel3.experimental.Methodology

class Topo extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(16.W))
    val in2 = Input(UInt(16.W))
    val out = Output(UInt(16.W))
  })
  val id1 = "id1"
  val id2 = "id2"
  val id3 = "id3"
  val id4 = "id4"

  assert(1.U(3.W) + 1.U(3.W) === 2.U(3.W), id=id1, mtd = Methodology.Combinatorial)
  assert(2.U(3.W) + 1.U(3.W) === 3.U(3.W), id=id2, deps=List(id1), mtd = Methodology.Combinatorial)
  val reg = Reg(UInt(16.W))
  reg := io.in1 + io.in2

  io.out := reg
  assert(io.out.in(1) >= io.in1, mtd=Methodology.BoundedModelCheck, deps=List(id1, id2))
}

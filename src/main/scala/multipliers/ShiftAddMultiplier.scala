package multipliers

import chisel3._
import chisel3.util._
import chisel3.iotesters._
import chisel3.experimental.verification.{ assert, _ }
import chisel3.experimental.Methodology

class ShiftAddMultiplier(width: Int) extends Module {
  val io = IO(new Bundle {
    val inA = Flipped(Decoupled((UInt(width.W))))
    val inB = Flipped(Decoupled((UInt(width.W))))

    val out = Decoupled(UInt((width * 2).W))
  })
  def printfInfo() = {
    printf("computation in progress : %d\n", computationInProgress)
    printf("aReg : %d\n", aReg)
    printf("bReg : %d\n", bReg)
    printf("outReg : %d\n", outReg)
    printf("counter : %d\n", counter.value)
  }

  //for formal verification only
  val aReg               = RegInit(0.U(width.W))
  val bReg               = RegInit(0.U(width.W))
  val mask               = Fill(width, 1.U(1.W))
  val outReg             = RegInit(0.U((width * 2).W))
  def shiftMask(i: UInt) = (mask << i)(width - 1, 0)
  val counter            = Counter(width + 1)

  //invariant = a[w-1:i]0[i-1:0]xb + out = axb
  val invariant = Wire(Bool())
  invariant := (bReg & shiftMask(counter.value)) * aReg + outReg === aReg * bReg

  val aAcc = RegInit(0.U((2 * width).W))

  assume(0.U <= counter.value && counter.value <= width.U)
  val computationInProgress = RegInit(false.B)

  def transactionStart: Bool = io.inA.valid && io.inB.valid && io.inA.ready && io.inB.ready
  when(transactionStart) {
    computationInProgress := true.B
    aReg := io.inA.bits
    aAcc := io.inA.bits
    bReg := io.inB.bits
    outReg := 0.U
    counter.reset()
    assert(invariant.in(1), mtd = Methodology.BoundedModelCheck)
  }.elsewhen(computationInProgress && counter.value =/= width.U) {
    //assert(invariant, mtd=Methodology.LoopInvariant)
    // when(counter.value =/= 3.U) {
      outReg := outReg + Mux(bReg(counter.value), aReg << counter.value, 0.U)
    // }
    counter.inc()
  }

  when(computationInProgress && counter.value === width.U) {
    computationInProgress := false.B
    counter.reset()
  }

  io.out.valid := counter.value === width.U
  io.inA.ready := !computationInProgress
  io.inB.ready := !computationInProgress
  io.out.bits := outReg

  when(transactionStart) {
    assert((io.inB.bits * io.inA.bits === (io.out.bits).in(width + 1)), mtd = Methodology.BoundedModelCheck)
  }
}

class ShiftAddMultiplierPeekPokeTester(c: ShiftAddMultiplier) extends PeekPokeTester(c) {
  for {
    i <- 1 to 20
    j <- 1 to 20
  } {
    println(s"for computation $i,$j")
    poke(c.io.inA.bits, i)
    poke(c.io.inB.bits, j)
    poke(c.io.inA.valid, 1)
    poke(c.io.inB.valid, 1)
    step(1)
    println(s"c.io.out = ${peek(c.io.out)}")
    poke(c.io.inA.valid, 0)
    poke(c.io.inB.valid, 0)

    var count = 0
    while (peek(c.io.out.valid) == BigInt(0) && count < 20) {
      println(s"c.io.out = ${peek(c.io.out)}")
      step(1)
      count += 1
    }
    if (count > 30) {
      println(s"Waited $count cycles on gcd inputs $i, $j, giving up")
      System.exit(0)
    }
    println(s"c.io.out = ${peek(c.io.out)}")
    expect(c.io.out.bits, i * j)
    step(1)
  }
}

class ShiftAddMultiplierTester extends ChiselFlatSpec {
  behavior of "ShiftAddMultiplier"
  backends foreach { backend =>
    it should s"correctly mutliply randomly generated numbers in $backend" in {
      chisel3.iotesters.Driver(() => new ShiftAddMultiplier(8), backend)(c => new ShiftAddMultiplierPeekPokeTester(c)) should be(
        true
      )
    }
  }
}

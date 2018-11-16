package modem

import chisel3._
import chisel3.experimental.FixedPoint
import dsptools.numbers._
import breeze.math.Complex
import scala.math
import dsptools.DspTester

class RXTester[T <: Data, U <: Data](c: RX[T,U], trials: Seq[T]) extends DspTester(c) {
  val maxCyclesWait = 1000 //Whatever numnber

  poke(c.io.in.valid, 1)
  poke(c.io.out.ready, 1)

  for(trial <- trials) {

  }
}

object FixedRXTester {
  def apply(
    iqParams: IQBundleParams[FixedPoint],
    pktDetectParams: FixedPacketDetectParams,
    eqParams: FixedEqualizerParams,
    cfoParams: FixedCFOParams,
    cpParams: CyclicPrefixParams[FixedPoint],
    fftParams: FixedFFTParams,
    bitsBundleParams: BitsBundleParams[Bool],
    demodParams: HardDemodParams,
    viterbiParams: FixedCoding,
    trials: Seq[DspComplex[UInt]]): Boolean = {
    chisel3.iotesters.Driver.execute(Array("-tbn", "firrtl", "-fiwv"), () => new RX(iqParams = iqParams, pktDetectParams = pktDetectParams, equalizerParams = eqParams, cfoParams = cfoParams, cyclicPrefixParams = cpParams, fftParams = fftParams, bitsBundleParams = bitsBundleParams, demodParams = demodParams, viterbiParams = viterbiParams)) {
      c => new RXTester(c, trials)
    }
  }
}

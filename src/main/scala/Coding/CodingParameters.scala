package modem

import chisel3._
import chisel3.util._
import chisel3.experimental.FixedPoint
import dsptools.numbers._

// Written by Kunmo Kim : kunmok@berkeley.edu
// Description: This code contains a group of parameters used for 802.11a Convolutional encoding and Viterbi decoding
trait CodingParams[T <: Data] extends BitsBundleParams[T] {
  val k: Int                        // size of smallest block of input bits
  val n: Int                        // size of smallest block of output bits
  val m: Int                        // number of memory elements. Constraint length is defined as K=m+1
  val K: Int                        // Constraint length
  val L: Int                        // Survivor path memory length. 6*K for hard-decision, 12*K for soft-decision
  val O: Int                        // Coded bits per OFDM symbol: 48, 96, 192, 288
  val D: Int                        // Viterbi decoder traceback depth.
  val H: Int                        // 802.11a Header length
  val nStates: Int                  // number of states
  val genPolynomial: List[Int]      // Matrix contains the generator polynomial
//  val CodingScheme: Int             // 0: Convolutional Coding, 1: Turbo Coding, 2: LDPC
//  val fbPolynomial: List[Int]       // feedback generator polynomial for Recursive Systematic Coding (Turbo Code) -> currently not supporting
  val tailBitingEn: Boolean         // 0: disable tail-biting, 1: enable tail-biting
  val tailBitingScheme: Int         // 0: zero tail-biting. 1: sophisticated tail-biting
  val numInputs: Int
  val pmBits: Int
  val softDecision: Boolean       // will always perform soft-decoding
}

case class FixedCoding(
  k: Int = 1,
  n: Int = 2,
  K: Int = 3,
  L: Int = 40,
  O: Int = 48,
  D: Int = 36,                            // D needs to be larger than 4 in current architecture
  H: Int = 24,                            // Header length after encoding
  genPolynomial: List[Int] = List(7, 5),  // generator polynomial
//  CodingScheme: Int = 0,
//  fbPolynomial: List[Int] = List(0),
  tailBitingEn: Boolean = false,
  tailBitingScheme: Int = 0,
  protoBitsWidth: Int = 16,
  bitsWidth: Int = 48,
  softDecision: Boolean = false,
) extends CodingParams[FixedPoint] {
  val protoBits = FixedPoint(protoBitsWidth.W, (protoBitsWidth-2).BP)
  val m = K - 1
  val nStates = math.pow(2.0, m.asInstanceOf[Double]).asInstanceOf[Int]
  val numInputs   = math.pow(2.0, k.asInstanceOf[Double]).asInstanceOf[Int]
  val pmBits = 5
}

case class HardCoding(
  k: Int = 1,
  n: Int = 2,
  K: Int = 3,
  L: Int = 40,
  O: Int = 48,
  D: Int = 36,                            // D needs to be larger than 4 in current architecture
  H: Int = 24,                            // Header length after encoding
  genPolynomial: List[Int] = List(7, 5),  // generator polynomial
  //  CodingScheme: Int = 0,
  //  fbPolynomial: List[Int] = List(0),
  tailBitingEn: Boolean = false,
  tailBitingScheme: Int = 0,
  protoBitsWidth: Int = 16,
  bitsWidth: Int = 48,
  softDecision: Boolean = false,
) extends CodingParams[SInt] {
  val protoBits = SInt(2.W)
  val m = K - 1
  val nStates = math.pow(2.0, m.asInstanceOf[Double]).asInstanceOf[Int]
  val numInputs   = math.pow(2.0, k.asInstanceOf[Double]).asInstanceOf[Int]
  val pmBits = 5
}
class MACctrl[T <: Data](params: CodingParams[T]) extends Bundle {
  val isHead      = Input(Bool())                   // indicate whether the current block is header
  val puncMatrix  = Input(Vec(4, UInt(1.W)))        // from MAC layer
  override def cloneType: this.type = MACctrl(params).asInstanceOf[this.type]
}
object MACctrl {
  def apply[T <: Data](params: CodingParams[T]): MACctrl[T] = new MACctrl(params)
}

class DecodeHeadBundle[T <: Data]() extends Bundle {
  val rate: Vec[UInt] = Vec(4, UInt(1.W))
  val dataLen: UInt   = UInt(12.W)

  override def cloneType: this.type = DecodeHeadBundle().asInstanceOf[this.type]
}
object DecodeHeadBundle {
  def apply[T <: Data](): DecodeHeadBundle[T] = new DecodeHeadBundle()
}

class DecodeDataBundle[T <: Data]() extends Bundle {
  val cntLen: UInt   = UInt(12.W)

  override def cloneType: this.type = DecodeDataBundle().asInstanceOf[this.type]
}
object DecodeDataBundle {
  def apply[T <: Data](): DecodeDataBundle[T] = new DecodeDataBundle()
}

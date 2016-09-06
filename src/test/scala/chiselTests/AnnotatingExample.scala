// See LICENSE for license details.

package chiselTests

import chisel3._
import chisel3.core.{Annotation, Module}
import chisel3.testers.BasicTester
import org.scalatest._

//scalastyle:off magic.number

/**
  * This Spec file illustrates use of Donggyu's component name API, it currently only
  * uses three methods .signalName, .parentModName and .pathName
  *
  * This is also an illustration of how to implement an annotation system in chisel3
  * A local (my) Driver and Builder are created to provide thread-local access to
  * an annotation map, and then a post elaboration annotation processor can resolve
  * the keys and could serialize the annotations to a file for use by firrtl passes
  */
case class ExampleRelative(value: String) extends Annotation.Value with Annotation.Relative
case class ExampleAbsolute(value: String) extends Annotation.Value with Annotation.Absolute

class SomeSubMod(param1: Int, param2: Int) extends Module {
  val io = new Bundle {
    val in = UInt(INPUT, 16)
    val out = SInt(OUTPUT, 32)
  }
  annotate(this, ExampleAbsolute(s"SomeSubMod($param1, $param2)"))
  annotate(io.in, ExampleRelative("sub mod io.in"))
  annotate(io.out, ExampleAbsolute("sub mod io.out"))
}

class AnnotatingExample extends Module {
  val io = new Bundle {
    val a  = UInt(INPUT, 32)
    val b  = UInt(INPUT, 32)
    val e  = Bool(INPUT)
    val z  = UInt(OUTPUT, 32)
    val v  = Bool(OUTPUT)
    val bun = new Bundle {
      val nested_1 = UInt(INPUT, 12)
      val nested_2 = Bool(OUTPUT)
    }
  }
  val x = Reg(UInt(width = 32))
  val y = Reg(UInt(width = 32))

  val subModule1 = Module(new ModC(1, 2))
  val subModule2 = Module(new ModC(3, 4))


  annotate(subModule2, ExampleRelative("SomeSubMod was used"))

  annotate(x, ExampleAbsolute("I am register X"))
  annotate(y, ExampleRelative("I am register Y"))
  annotate(io.a, ExampleAbsolute("I am io.a"))
  annotate(io.bun.nested_1, ExampleRelative("I am io.bun.nested_1"))
  annotate(io.bun.nested_2, ExampleAbsolute("I am io.bun.nested_2"))
}

class AnnotatingExampleTester extends BasicTester {
  val dut = Module(new AnnotatingExample)

  stop()
}

class AnnotatingExampleSpec extends FlatSpec with Matchers {
//  behavior of "Annotating components of a circuit"
//
//  def hasComponent(name: String, annotations: Seq[Annotation.Resolved]): Boolean = {
//    annotations.exists { annotation =>
//      annotation.componentName == name }
//  }
//  def valueOf(name: String, annotations: Seq[Annotation.Resolved]): Option[String] = {
//    annotations.find { annotation => annotation.componentName == name } match {
//      case Some(Annotation.Resolved(_, ExampleAbsolute(value))) => Some(value)
//      case Some(Annotation.Resolved(_, ExampleRelative(value))) => Some(value)
//      case _ => None
//    }
//  }
//
//  it should "contain the following relative keys" in {
//    val circuit = Driver.elaborate{ () => new AnnotatingExampleTester }
//    val annotations = circuit.annotations
//
//     Driver.dumpFirrtl(circuit)
//    // Driver.dumpFirrtl(circuit, Some(new java.io.File("./aaa_file.fir")))
//    // Driver.dumpFirrtl(circuit, Some(new java.io.File("./aaa_file.blur")))
//
//    hasComponent("SomeSubMod.io.in", annotations) should be (true)
//    hasComponent("AnnotatingExample.y", annotations) should be (true)
//
//    valueOf("SomeSubMod.io.in", annotations) should be (Some("sub mod io.in"))
//  }
//  it should "contain the following absolute keys" in {
//    val annotations = Driver.elaborate{ () => new AnnotatingExampleTester }.annotations
//
//    hasComponent("AnnotatingExampleTester.dut.subModule2.io.out", annotations) should be (true)
//    hasComponent("AnnotatingExampleTester.dut.x", annotations) should be (true)
//
//    assert(valueOf("AnnotatingExampleTester.dut.subModule2.io.out", annotations) === (Some("sub mod io.out")))
//  }
}
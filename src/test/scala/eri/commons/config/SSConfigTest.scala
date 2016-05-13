package eri.commons.config

import java.io.File
import java.nio.file.{Paths, Path}
import java.time.Duration

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.FunSpec

/**
 * Test rig for Scala Simple Config.
 * 
 * @author <a href="mailto:fitch@datamininglab.com">Simeon H.K.Fitch</a>
 * @since 5/12/16
 */
class SSConfigTest extends FunSpec {
  describe("initialization") {
    it("should allow creation from root config") {
      val conf = new SSConfig()
      assert(conf.ints.fortyTwoAgain.as[Int] === 42)
    }
    it("should allow creation from nested config") {
      val conf = new SSConfig("floats")
      assert(conf.fortyTwoPointOne.as[Float] === 42.1f)
    }
    it("should allow creation from externally configured config") {
      import scala.collection.JavaConverters._
      val inline = Map("one" -> Int.box(1), "two" -> Int.box(2)).asJava
      val conf = new SSConfig("", ConfigFactory.parseMap(inline))
      assert(conf.two.as[Int] === 2)
    }
  }
  describe("configuration types") {
    val conf = new SSConfig()
    it("should support integers") {
      assert(conf.ints.fortyTwo.as[Int] === 42)
      assert(conf.ints.fortyTwo.as[Long] === 42l)
    }
    it("should support floating point numbers") {
      assert(conf.floats.pointThirtyThree.as[Float] === 0.33f)
      assert(conf.floats.pointThirtyThree.as[Double] === 0.33)
    }
    it("should support strings") {
      assert(conf.strings.concatenated.as[String] === "null bar 42 baz true 3.14 hi")
    }
    it("should support durations") {
      assert(conf.durations.halfSecond.as[Duration] === Duration.ofMillis(500))
    }
    it("should support sizes") {
      assert(conf.memsizes.meg.asSize === 1024 * 1024)
    }
    it("should support paths") {
      assert(conf.system.userhome.as[Path] === Paths.get(sys.props("user.home")))
      assert(conf.system.userhome.as[File] === Paths.get(sys.props("user.home")).toFile)
    }
  }
  describe("behavior of missing config values") {
    val conf = new SSConfig()
    it("should support `Some`") {
      assert(conf.system.javaversion.asOption[String] === Some(sys.props("java.version")))
    }
    it("should support `None`") {
      assert(conf.am.not.here.asOption[Int] === None)
    }
    it("should support default `ConfigException` behavior") {
      intercept[ConfigException] {
        conf.system.oops.as[String]
      }
    }
  }
}
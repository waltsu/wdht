package com.waltsu.wdht

import org.junit.runner.RunWith
import org.specs2.mutable
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StartSpec extends mutable.Specification {
  "test" should {
    "execute something" in {
      1 should equalTo(1)
    }
  }
}

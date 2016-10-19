package com.waltsu.wdht.testhelpers

import org.specs2.mutable.BeforeAfter
import com.waltsu.wdht.storage.{Migration, StorageEngine}

trait CleanDatabase extends BeforeAfter {
  def before = {
    Migration().migrate()
  }

  def after = {
    StorageEngine.reset()
  }


}

package com.waltsu.wdht

import slick.driver.SQLiteDriver.api._

object StorageEngine {
  val database = Database.forConfig("storageDatabase")
  implicit val session = database.createSession()
}

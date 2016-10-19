package com.waltsu.wdht.tables

import com.waltsu.wdht.models.StoredObject
import slick.driver.SQLiteDriver.api._
import slick.lifted.Tag

class StoredObjectTable(tag: Tag) extends Table[StoredObject](tag, "objects") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def key = column[String]("key")
  def value = column[String]("value")

  def * = (id, key, value) <> ((StoredObject.apply _).tupled, StoredObject.unapply)
}

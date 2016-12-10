package com.waltsu.wdht.storage.tables

import com.waltsu.wdht.storage.models.StoredObject
import slick.driver.SQLiteDriver.api._
import slick.lifted.Tag

object StoredObjectTable {
  val storedObjects = TableQuery[StoredObjectTable]

  def returningStoredObjects = (storedObjects returning storedObjects.map(_.id)) into ((`object`, id) => `object`.copy(id=id))

}
class StoredObjectTable(tag: Tag) extends Table[StoredObject](tag, "objects") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def key = column[String]("key")
  def value = column[String]("value")

  def * = (id, key, value) <> ((StoredObject.apply _).tupled, StoredObject.unapply)
}

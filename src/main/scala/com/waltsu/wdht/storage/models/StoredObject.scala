package com.waltsu.wdht.storage.models

import play.api.libs.json.Json

object StoredObject {
  implicit val storedObjectFormat = Json.format[StoredObject]
}
case class StoredObject(id: Int, key: String, value: String)

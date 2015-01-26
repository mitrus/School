package models

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 * Created by mitrus on 1/26/15.
 */
case class SecretKey( key:          String,
                      form:         String,
                      school:       String,
                      master:       String)

object SecretKey {
  implicit object SecretKeyBSONWriter extends BSONDocumentWriter[SecretKey] {
    def write(obj: SecretKey): BSONDocument = {
      BSONDocument(
        "key" -> obj.key,
        "form" -> obj.form,
        "school" -> obj.school,
        "master" -> obj.master
      )
    }
  }

  implicit object SecretKeyBSONReader extends BSONDocumentReader[SecretKey] {
    def read(bson: BSONDocument): SecretKey = {
      SecretKey(
        bson.getAs[String]("key").get,
        bson.getAs[String]("form").get,
        bson.getAs[String]("school").get,
        bson.getAs[String]("master").get
      )
    }
  }
}

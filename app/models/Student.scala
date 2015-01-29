package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

/**
 * Created by mitrus on 1/25/15.
 */
case class Student( master:     String,
                    form:       String) extends User

object Student {
  implicit object StudentBSONWriter extends BSONDocumentWriter[Student] {
    def write(obj: Student): BSONDocument = {
      val bson = BSONDocument(
        "master" -> obj.master,
        "form"   -> obj.form
      )
      bson
    }
  }

  implicit object StudentBSONReader extends BSONDocumentReader[Student] {
    def read(bson: BSONDocument): Student = {
      Student(
        bson.getAs[String]("master").get,
        bson.getAs[String]("form").get
      )
    }
  }
}

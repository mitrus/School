package models

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 * Created by mitrus on 2/1/15.
 */
case class Form( form:     String,
                 students: List[String])

object Form {
  implicit object FormBSONWriter extends BSONDocumentWriter[Form] {
    def write(obj: Form): BSONDocument = {
      BSONDocument(
        "form" -> obj.form,
        "students" -> obj.students
      )
    }
  }

  implicit object FormBSONReader extends BSONDocumentReader[Form] {
    def read(bson: BSONDocument): Form = {
      Form(
        bson.getAs[String]("form").get,
        bson.getAs[List[String]]("students").get
      )
    }
  }
}

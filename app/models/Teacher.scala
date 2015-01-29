package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

/**
 * Created by mitrus on 1/25/15.
 */
case class Teacher( subjects:   List[String],
                    master:     Option[String]) extends User

object Teacher {
  implicit object TeacherBSONWriter extends BSONDocumentWriter[Teacher] {
    def write(obj: Teacher) = {
      val bson = BSONDocument(
        "subjects"  -> obj.subjects
      )
      obj.master.map { master =>
        bson.add(BSONDocument("master" -> master))
      } getOrElse {
        bson
      }
    }
  }

  implicit object TeacherBSONReader extends BSONDocumentReader[Teacher] {
    def read(bson: BSONDocument) = {
      Teacher(
        bson.getAs[List[String]]("subjects").get,
        bson.getAs[String]("master")
      )
    }
  }
}
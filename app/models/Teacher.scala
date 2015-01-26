package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

/**
 * Created by mitrus on 1/25/15.
 */
case class Teacher( school:     String,
                    subjects:   List[String],
                    master:     Option[String]) extends User

object Teacher {
  implicit object TeacherBSONWriter extends BSONDocumentWriter[Teacher] {
    def write(obj: Teacher) = {
      val bson = BSONDocument(
        "school"    -> obj.school,
        "subjects"  -> obj.subjects
      )
      obj.master.foreach { master =>
        bson.add(BSONDocument("master" -> master))
      }
      bson
    }
  }

  implicit object TeacherBSONReader extends BSONDocumentReader[Teacher] {
    def read(bson: BSONDocument) = {
      Teacher(
        bson.getAs[String]("school").get,
        bson.getAs[List[String]]("subjects").get,
        bson.getAs[String]("master")
      )
    }
  }
}
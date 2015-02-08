package models

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}

/**
 * Created by mitrus on 2/8/15.
 */
case class Subject( subject:      String,
                    teachers:     List[String])
object Subject {
  implicit object SubjectBSONWriter extends BSONDocumentWriter[Subject] {
    def write(obj: Subject): BSONDocument = {
      BSONDocument(
        "subject" -> obj.subject,
        "teachers" -> obj.teachers
      )
    }
  }

  implicit object SubjectBSONReader extends BSONDocumentReader[Subject] {
    def read(bson: BSONDocument): Subject = {
      Subject(
        bson.getAs[String]("subject").get,
        bson.getAs[List[String]]("teachers").get
      )
    }
  }
}

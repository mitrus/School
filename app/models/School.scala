package models

import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by mitrus on 1/23/15.
 */
case class School( number:     String,
                   forms:      List[Form],
                   subjects:   List[Subject])

object School extends Controller with MongoController {
  implicit object SchoolBSONWriter extends BSONDocumentWriter[School] {
    def write(school: School) = {
      val bson = BSONDocument(
        "number" -> school.number,
        "forms" -> school.forms,
        "subjects" -> school.subjects
      )
      bson
    }
  }

  implicit object SchoolBSONReader extends BSONDocumentReader[School] {
    def read(doc: BSONDocument) = {
      School(
        doc.getAs[String]("number").get,
        doc.getAs[List[Form]]("forms").get,
        doc.getAs[List[Subject]]("subjects").get
      )
    }
  }

  def getSchoolList: Future[List[String]] = {
    val schoolsDB = db.collection[BSONCollection]("schools")
    val schoolList =
      schoolsDB.
        find(BSONDocument()).
        cursor[School].
        collect[List]()
    schoolList.map(_.map(_.number))
  }
}

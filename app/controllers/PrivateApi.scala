package controllers

import controllers.SchoolController._
import jp.t2v.lab.play2.auth.{AuthElement}
import models.{Subject, Form, School, SchoolPermission}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by antonk on 02.02.15.
 */
object PrivateApi extends Controller with MongoController with AuthElement with AuthConfigImpl {
  def addFormToSchool(form: String) = AsyncStack(AuthorityKey -> SchoolPermission) { implicit request =>
    // TODO: Implement adding form to school db.
    val schoolsDB = db.collection[BSONCollection]("schools")
    val query = BSONDocument(
      "number" -> loggedIn.school
    )
    val findSchool =
      schoolsDB.find(query).
      one[School]
    findSchool.flatMap {
      case Some(school) => {
        val formObj = Form(form, Nil)
        val updateQuery = BSONDocument(
          "$push" -> BSONDocument(
            "forms" -> formObj
          )
        )
        schoolsDB.update(query, updateQuery).map { _ =>
          Ok("Form Added.")
        }
      }
      case None => {
        Future.successful(BadRequest("No such school."))
      }

    }
  }

  def addSubjectToSchool(subject: String) = AsyncStack(AuthorityKey -> SchoolPermission) { implicit request =>
    val schoolsDB = db.collection[BSONCollection]("schools")
    val query = BSONDocument(
      "number" -> loggedIn.school
    )
    val findSchool =
      schoolsDB.find(query).
        one[School]
    findSchool.flatMap {
      case Some(school) => {
        val subjectObj = Subject(subject, Nil)
        val updateQuery = BSONDocument(
          "$push" -> BSONDocument(
            "subjects" -> subjectObj
          )
        )
        schoolsDB.update(query, updateQuery).map( _ =>
          Ok("Subject Added.")
        )
      }
      case None => {
        Future.successful(BadRequest("No such school."))
      }
    }
  }
}

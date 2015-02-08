package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{School, SchoolPermission}
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SchoolController extends Controller with MongoController with AuthElement with AuthConfigImpl {



  def students = AsyncStack(AuthorityKey -> SchoolPermission) { implicit request =>
    val schoolsDB = db.collection[BSONCollection]("schools")
    schoolsDB
    .find(BSONDocument(
      "number" -> loggedIn.school
    ))
      .one[School]
      .map {
      case Some(school) =>
        Ok(views.html.students(loggedIn, school.forms.map(_.form).sorted))
      case None =>
        Ok("Current school is not in DB.")
      }
  }

  def subjects = AsyncStack(AuthorityKey -> SchoolPermission) { implicit request =>
    val schoolsDB = db.collection[BSONCollection]("schools")
    schoolsDB
      .find(BSONDocument(
        "number" -> loggedIn.school
      ))
      .one[School]
      .map {
      case Some(school) =>
        Ok(views.html.subject_school(loggedIn, school.subjects.map(_.subject).sorted))
      case None =>
        Ok("Current school is not in DB.")
    }
  }
}

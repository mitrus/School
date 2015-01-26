package controllers

import controllers.Application._
import jp.t2v.lab.play2.auth.AuthElement
import models.{School, AdministratorPermission}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{WebSocket, Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by mitrus on 1/22/15.
 */
object Administrator extends Controller with MongoController with AuthElement with AuthConfigImpl {

  case class SchoolData(number: String)

  val addSchoolForm = Form(
    mapping(
      "number" -> text
    )(SchoolData.apply)(SchoolData.unapply)
  )

  def schools = StackAction(AuthorityKey -> AdministratorPermission) { implicit request =>
    Ok(views.html.schools(loggedIn))
  }

  def addSchool = StackAction(AuthorityKey -> AdministratorPermission) { implicit request =>
    addSchoolForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.schools(loggedIn)),
      schoolNumber => {
        // TODO: Check weather the school is already exists.
        val schoolDB = db.collection[BSONCollection]("schools")
        schoolDB.insert(School(schoolNumber.number, Nil))
        Redirect(routes.Administrator.schools)
      }
    )
  }
}

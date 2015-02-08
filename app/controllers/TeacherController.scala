package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.{Account, SecretKey, Teacher, TeacherPermission}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import school.Utilities

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

/**
 * Created by mitrus on 1/27/15.
 */
object TeacherController extends Controller with MongoController with AuthElement with AuthConfigImpl {

  case class BecomeMasterData(form: String)

  val becomeMasterForm = Form(
    mapping(
      "form" -> text
    )(BecomeMasterData.apply)(BecomeMasterData.unapply)
  )

  def becomeMaster = AsyncStack(AuthorityKey -> TeacherPermission) { implicit request =>
    becomeMasterForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.html.masterPage("Error in form!", loggedIn, loggedIn.user.get.asInstanceOf[Teacher]))),
      data => {
        // TODO: check whether this form exists.
        loggedIn.user.get.asInstanceOf[Teacher].master match {
          case None => {
            val secretkeysDB = db.collection[BSONCollection]("secretkeys")
            val usersDB = db.collection[BSONCollection]("users")
            val query = BSONDocument(
              "form" -> data.form,
              "school" -> loggedIn.school
            )
            val secretKeyForForm = secretkeysDB.
              find(query).
              one[SecretKey]
            secretKeyForForm.flatMap {
              case None => {
                val key = Utilities.randomAlphanumericString(6)
                val insertSK = secretkeysDB.insert(SecretKey(key, data.form, loggedIn.school, loggedIn.id))
                insertSK.flatMap { _ =>
                  val newAccount = loggedIn.copy(user = Some(loggedIn.user.get.asInstanceOf[Teacher].copy(master = Some(data.form))))
                  usersDB.update(BSONDocument("id" -> loggedIn.id), newAccount).map { x =>
                    Ok(views.html.masterPage("Secret key " + key + " has been added!", loggedIn, loggedIn.user.get.asInstanceOf[Teacher]))
                  }
                } recover {
                  case _ => Ok(views.html.masterPage("Error in generation secret key!", loggedIn, loggedIn.user.get.asInstanceOf[Teacher]))
                }
              }
              case _ => {
                Future.successful {Ok(views.html.masterPage("This form already has a master!", loggedIn, loggedIn.user.get.asInstanceOf[Teacher]))}
              }
            }
          }
          case _ => {
            Future.successful {Ok(views.html.masterPage("You're already a master!", loggedIn, loggedIn.user.get.asInstanceOf[Teacher]))}
          }
        }


      }
    )
  }

  def masterPage = StackAction(AuthorityKey -> TeacherPermission) { implicit request =>
    val teacherInfo = loggedIn.user.get.asInstanceOf[Teacher]
    Ok(views.html.masterPage("", loggedIn, teacherInfo))
  }
}

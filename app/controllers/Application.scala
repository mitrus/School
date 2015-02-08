package controllers

import jp.t2v.lab.play2.auth.{OptionalAuthElement, AuthElement, LoginLogout}
import models.{TeacherPermission, StudentPermission, SecretKey, Account}
import org.mindrot.jbcrypt.BCrypt
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import school.Utilities

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller with MongoController with OptionalAuthElement with AuthConfigImpl with LoginLogout {

  //with LoginLogout with AuthConfigImpl {

  case class SignUpStudentData(email: String, password: String, name: String, secretKey: String)
  case class SignUpTeacherData(email: String, password: String, name: String, school: String)

  case class SignInUserData(email: String, password: String)

  val signUpStudentForm = Form(
    mapping(
      "email" -> text,
      "password" -> text,
      "name" -> text,
      "secretkey" -> text
    )(SignUpStudentData.apply)(SignUpStudentData.unapply)
  )

  val signUpTeacherForm = Form(
    mapping(
      "email" -> text,
      "password" -> text,
      "name" -> text,
      "school" -> text
    )(SignUpTeacherData.apply)(SignUpTeacherData.unapply)
  )

  val signInForm = Form(
    mapping(
      "email" -> text,
      "password" -> text
    )(SignInUserData.apply)(SignInUserData.unapply)
  )

  def index = StackAction { implicit request =>
    Ok(views.html.index("dsd", loggedIn))
  }

  def signIn = StackAction { implicit request =>
    loggedIn.fold(ifEmpty = {
      Ok(views.html.signIn("SignIn"))
    })(_ => Redirect(routes.Application.index))
  }

  def authenticate = Action.async { implicit request =>
    signInForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.html.signIn("Errors"))),
      credentials    => Account.authenticate(credentials.email, credentials.password) flatMap { optUser =>
        optUser map { account =>
          gotoLoginSucceeded(account.email)
        } getOrElse {
          Future.successful(
            BadRequest(views.html.signIn("Wrong email or password")))
        }
      }
    )
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  def signUp = StackAction { implicit request =>
    loggedIn.fold(ifEmpty = {
      Ok(views.html.signUp("Register"))
    })(_ => Redirect(routes.Application.index))
  }

  def register(permission: String) = AsyncStack { implicit request =>
    permission match {
      case "student" => signUpStudentForm.bindFromRequest().fold(
        formWithErrors => Future.successful(Ok(views.html.index("BAD REQUEST", loggedIn))),
        studentData => {
          // TODO: change salt generator
          val salt = BCrypt.gensalt()
          val passwordHash = studentData.password //BCrypt.hashpw(userData.password, salt)
          val usersDB = db.collection[BSONCollection]("users")
          val secretKeysDB = db.collection[BSONCollection]("secretkeys")

          val query = BSONDocument(
            "email" -> studentData.email)
          val secretQuery = BSONDocument(
            "key" -> studentData.secretKey
          )
          var usedName = usersDB.find(query).one[BSONDocument]
          var optSecretKey = secretKeysDB.find(secretQuery).one[SecretKey]
          usedName.flatMap {
            case Some(_) => Future.successful { Ok(views.html.index("email exists", loggedIn)) }
            case None => {
              optSecretKey.flatMap {
                case None => Future.successful { Ok(views.html.index("secret key incorrect", loggedIn)) }
                case Some(key) => {
                  val insertUser = usersDB.insert(Account(Utilities.randomAlphanumericString(10), studentData.email, passwordHash, studentData.name, key.school, StudentPermission,
                    Some(models.Student(key.master, key.form))))
                  insertUser.map(_ => Ok(views.html.index("OK", loggedIn)))
                }
              }

            }
          }
        }
      )

      case "teacher" => signUpTeacherForm.bindFromRequest().fold(
        formWithErrors => Future.successful(Ok(views.html.index("BAD REQUEST", loggedIn))),
        teacherData => {
          // TODO: change salt generator
          val salt = BCrypt.gensalt()
          val passwordHash = teacherData.password //BCrypt.hashpw(userData.password, salt)
          val users = db.collection[BSONCollection]("users")
          val query = BSONDocument(
            "email" -> teacherData.email)
          var usedName = users.find(query).cursor[BSONDocument].collect[List]()
          usedName.flatMap { names =>
            if (names.length != 0)
              Future.successful {Ok(views.html.index("email exists", loggedIn)) }
            else {
              val insertUser = users.insert(Account(Utilities.randomAlphanumericString(10), teacherData.email, passwordHash, teacherData.name, teacherData.school, TeacherPermission,
                Some(models.Teacher(Nil, None))))
              insertUser.map(_ => Ok(views.html.index("OK", loggedIn)))
            }
          }
        }
      )
    }

  }


}

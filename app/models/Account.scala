package models
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.Controller
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import scalikejdbc._
import play.modules.reactivemongo.MongoController
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by antonk on 05.01.15.
 */
case class Account( id:           String,
                    email:        String,
                    passwordHash: String,
                    name:         String,
                    school:       String,
                    permission:   UserPermission,
                    user:         Option[User])

object Account extends Controller with MongoController {
  implicit object AccountBSONWriter extends BSONDocumentWriter[Account] {
    def write(account: Account) = {
      val optUser = account.user.map {
        case student: Student =>
          Student.StudentBSONWriter.write(student)
        case teacher: Teacher =>
          Teacher.TeacherBSONWriter.write(teacher)
      }

      val bson = BSONDocument(
        "id" -> account.id,
        "email" -> account.email,
        "password_hash" -> account.passwordHash,
        "name" -> account.name,
        "school" -> account.school,
        "permission" -> UserPermission.stringify(account.permission)
      )
      optUser.map { user =>
        bson.add("user" -> user)
      } getOrElse {
        bson
      }
    }
  }

  implicit object AccountBSONReader extends BSONDocumentReader[Account] {
    def read(doc: BSONDocument): Account = {
      val permission = UserPermission.valueOf(doc.getAs[String]("permission").get)
      Account(
        doc.getAs[String]("id").get,
        doc.getAs[String]("email").get,
        doc.getAs[String]("password_hash").get,
        doc.getAs[String]("name").get,
        doc.getAs[String]("school").get,
        permission,
        permission match {
          case StudentPermission =>
            doc.getAs[Student]("user")
          case TeacherPermission =>
            doc.getAs[Teacher]("user")
          case _ =>
            None
        })
    }
  }

  def findById(id: String): Future[Option[Account]] = {
    val users = db.collection[BSONCollection]("users")
    users.
      find(BSONDocument("email" -> id)).
      one[Account]
  }

  def authenticate(email: String, password: String): Future[Option[Account]] = {
    val users = db.collection[BSONCollection]("users")
    users.
      find(BSONDocument("email" -> email, "password_hash" -> password)).
      one[Account]
  }
}
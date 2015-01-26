package controllers

import actors.SearchSchoolActor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.{SearchFeed, StartSearch, UpdateSchoolList}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by mitrus on 1/25/15.
 */
object Api extends Controller with MongoController with OptionalAuthElement with AuthConfigImpl {
  implicit val timeout = Timeout(5 seconds)

  val searchActor = Akka.system.actorOf(Props[SearchSchoolActor])

  def searchSchool(prefix: String) = AsyncStack { implicit request =>
    // TODO: Set a scheduler for updating.

    searchActor ! UpdateSchoolList
    (searchActor ? StartSearch(prefix)).map {
      case SearchFeed(out) => Ok(Json.toJson(out))
      case _ => BadRequest("Bad request")
    }
  }

}

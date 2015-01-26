package controllers

import actors.SearchSchoolActor
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import jp.t2v.lab.play2.auth.{AuthElement, OptionalAuthElement}
import models.{SearchFeed, StartSearch, UpdateSchoolList}
import play.api.libs.concurrent.Akka
import play.api.libs.json.Json
import play.api.Play.current
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Students extends Controller with MongoController with AuthElement with AuthConfigImpl {

}

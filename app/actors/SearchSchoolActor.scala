package actors

import akka.actor.Actor
import models.{UpdateSchoolList, School, SearchFeed, StartSearch}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by mitrus on 1/24/15.
 */
class SearchSchoolActor extends Actor {

  var schoolList: List[String] = Nil

  def receive = {
    case startSearch: StartSearch => sender ! SearchFeed(startSearching(startSearch.prefix))
    case UpdateSchoolList => updateSchoolInfo
  }

  private def updateSchoolInfo = {
    School.getSchoolList.onComplete {
      case Success(ans) => {
        schoolList = ans
      }
      case Failure(e) => {
        println("Failure in updating school list for a query.\n For error:" + e)
      }
    }
  }

  private def startSearching(prefix: String): List[String] = {
    schoolList.filter(_.startsWith(prefix))
  }
}

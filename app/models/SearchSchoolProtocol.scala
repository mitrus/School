package models

import akka.actor.Actor

case class StartSearch(prefix: String)

case class SearchFeed(out: List[String])

case object UpdateSchoolList
package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.{Comment, Discussion}
import Discussion._

class AppController extends Controller {

  def index = Assets.at(path = "/public", file = "index.html")

  def discussionPage = Assets.at(path = "/public", file = "discussion.html")

  def discussion(uuid: String) = Action.async {
    get(uuid).map { d => Ok(Json.toJson(d))}
  }

  //TODO: reaction on db update
  def addOpinion = Action { req =>
    req.body.asJson match {
      case Some(json) =>
        val (uuid, comment) = json.as[(String, Comment)]
        addComment(uuid, comment)
        Ok(Json.toJson("successful"))

      case None => BadRequest
    }
  }


}

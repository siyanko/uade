package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.Comment
import services.Discussion._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AppController extends Controller {

  def index = Assets.at(path = "/public", file = "index.html")

  def discussionPage = Assets.at(path = "/public", file = "discussion.html")

  def discussion(uuid: String) = Action.async {
    get(uuid).map { d => Ok(Json.toJson(d)) }
  }

  def addOpinion = Action.async { req =>
    req.body.asJson match {
      case Some(json) =>
        convertJson(json) match {
          case Success((uuid, comment)) => addComment(uuid, comment).map {
            case _ => Ok(Json.toJson("successful"))
          }.recover {
            case _ => InternalServerError
          }

          case Failure(ex) => Future {
            BadRequest(Json.toJson(ex.getMessage))
          }
        }

      case None => Future {
        BadRequest("Expected application/json content")
      }
    }
  }

  private def convertJson(json: JsValue): Try[(String, Comment)] = Try {
    json.as[(String, Comment)]
  }


}

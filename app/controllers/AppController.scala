package controllers

import play.api.mvc.Controller

/**
  * A very small controller that renders a home page.
  */
class AppController extends Controller {

  def index = Assets.at (path = "/public", file = "index.html")

}

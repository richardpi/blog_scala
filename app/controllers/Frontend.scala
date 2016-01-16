package controllers

import views._
import play.api.mvc._
import models.{DAOComponent, DAO}
import java.util.concurrent.TimeoutException
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

class Frontend(dao: DAOComponent) extends Controller {

  def list: Action[AnyContent] = Action.async { implicit request =>

    dao.list.map { list =>
      val listSorted = list.sortBy(_.date).reverse
      Ok(html.frontend.list(listSorted))
    }.recover {
      case ex: TimeoutException =>
        Logger.error("Problem found in post list process")
        InternalServerError(ex.getMessage)
    }
  }

  def view(id: Long): Action[AnyContent] = Action.async { implicit request =>

    dao.findById(id).map(post => Ok(html.frontend.view(id, post))).recover {
      case ex: TimeoutException =>
        Logger.error("Problem found in post view process")
        InternalServerError(ex.getMessage)
    }

  }

  def about(): Action[AnyContent] = Action { implicit request =>
    Ok(html.frontend.about())
  }
}

object Frontend extends Frontend(DAO)
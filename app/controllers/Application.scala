package controllers

import views._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{DAOComponent, DAO, Post}
import java.util.concurrent.TimeoutException
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

/**
 * Manage a database of blog posts
 */
class Application(dao: DAOComponent) extends Controller with Secured {

  /**
   * Redirect to the application home.
   */
  val Home = Redirect(routes.Application.list)

  /**
    * post form (used in both edit and create screens).
    */
  val postForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText,
      "date" -> default(date("dd/MM/yyyy"), new java.util.Date),
      "intro" -> nonEmptyText,
      "content" -> optional(text),
      "author" -> nonEmptyText
    )(Post.apply)(Post.unapply))

  /**
   * Display list of post entries.
   */
  def list: Action[AnyContent] = AuthenticatedAction.async { implicit request =>

    dao.list().map { list =>
      val listSorted = list.sortBy(_.date).reverse
      Ok(html.list(listSorted))
    }.recover {
      case ex: TimeoutException =>
        Logger.error("Problem found in post list process")
        InternalServerError(ex.getMessage)
    }

  }

  /**
    * Display the 'edit form' of a existing post.
    */
  def edit(id: Long): Action[AnyContent] = AuthenticatedAction.async { implicit request =>
    dao.findById(id).map(post => Ok(html.editForm(id, postForm.fill(post)))).recover {
      case ex: TimeoutException =>
        Logger.error("Problem found in post edit process")
        InternalServerError(ex.getMessage)
      }
    }

  /**
    * Handle the 'edit post form' submission
    */
  def update(id: Long): Action[AnyContent] = AuthenticatedAction.async { implicit request =>
    postForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.editForm(id, formWithErrors))),
      post => {
        val futurePostUpdate = dao.update(id, post.copy(id = Some(id)))
        futurePostUpdate.map { result =>
          Home.flashing("success" -> "Post %s has been updated".format(post.title))
        }.recover {
          case ex: TimeoutException =>
            Logger.error("Problem found in post update process")
            InternalServerError(ex.getMessage)
        }
      })
  }

  /**
    * Display the 'new post form'.
    */
  def create: Action[AnyContent] = AuthenticatedAction { implicit request =>
    val author = getAuthUser.get.name
    Ok(html.createForm(postForm, author))
  }

  /**
    * Handle the 'new post form' submission.
    */
  def save: Action[AnyContent] = AuthenticatedAction.async { implicit request =>
    val author = getAuthUser.get.name
    postForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.createForm(formWithErrors, author))),
      post => {
        val futurePostInsert = dao.insert(post)
        futurePostInsert.map { result => Home.flashing("success" -> "Post %s has been created".format(post.title)) }.recover {
          case ex: TimeoutException =>
            Logger.error("Problem found in post save process")
            InternalServerError(ex.getMessage)
        }
      })
  }

  /**
    * Handle post deletion
    */
  def delete(id: Long): Action[AnyContent] = AuthenticatedAction.async { implicit request =>
    val futurePostDel = dao.delete(id)
    futurePostDel.map { result => Home.flashing("success" -> "Post has been deleted") }.recover {
      case ex: TimeoutException =>
        Logger.error("Problem found in post delete process")
        InternalServerError(ex.getMessage)
    }
  }

}

object Application extends Application(DAO)

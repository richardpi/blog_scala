package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import views.html
import scala.concurrent.Future

import play.api.mvc.{Request, WrappedRequest}
import models.User

object Auth extends Controller {

  val defaultUser = None

  /**
    * login form
    */
  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying ("Invalid username or password", result => result match {
      case (username, password) => check(username, password)
    })
  )

  def check(username: String, password: String) = {
    User.find(username).filter(user => user.checkPassword(password)) match {
      case Some(User(_,_,_)) => true
      case _ => false
    }
  }

  /**
    * Display the 'login form'.
    */
  def login: Action[AnyContent] = Action { implicit request =>
    Ok(html.loginForm(loginForm))
  }

  def authenticate: Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.loginForm(formWithErrors)),
      user => Redirect(routes.Application.list).withSession(Security.username -> user._1)
    )
  }

  def logout: Action[AnyContent] = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }

}

trait Secured {
  def username(request: RequestHeader) = request.session.get(Security.username)

  def getAuthUser(implicit request: RequestHeader) = User.find(username(request).get)
}

class AuthenticatedRequest[A](val user: User, val request: Request[A])
  extends WrappedRequest[A](request)

object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
  def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    request.session.get("username")
      .flatMap(User.find(_))
      .map(user => block(new AuthenticatedRequest(user, request)))
      .getOrElse(Future.successful(Results.Redirect(routes.Auth.login)))
  }
}
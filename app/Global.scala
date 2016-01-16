import java.text.SimpleDateFormat
import play.api._
import models._
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    StartData.insert()
  }

}

/**
 *  Starting set of data to be inserted into the sample application.
 */

object StartData {

  val sdf = new SimpleDateFormat("dd/MM/yyyy")

  def insert(): Unit = {
    DAO.count map { size =>
      if (size == 0) {
        val posts = Seq(
          Post(Option(1L), "title of post", new java.util.Date, "intro text", Option("main content text"), "author kowalski")
        )
        posts.map(DAO.insert)
      }
    }
  }
}

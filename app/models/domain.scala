package models

import java.util.Date
import slick.lifted.Tag
import slick.driver.PostgresDriver.api._

case class Post(id: Option[Long], title: String, date: Date, intro: String, content: Option[String], author: String)

class Posts(tag: Tag) extends Table[Post](tag, "POSTS") {

  implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def date = column[Date]("date")
  def intro = column[String]("intro")
  def content = column[String]("content")
  def author = column[String]("author")

  def * = (id.?, title, date, intro, content.?, author) <> (Post.tupled, Post.unapply _)

}
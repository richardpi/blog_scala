package models

import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import play.api.db.DB
import play.api.Play.current

trait DAOComponent {

  def insert(post: Post): Future[Int]
  def update(id: Long, post: Post): Future[Int]
  def delete(id: Long): Future[Int]
  def list(): Future[Seq[Post]]
  def findById(id: Long): Future[Post]
  def count(): Future[Int]

}

object DAO extends DAOComponent {

  private val posts = TableQuery[Posts]

  private def db: Database = Database.forDataSource(DB.getDataSource())

  /**
    * Filter post with id
    */
  private def filterQuery(id: Long): Query[Posts, Post, Seq] =
    posts.filter(_.id === id)

  /**
    * Find post by id
    */
  override def findById(id: Long): Future[Post] =
    try db.run(filterQuery(id).result.head)
    finally db.close

  /**
    * Create a new post
    */
  override def insert(post: Post): Future[Int] =
    try db.run(posts += post)
    finally db.close

  /**
    * Update post with id
    */
  override def update(id: Long, post: Post): Future[Int] =
    try db.run(filterQuery(id).update(post))
    finally db.close

  /**
    * Delete post with id
    */
  override def delete(id: Long): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close


  /**
    * List of all posts
    */
  override def list(): Future[Seq[Post]] = {
    try {
      val query =
        for {
          post <- posts
        } yield (post)

      db.run(query.result)
    } finally { db.close() }
  }

  /**
    * Count total posts in database
    */
  override def count: Future[Int] =
    try db.run(posts.length.result)
    finally db.close

}

package models

object User {
  val users = List(
    User("admin", "1234", "Admin")
  )
  def find(username: String):Option[User] = users.filter(_.username == username).headOption
}

case class User(username:String, password:String, name: String) {
  def checkPassword(password: String): Boolean = this.password == password
}

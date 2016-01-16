# play-scala-blog

This is blog written in Play/Scala using Slick

To read more about the project, go to:
http://www.blog.thelogichouse.co.uk/posts/1

Prerequisites: 
* Installed PostgreSql with database called 'test' (If you don't want to use Postgres, change DB drivers in conf/application.conf)
* Installed SBT or Activator

To run the application use command:

`sbt run` or `activator run`

The project by default should be available at localhost:9000


Heroku
------

If you deploy the project on heroku, use environmental variables instead of default database settings:
db.default.url=${?DATABASE_URL}

Make sure evolutions are enabled:
applyEvolutions.default=true

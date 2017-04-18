import sbt._
import sbt.Keys._

object Aliases {

  def libs = libraryDependencies

  def root = file(".")

  def aliases(nameCommands: (String, String)*) =
    nameCommands.flatMap {
      case (name, command) =>
        addCommandAlias(name, command)
    }

  def chain(commands: String*) =
    commands.mkString(";", ";", "")

}

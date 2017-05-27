package services

object Validator {

  def isValidUserName(uName: String) =  uName.matches("^[А-Яа-я ]{1,32}$")

  def isValidComment(comment: String) = comment.matches("^[А-Яа-я .,!?_-/]{1,300}$")

}

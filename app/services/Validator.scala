package services

object Validator {

  def isValidUserName(uName: String) =  uName.matches("^[А-Яа-яєЄіІїЇ ']{1,32}$")

  def isValidComment(comment: String) = comment.matches("^[А-Яа-яєЄіІїЇ .,'!?_-]{1,300}$")

}

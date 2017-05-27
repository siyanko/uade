package services

import java.util.Date

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, AttributeValueUpdate, UpdateItemResult}
import play.api.libs.json._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

object Discussion {

  private val dynamoClient = AmazonDynamoDBAsyncClientBuilder.standard()
    .withRegion(Regions.EU_WEST_1)
    .build()

    private val dynamoDb = DynamoDb(dynamoClient)
//  private val dynamoDb = DynamoDb.local()

  implicit val commentWrites = new Writes[Comment] {
    override def writes(c: Comment): JsValue = Json.obj(
      "userName" -> c.userName,
      "time" -> DateFormatter.format(c.time),
      "message" -> c.message
    )
  }

  implicit val commentReads = new Reads[(String, Comment)] {
    override def reads(json: JsValue): JsResult[(String, Comment)] = {
      val name = (json \ "name").as[String]
      val comment = (json \ "comment").as[String]
      val uuid = (json \ "uuid").as[String]

      if (Validator.isValidUserName(name) && Validator.isValidComment(comment)){
        JsSuccess((uuid, Comment(userName = name, message = comment)))
      }else {
        JsError("User name or comment is not valid.")
      }
    }
  }

  implicit val discussionWrites = new Writes[Discussion] {
    override def writes(d: Discussion): JsValue = Json.obj(
      "id" -> d.uuid,
      "title" -> d.title,
      "date" -> DateFormatter.format(d.date),
      "comments" -> d.comments
    )
  }

  //TODO: function to convert comments from dynamodb to object
  def get(uuid: String)(implicit executor: ExecutionContext): Future[Discussion] =
    dynamoDb.getItem("Discussions", Map("uuid" -> new AttributeValue(uuid))).map(_.getItem.asScala)
      .map { case d => 
        val uuid = d("uuid").getS
        val title =  d("title").getS
        val date =  d("date").getN.toLong
        val comments = d("comments").getL.asScala.toList.map(_.getM).map(_.asScala)
          .map(m => Comment(m("userName").getS, m("time").getS.toLong, m("message").getS))
        
        Discussion(uuid, title, date, comments)
      }

  // TODO: function to convert comments from object to dynamodb
  def addComment(uuid: String, c: Comment)(implicit executor: ExecutionContext): Future[UpdateItemResult] =
    get(uuid).flatMap { d =>
      val comments: Seq[AttributeValue] = (d.comments :+ c)
        .map(c => Map("userName" -> new AttributeValue(c.userName),
          "time" -> new AttributeValue(c.time.toString),
          "message" -> new AttributeValue(c.message)))
        .map(m => new AttributeValue().withM(m.asJava))

      dynamoDb.updateItem("Discussions",
        Map("uuid" -> new AttributeValue(d.uuid)),
        Map("comments" -> new AttributeValueUpdate().withValue(new AttributeValue().withL(comments.asJava)))
      )
    }
}

case class Discussion(uuid: String, title: String, date: Long, comments: List[Comment] = List())

case class Comment(userName: String, time: Long = new Date().getTime, message: String)

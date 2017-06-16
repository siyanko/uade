package services

import java.util

import com.amazonaws.services.dynamodbv2.model.{AttributeValue, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.collection.JavaConverters._
import scala.concurrent.Await

class DynamoDbSpec extends WordSpec with Matchers with ScalaFutures with SpanSugar with BeforeAndAfterAll {

  val dynamoDb = DynamoDb.local()

  val tableName = "Discussions"

  override protected def beforeAll(): Unit = {
    val keys = List(new KeySchemaElement("uuid", KeyType.HASH))
    val attrs = List(
      new AttributeDefinition("uuid", ScalarAttributeType.S)
    )

    Await.result(dynamoDb.createTable(tableName, keys, attrs, new ProvisionedThroughput(5L, 5L)), 2 seconds)
  }

  val links = List(
    covert(
      Map("link" -> "test link", "title" -> "test link title"), (x: String) => new AttributeValue(x)
    )
  )

  val details: util.Map[String, AttributeValue] = Map(
    "description" -> new AttributeValue("test description"),
    "links" ->
      new AttributeValue().withL(
        convert(links, (m: Map[String, AttributeValue]) => new AttributeValue().withM(m.asJava)).asJava
      )).asJava

  val uuid = "88bb9936-3e58-432c-bba9-3ed5aeb7d7cb"
  val data: Map[String, AttributeValue] = Map(
    "uuid" -> new AttributeValue(uuid),
    "title" -> new AttributeValue("test title"),
    "date" -> new AttributeValue("1234567"),
    "details" -> new AttributeValue().withM(details)
  )

  def convert[A](l: List[A], f: A => AttributeValue): Seq[AttributeValue] = l.map(f(_))

  def covert[A](m: Map[String, A], f: A => AttributeValue): Map[String, AttributeValue] = m.map{
    case (k, v) => k -> f(v)
  }

  def someAttrVal(v: String) = Some(new AttributeValue(v))

  "DynamoDb" should {
    "put item into the table" in {
      whenReady(dynamoDb.putItem(tableName, data), timeout(2 seconds)) { result =>
        Option(result.getAttributes) shouldBe None
      }
    }

    "get item from the table" in {
      val attr = Map("uuid" -> new AttributeValue(uuid))
      whenReady(dynamoDb.getItem(tableName, attr), timeout(2 seconds)) { result =>
        val respAttrs = result.getItem.asScala
        respAttrs.get("date") shouldBe someAttrVal("1234567")
        respAttrs.get("title") shouldBe someAttrVal("test title")
        respAttrs.get("uuid") shouldBe someAttrVal(uuid)
        respAttrs.get("details") shouldBe Some(new AttributeValue().withM(details))
      }
    }
  }

}

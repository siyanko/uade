package services

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, CreateTableRequest, _}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBAsync, AmazonDynamoDBAsyncClientBuilder}

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}

object DynamoDb {
  def apply(client: AmazonDynamoDBAsync): DynamoDb = new DynamoDb(client)

  def local(): DynamoDb = {
    val client = AmazonDynamoDBAsyncClientBuilder.standard()
      .withEndpointConfiguration(
        new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
      .build();

    new DynamoDb(client)
  }

}

class DynamoDb(client: AmazonDynamoDBAsync) {
  def createTable(tableName: String, keys: List[KeySchemaElement],
                  attrs: List[AttributeDefinition],
                  througput: ProvisionedThroughput): Future[CreateTableResult] =
    createTable(
      new CreateTableRequest(attrs.asJava, tableName, keys.asJava, througput)
    )

  private def createTable(req: CreateTableRequest) =
    doCall(req, createTableAsync, Promise[CreateTableResult]())


  private def createTableAsync(req: CreateTableRequest, h: AsyncHandler[CreateTableRequest, CreateTableResult]): Unit =
    client.createTableAsync(req, h)

  private def putItemAsync(req: PutItemRequest, h: AsyncHandler[PutItemRequest, PutItemResult]): Unit =
    client.putItemAsync(req, h)

  private def doCall[Request <: AmazonWebServiceRequest, Result](r: Request,
                                                                 fun: (Request, AsyncHandler[Request, Result]) => Unit,
                                                                 promise: Promise[Result]): Future[Result] = {
    fun(r, new AsyncHandler[Request, Result] {
      override def onError(exception: Exception): Unit = promise.failure(exception)

      override def onSuccess(request: Request, result: Result): Unit = promise.success(result)
    })

    promise.future
  }

  def putItem(tableName: String, attrs: Map[String, AttributeValue]): Future[PutItemResult] =
    putItem(new PutItemRequest(tableName, attrs.asJava))

  private def putItem(req: PutItemRequest) = doCall(req, putItemAsync, Promise[PutItemResult]())

  def getItem(tableName: String, attrs: Map[String, AttributeValue]): Future[GetItemResult] =
    getItem(new GetItemRequest(tableName, attrs.asJava))

  private def getItemAsync(req: GetItemRequest, h: AsyncHandler[GetItemRequest, GetItemResult]): Unit =
    client.getItemAsync(req, h)

  private def getItem(req: GetItemRequest): Future[GetItemResult] = doCall(req, getItemAsync, Promise[GetItemResult]())

  def updateItem(tableName: String, keys: Map[String, AttributeValue], updates: Map[String, AttributeValueUpdate]): Future[UpdateItemResult] =
    updateItem(new UpdateItemRequest(tableName, keys.asJava, updates.asJava))

  private def updateItemAsync(req: UpdateItemRequest, h: AsyncHandler[UpdateItemRequest, UpdateItemResult]): Unit =
    client.updateItemAsync(req, h)

  private def updateItem(req: UpdateItemRequest): Future[UpdateItemResult] = doCall(req, updateItemAsync, Promise[UpdateItemResult]())

}

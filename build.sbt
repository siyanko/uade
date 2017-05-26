
scalaVersion := "2.11.11"

name := """ade-ua"""

enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.132",
  "org.scalactic" %% "scalactic" % "3.0.1",
 "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

startDynamoDBLocal := startDynamoDBLocal.dependsOn(compile in Test).value
test in Test := (test in Test).dependsOn(startDynamoDBLocal).value
testOnly in Test := (testOnly in Test).dependsOn(startDynamoDBLocal).value
testOptions in Test += dynamoDBLocalTestCleanup.value

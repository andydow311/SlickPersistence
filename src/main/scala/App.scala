import slick.jdbc.MySQLProfile.api._
import UrlMethods._
import MysqlMethods._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.collection.mutable.ListBuffer

object App extends App {

  //todo workout a way to stop system and actors automatically

  val system = ActorSystem("actorSystem")
  val actorToCreateSchema = system.actorOf(Props[SchemaCreator],"schemaCreator")
  val actorToCrawlWeb = system.actorOf(Props[Crawler],"crawler")
  val actorToInsertHorseData = system.actorOf(Props[HorseData],"horseData")
  val actorToInsertRaceData = system.actorOf(Props[RaceData],"raceData")

  //these need to be changed by user
  val createSchemaFlag = true
  var lowerLimit = "0"
  var upperLimit = "100"
  val increment = "100"
  val threshold = "10000"

  actorToCreateSchema ! createSchemaFlag

  main()

  def main(): Unit = {

    while (upperLimit.toInt <= threshold.toInt) {

      val system = ActorSystem("actorSystem")
      val actorToCreateSchema = system.actorOf(Props[SchemaCreator],"schemaCreator")
      val actorToCrawlWeb = system.actorOf(Props[Crawler],"crawler")
      val actorToInsertHorseData = system.actorOf(Props[HorseData],"horseData")
      val actorToInsertRaceData = system.actorOf(Props[RaceData],"raceData")

      val urlParameters: List[Int] = Range(lowerLimit.toInt, upperLimit.toInt).toList

      actorToCrawlWeb ! urlParameters

      val revisedLowerLimit = lowerLimit.toInt + increment.toInt
      val revisedUpperLimit = upperLimit.toInt + increment.toInt

      lowerLimit = String.valueOf(revisedLowerLimit)
      upperLimit = String.valueOf(revisedUpperLimit)

    }

  }


  class Crawler extends Actor with ActorLogging {

    override def receive:Receive = {
      case parameters:List[Int] => {

        parameters.foreach(lookupKey => {

          //log.info(s"${lookupKey} received by crawler")

          val id = "%07d".format(lookupKey)

          val url = "https://www.sportinglife.com/racing/profiles/horse/" + id

          createHorseFromUrl(url, id.toInt) match {
            case Left(ex) => log.info(ex.getMessage)
            case Right(horseData) => actorToInsertHorseData ! horseData
          }

          createRaceData(url, id.toInt) match {
            case Left(ex) => log.info(ex.getMessage)
            case Right(raceData) => actorToInsertRaceData ! raceData
          }

        })

      }
   }

  }

  class SchemaCreator extends Actor with ActorLogging {

    override def receive:Receive = {
      case true => {
        val createtables = tables.map(_.schema.create)
        db.run(DBIO.sequence(createtables))
        log.info(s"${createtables.length} have been created")
      }

      case false => {
        log.info("No tables have been created")
      }

    }

  }

  class HorseData extends Actor with ActorLogging {

    override def receive:Receive = {
      case(horse:Horse) => {
        insertHorseData(horse) match {
          case Left(ex) => log.info(ex.getMessage)
          case Right(success) => ??? //log.info(success)
        }
      }
    }

  }


  class RaceData extends Actor with ActorLogging {

    val results: ListBuffer[Either[java.lang.Throwable,String]] = ListBuffer()

    override def receive:Receive = {
      case(race:Race) =>{
        insertRaceData(race) match{
          case Left(ex) => log.info(ex.getMessage)
          case Right(success) => ??? //log.info(success)
        }
        }
       }


    }


  def insertRaceData(race:Race):Either[java.lang.Throwable,String] = {
    try {
      insertRaceDetails(race)
    }catch {
      case ex: Exception => Left(ex)
    }
    try {
      insertRaceTypeToLabel(None,race.courseType)
    }catch {
      case ex: Exception => Left(ex)
    }
    try {
      insertRaceDistanceToLabel(None,race.distance)
    }catch {
      case ex: Exception => Left(ex)
    }
    try {
      insertRaceGoingToLabel(None, race.going)
    }catch {
      case ex: Exception => Left(ex)
    }

    try {
      insertRaceClassToLabel(None, race.raceClass)
    }catch {
      case ex: Exception => Left(ex)
    }

    try {
      insertRaceStartingPriceToLabel(None, race.startingPrice)
    }catch {
      case ex: Exception => Left(ex)
    }

    Right(s"All race data was inserted for ${race.toString}")

  }


  def insertHorseData(horse:Horse): Either[java.lang.Throwable,String] = {

    try {
      insertHorseDetails(horse)
    } catch {
      case ex: Exception => Left(ex)
    }

    try {
      insertHorsesOwner(horse.owner)
    }catch{
      case ex: Exception => Left(ex)
    }

    try {
      insertHorsesTrainer(horse.trainer)
    }catch{
      case ex: Exception => Left(ex)
    }

    try {
      insertHorsesSire(horse.sireName)
    }catch{
      case ex: Exception => Left(ex)
    }

    try {
      insertHorsesDam(horse.damName)
    }catch{
      case ex: Exception => Left(ex)
    }

    Right(s"All horse data was inserted for ${horse.id}")

  }

  case class Race(horseId:Int, horseAge:Long, raceDate:String, position:String, numberRan:String, bha:String, courseType:String,
                  course:String, distance:String, going:String, raceClass:String, startingPrice:String, posMap:String)

  case class Horse(id:Int, name:String, dob:String, trainer:String, sex:String, sireName:String, damName:String, owner:String)
  
}

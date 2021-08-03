
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import slick.jdbc.meta.MTable

import UrlMethods._
import MysqlMethods._

object App extends App {

  //User must set these vals
  var lowerLimit = "11000"
  var upperLimit = "11100"
  val threshold = "12000"


  def main(): Unit ={

    val existing = db.run(MTable.getTables)

    Await.result(existing, Duration.Inf)

    val existingTable = existing.flatMap( tableNames => {
      val names = tableNames.map(mt => mt.name.name)
      val createIfNotExist = tables.filter( table =>
        (names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })

    Await.result(existingTable, Duration.Inf)

    try {

    while(upperLimit.toInt <= 12000) {

      println("upperLimit is " + upperLimit)
      println("lowerLimit is " + lowerLimit)

      val getHorseData: Seq[(Int, String, String, String, String, String, String, String)] = Range(lowerLimit.toInt, upperLimit.toInt).map(n => {
        val id = "%07d".format(n)
        val url = "https://www.sportinglife.com/racing/profiles/horse/" + id
        createHorseFromUrl(url, id.toInt)
      })


      val getRaceData: Seq[Seq[(Int, Long, String, String, String, String, String, String, String, String, String, String, String)]] = Range(lowerLimit.toInt, upperLimit.toInt).map(n => {
        val id = "%07d".format(n)
        val url = "https://www.sportinglife.com/racing/profiles/horse/" + id
        createRaceData(url, id.toInt)
      })

      getHorseData.foreach(
        h => {
          try {
            insertHorseDetails(h)
          }catch{
            case e:Exception => e.printStackTrace()
          }

          try {
            insertHorsesOwner(h._8)
          }catch{
            case e:Exception => e.printStackTrace()
          }

          try {
            insertHorsesTrainer(h._4)
          }catch{
            case e:Exception => e.printStackTrace()
          }

          try {
            insertHorsesSire(h._6)
          }catch{
            case e:Exception => e.printStackTrace()
          }

          try {
            insertHorsesDam(h._7)
          }catch{
            case e:Exception => e.printStackTrace()
          }

        }
      )

        getRaceData.foreach(
          f => {
            f.foreach(g => {
              try {
                insertRaceDetails(g)
              }catch{
                case e:Exception => e.printStackTrace()
              }

              try {
                insertRaceTypeToLabel((None, g._7))
              }catch{
                case e:Exception => e.printStackTrace()
              }

              try {
                insertRaceCourseToLabel((None, g._8))
              }catch{
                case e:Exception => e.printStackTrace()
              }

              try {
                insertRaceDistanceToLabel((None, g._9))
              }catch{
                case e:Exception => e.printStackTrace()
              }

              try {
                insertRaceGoingToLabel((None, g._10))
              }catch{
                case e:Exception => e.printStackTrace()
              }

              try {
                insertRaceClassToLabel((None, g._11))
              }catch{
                case e:Exception => e.printStackTrace()
              }


              try {
                insertRaceStartingPriceToLabel((None, g._12))
              }catch{
                case e:Exception => e.printStackTrace()
              }
            })
          }
        )

      val ll = lowerLimit.toInt + 100
      val ul = upperLimit.toInt + 100

      lowerLimit = String.valueOf(ll)
      upperLimit = String.valueOf(ul)

    }

    } finally db.close

  }


  main()

}

import Tables.{Horses, HorsesDamToId, HorsesOwnerToId, HorsesSireToId, HorsesTrainerToId, RaceClassToLabel, RaceCourseToLabel, RaceDetails, RaceDistanceToLabel, RaceGoingToLabel, RaceStartingPriceToLabel, RaceTypeToLabel}
import slick.jdbc.MySQLProfile
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

object MysqlMethods {

  val db: MySQLProfile.backend.Database = Database.forConfig("slick-mysql.db.properties")

  val horses = TableQuery[Horses]
  val horsesOwner = TableQuery[HorsesOwnerToId]
  val horsesTrainer = TableQuery[HorsesTrainerToId]
  val horsesDam = TableQuery[HorsesDamToId]
  val horsesSire = TableQuery[HorsesSireToId]
  val races = TableQuery[RaceDetails]
  val raceTypeToLabel = TableQuery[RaceTypeToLabel]
  val raceCourseToLabel = TableQuery[RaceCourseToLabel]
  val raceDistanceToLabel = TableQuery[RaceDistanceToLabel]
  val raceGoingToLabel = TableQuery[RaceGoingToLabel]
  val raceClassToLabel = TableQuery[RaceClassToLabel]
  val raceStartingPriceToLabel = TableQuery[RaceStartingPriceToLabel]

  val tables = List(horses, horsesOwner, horsesTrainer, horsesDam, horsesSire, races, raceTypeToLabel,
    raceCourseToLabel, raceDistanceToLabel, raceGoingToLabel, raceClassToLabel, raceStartingPriceToLabel)


  def insertHorseDetails(
                          horse:(Int, String, String, String, String, String, String, String)
                        ) = db.run(
    horses += (horse._1, horse._2, horse._3, horse._4, horse._5, horse._6, horse._7,horse._8)
  )


  def insertHorsesOwner(
                         horse:(String)
                       ) = db.run(horsesOwner += (None, horse))

  def insertHorsesTrainer(
                           horse:(String)
                         ) = db.run(horsesTrainer += (None, horse))

  def insertHorsesSire(
                        horse:(String)
                      ) = db.run(horsesSire += (None, horse))

  def insertHorsesDam(
                       horse:(String)
                     ) = db.run(horsesDam += (None, horse))

  def insertRaceDetails(
                         race:(Int,Long,String,String,String,String,String,String,String,String,String,String, String)
                       ) = db.run(
    races += (None,race._1, race._2, race._3, race._4, race._5, race._6, race._7, race._8, race._9, race._10, race._11, race._12,race._13)
  )


  def insertRaceTypeToLabel(
                             race:(Option[Int], String)
                           ) = db.run(
    raceTypeToLabel += (None,race._2)
  )

  def insertRaceCourseToLabel(
                               race:(Option[Int], String)
                             ) = db.run(
    raceCourseToLabel += (None,race._2)
  )

  def insertRaceDistanceToLabel(
                                 race:(Option[Int], String)
                               ) = db.run(
    raceDistanceToLabel += (None,race._2)
  )

  def insertRaceGoingToLabel(
                              race:(Option[Int], String)
                            ) = db.run(
    raceGoingToLabel += (None,race._2)
  )

  def insertRaceClassToLabel(
                              race:(Option[Int], String)
                            ) = db.run(
    raceClassToLabel += (None,race._2)
  )

  def insertRaceStartingPriceToLabel(
                                      race:(Option[Int], String)
                                    ) = db.run(
    raceStartingPriceToLabel += (None,race._2)
  )

}

import Tables.{Horses, HorsesDamToId, HorsesOwnerToId, HorsesSireToId, HorsesTrainerToId, RaceClassToLabel, RaceCourseToLabel, RaceDetails, RaceDistanceToLabel, RaceGoingToLabel, RaceStartingPriceToLabel, RaceTypeToLabel}
import slick.jdbc.MySQLProfile
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import App.{Horse, Race}

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
                          horse:Horse
                        ) = db.run(
    horses += (horse.id, horse.name, horse.dob, horse.trainer, horse.sex, horse.sireName, horse.damName, horse.trainer)
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
                         race:Race
                       ) = db.run(
    races += (None,race.horseId, race.horseAge, race.raceDate, race.position, race.numberRan, race.bha, race.courseType,
      race.course, race.distance, race.going, race.raceClass, race.startingPrice,race.posMap)
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

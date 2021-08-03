import MysqlMethods.horses
import slick.jdbc.MySQLProfile.api._

object Tables {

  class Horses(tag: Tag) extends Table[(Int, String, String, String, String, String, String, String)](tag, "horses_details") {
    def id = column[Int]("horse_id", O.PrimaryKey)

    def name = column[String]("horse_name")

    def dob = column[String]("date_of_birth")

    def trainer = column[String]("trainer", O.SqlType("VARCHAR(255)"))

    def sex = column[String]("sex")

    def sire = column[String]("sire_name", O.SqlType("VARCHAR(255)"))

    def dam = column[String]("dam_name", O.SqlType("VARCHAR(255)"))

    def owner = column[String]("owner", O.SqlType("VARCHAR(255)"))

    def * = (id, name, dob, trainer, sex, sire, dam, owner)

  }

  class HorsesOwnerToId(tag: Tag) extends Table[(Option[Int], String)](tag, "horses_owner_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def owner = column[String]("owner", O.SqlType("VARCHAR(255)"))

    def * = (label, owner)

    def uniqueOwner = index("unique_owner_id", (owner), unique = true)

  }


  class HorsesTrainerToId(tag: Tag) extends Table[(Option[Int], String)](tag, "horses_trainer_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def trainer = column[String]("trainer", O.SqlType("VARCHAR(255)"))

    def uniqueTrainer = index("unique_trainer_id", (trainer), unique = true)

    def * = (label, trainer)

  }

  class HorsesSireToId(tag: Tag) extends Table[(Option[Int], String)](tag, "horses_sire_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def sire = column[String]("sire", O.SqlType("VARCHAR(255)"))

    def uniqueSire = index("unique_sire_id", (sire), unique = true)

    def * = (label, sire)

  }

  class HorsesDamToId(tag: Tag) extends Table[(Option[Int], String)] (tag, "horses_dam_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def dam = column[String]("dam", O.SqlType("VARCHAR(255)"))

    def uniqueDam = index("unique_dam_id", (dam), unique = true)

    def * = (label, dam)

  }

  class RaceDetails(tag: Tag) extends Table[(Option[Int], Int,Long,String,String,String,String,String,String,String,String,String,String, String)](tag, "races") {

    def raceId = column[Option[Int]]("race_id", O.PrimaryKey, O.AutoInc)

    def horseId = column[Int]("horse_id")

    def horseAge = column[Long]("age_of_horse_in_months")

    def raceDate = column[String]("date_of_race")

    def position = column[String]("position")

    def numberRan = column[String]("number_of_horses_in_race")

    def bha = column[String]("BHA")

    def courseType = column[String]("type")

    def course = column[String]("course")

    def distance = column[String]("distance")

    def going = column[String]("going")

    def raceClass = column[String]("class")

    def startingPrice = column[String]("price")

    def posMap = column[String]("pos_map")

    def horseIdForeignKey = foreignKey("HORSES_FK", horseId, horses)(_.id)

    def * = (raceId, horseId, horseAge, raceDate, position, numberRan, bha, courseType, course, distance, going, raceClass, startingPrice, posMap)

  }

  class RaceTypeToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_type_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def courseType = column[String]("type", O.SqlType("VARCHAR(255)"))

    def uniqueCourseType = index("unique_course_type", courseType, unique = true)

    def * = (label, courseType)

  }

  class RaceCourseToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_course_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def course = column[String]("course", O.SqlType("VARCHAR(255)"))

    def uniqueCourseType = index("unique_course", course, unique = true)

    def * = (label, course)

  }


  class RaceDistanceToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_distance_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def distance = column[String]("distance", O.SqlType("VARCHAR(255)"))

    def uniqueDistanceType = index("unique_distance", distance, unique = true)

    def * = (label, distance)

  }

  class RaceGoingToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_going_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def going = column[String]("going", O.SqlType("VARCHAR(255)"))

    def uniqueGoing = index("unique_going", going, unique = true)

    def * = (label, going)

  }

  class RaceClassToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_class_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def raceClass = column[String]("class", O.SqlType("VARCHAR(255)"))

    def uniqueGoing = index("unique_class", raceClass, unique = true)

    def * = (label, raceClass)

  }


  class RaceStartingPriceToLabel(tag: Tag) extends Table[(Option[Int], String)](tag, "race_price_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def raceStartingPrice = column[String]("price", O.SqlType("VARCHAR(255)"))

    def uniqueGoing = index("unique_price", raceStartingPrice, unique = true)

    def * = (label, raceStartingPrice)

  }

}

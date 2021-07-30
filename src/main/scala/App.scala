import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

import App.{db, horsesOwner}
import org.jsoup.Jsoup
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.matching.Regex
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ListBuffer


object App extends App {

  val lowerLimit = "1"
  val upperLimit = "11"

  val getHorseData : Seq[(Int, String, String, String, String, String, String, String)] = Range(lowerLimit.toInt,upperLimit.toInt).map(n => {
    val id = "%07d".format(n)
    val url = "https://www.sportinglife.com/racing/profiles/horse/" + id
    createHorseFromUrl(url,id.toInt)
  }).toSeq


  val db: MySQLProfile.backend.Database = Database.forConfig("slick-mysql.db.properties")

  val horses = TableQuery[Horses]
  val horsesOwner = TableQuery[HorsesOwnerToId]
  val horsesTrainer = TableQuery[HorsesTrainerToId]
  val horsesDam = TableQuery[HorsesDamToId]
  val horsesSire = TableQuery[HorsesSireToId]

  try {

    Await.result(db.run(DBIO.seq(

      // create the schema
      horses.schema.create,
      horsesOwner.schema.create,
      horsesTrainer.schema.create,
      horsesDam.schema.create,
      horsesSire.schema.create,

      // insert two User instances
      horses ++= getHorseData,
      horsesOwner ++= getHorseData.map(f => {
        (None,f._8, f._1)
      }).toSeq,

      horsesTrainer++= getHorseData.map(f => {
        (None,f._4, f._1)
      }).toSeq,

      horsesDam++= getHorseData.map(f => {
        (None,f._6, f._1)
      }).toSeq,

      horsesSire++= getHorseData.map(f => {
        (None,f._7, f._1)
      }).toSeq,

      // print the users (select * from USERS)
      horses.result.map(println)
    )), Duration.Inf)
  } finally db.close




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

 class HorsesOwnerToId(tag: Tag) extends Table[(Option[Int], String, Int)](tag, "horses_owner_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def owner = column[String]("owner", O.SqlType("VARCHAR(255)"))

    def id = column[Int]("horse_id")

    def * = (label, owner, id)

    def uniqueOwner = index("unique_owner_id", (owner , id), unique = true)

    def horseId = foreignKey("OWNER_FK", id, horses)(_.id)

  }


  class HorsesTrainerToId(tag: Tag) extends Table[(Option[Int], String, Int)](tag, "horses_trainer_to_label") {

    def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

    def trainer = column[String]("trainer", O.SqlType("VARCHAR(255)"))

    def id = column[Int]("horse_id")

    def uniqueTrainer = index("unique_trainer_id", (trainer , id), unique = true)

    def horseId = foreignKey("TRAINER_FK", id, horses)(_.id)

    def * = (label, trainer, id)

   }

    class HorsesSireToId(tag: Tag) extends Table[(Option[Int], String, Int)](tag, "horses_sire_to_label") {

      def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

      def sire = column[String]("sire", O.SqlType("VARCHAR(255)"))

      def id = column[Int]("horse_id")

      def uniqueSire = index("unique_sire_id", (sire , id), unique = true)

      def horseId = foreignKey("SIRE_FK", id, horses)(_.id)

     def * = (label, sire, id)

   }

   class HorsesDamToId(tag: Tag) extends Table[(Option[Int], String, Int)] (tag, "horses_dam_to_label") {

     def label = column[Option[Int]]("label", O.PrimaryKey, O.AutoInc)

     def dam = column[String]("dam", O.SqlType("VARCHAR(255)"))

     def id = column[Int]("horse_id")

     def uniqueDam = index("unique_dam_id", (dam , id), unique = true)

     def horseId = foreignKey("DAM_FK", id, horses)(_.id)

     def * = (label, dam, id)

   }

  class Races(tag: Tag) extends Table[(Int, String, String, String, String, String, String, String)](tag, "horses_details") {
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


  def createHorseFromUrl(url:String, id:Int) : (Int, String, String, String, String, String, String, String) = {
    val doc = Jsoup.connect(url).get().toString

    val nameRegex: Regex = """<h1 data-test-id="header-title" class=\"Header__Title-xeaizz-0 ndttp\">(.+?)</h1>""".r
    val horseDataRegex: Regex = """<td class="Header__DataValue-xeaizz-4 cUoUJA">(.+?)</td>""".r

    val name=  nameRegex.findFirstMatchIn(doc).get.group(1).replace(",","")

    val horseData  = horseDataRegex.findAllMatchIn(doc).toList

    val dob = getDob(clean(horseData(0).toString()))

  //  val age  = convertDobToMonths(dob.trim) toDo will need this for racing

    val trainer = getTrainer(clean(horseData(1).toString())).replace(",","")

    val sex = mapSex(clean(horseData(2).toString()))

    var sire = ""

    var dam = ""

    var owner = ""

    if(horseData.length == 6) {

      sire = clean(horseData(3).toString()).replace(",","")

      dam = clean(horseData(4).toString()).replace(",","")

      owner = clean(horseData(5).toString()).replace(",","").replace("&amp;","&")


    }else if(horseData.length == 5){

      val sire = clean(horseData(3).toString())

      val dam = clean(horseData(4).toString())

    }

    (id,name, dob, trainer, sex, sire, dam, owner)

  }


  def getRaceDetails(horseId:Int, horseDob: String, doc:String): List[(Int,Long,String,String,String,String,String,String,String,String,String, String)] ={

    val horseAgeInMonths = convertDobToMonths(horseDob)

    val thisdoc = doc.replaceAll("\n","")

    val rowRegex: Regex = """<tr class="FormTable__StyledTr-sc-1xr7jxa-3 foFtQM">(.+?)</tr>""".r
    val fieldRegex: Regex = """<td class="FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy">(.+?)</td>""".r
    val dateRegex: Regex = """<td class="FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy"><a href=".*">(.+?)</a></td>""".r
    val otherRegex: Regex = """ <td class="FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy">(.*?)</td>""".r
    val rows = rowRegex.findAllMatchIn(thisdoc).toList

    val list: ListBuffer[(Int,Long,String,String,String,String,String,String,String,String,String, String)] = ListBuffer()

    for (elem <- rows) {
      val sequence = Array("", "", "", "", "", "", "", "", "", "")

      val otherData = otherRegex.findAllMatchIn(elem.toString()).toList

      for (elems <- otherData) {
        if (sequence(0).trim().isEmpty) {
          sequence(0) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(1).trim().isEmpty) {
          val x = cleanRaceData(elems.toString())
          if (x.contains("/")) {
            sequence(1) = x.split("/")(0).replace(",", "")
            sequence(2) = x.split("/")(1).replace(",", "")
          } else {
            sequence(1) = "-"
            sequence(2) = "-"
          }
        } else if (sequence(3).trim().isEmpty) {
          sequence(3) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(4).trim().isEmpty) {
          sequence(4) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(5).trim().isEmpty) {
          sequence(5) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(6).trim().isEmpty) {
          sequence(6) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(7).trim().isEmpty) {
          sequence(7) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(8).trim().isEmpty) {
          sequence(8) = cleanRaceData(elems.toString().replace(",", ""))
        } else if (sequence(9).trim().isEmpty) {
          sequence(9) = cleanRaceData(elems.toString().replace(",", ""))
        }

      }

      list.append((horseId,horseAgeInMonths,sequence(0),sequence(1),sequence(2),sequence(3),sequence(4),sequence(5),sequence(6),sequence(7),sequence(8), sequence(9)))

    }

    list.toList

    }

  def mapSex(sex:String): String ={
    if(sex.trim.toLowerCase.equals("filly")){
      "1"
    }else{
      "0"
    }

  }


  def mapPos(pos:String): Double ={
    if(pos.trim.equals("1") || pos.trim.equals("2") || pos.trim.equals("3")){
      1
    }else{
      0
    }

  }

  def cleanRaceData(str:String) :String = {
    val output = str.replaceAll("<td class=\"FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy\">","")
      .replaceAll("</td>","")
      .replaceAll("<td class=\"FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy\">","")
      .replaceAll("<a href=\".*\">","")
      .replaceAll("</a>","")

    if(output.trim.isEmpty){
      "-"
    }else{
      output
    }

  }

  def clean(str: String) : String = {
    str.replaceAll("<td class=\"Header__DataValue-xeaizz-4 cUoUJA\">","")
      .replaceAll("</td>", "")
      .trim()
  }

  def getDob(age: String) : String = {
    val dobRegex: Regex = """\d{1,2}th\s[a-zA-Z]+\s\d{4}|\d{1,2}st\s[a-zA-Z]+\s\d{4}|\d{1,2}rd\s[a-zA-Z]+\s\d{4}|\d{1,2}nd\s[a-zA-Z]+\s\d{4}""".r
    dobRegex.findFirstMatchIn(age).get.toString()
  }

  def getTrainer(trainer: String): String = {
    trainer.replaceAll("<a href=\"/racing/profiles/trainer/\\d+\">", "")
      .replaceAll("</a>","")
      .trim()
  }

  def convertDobToMonths(birthDate:String): Long={

    val dayStr = birthDate.trim().split(" ")(0)
    val year = birthDate.trim().split(" ")(2)
    val month = birthDate.trim().split(" ")(1).toUpperCase
    val day = """\d+""".r findFirstIn(dayStr)
    val today = LocalDate.now


    val format = DateTimeFormat.forPattern("MMM")
    val instance = format.withLocale(Locale.ENGLISH).parseDateTime(month)

    val yearFormat = DateTimeFormat.forPattern("YYYY")
    val yearInstance = yearFormat.withLocale(Locale.ENGLISH).parseDateTime(year)

    val month_number = instance.getMonthOfYear
    val month_text = instance.monthOfYear.getAsText(Locale.ENGLISH)

    val  year_number = yearInstance.getYear

    val birthday = LocalDate.of(year_number,month_number, day.get.toInt)

    ChronoUnit.MONTHS.between(birthday, today)


  }

}

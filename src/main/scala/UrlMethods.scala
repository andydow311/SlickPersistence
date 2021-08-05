import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

import App.{Horse, Race}
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object UrlMethods {

  def createHorseFromUrl(url:String, id:Int) : Either[java.lang.Throwable, Horse] = {

    try {
      val doc = Jsoup.connect(url).get().toString

      val nameRegex: Regex = """<h1 data-test-id="header-title" class=\"Header__Title-xeaizz-0 ndttp\">(.+?)</h1>""".r
      val horseDataRegex: Regex = """<td class="Header__DataValue-xeaizz-4 cUoUJA">(.+?)</td>""".r

      val name = nameRegex.findFirstMatchIn(doc).get.group(1).replace(",", "")

      val horseData = horseDataRegex.findAllMatchIn(doc).toList

      val dob = getDob(clean(horseData(0).toString()))

      val trainer = getTrainer(clean(horseData(1).toString())).replace(",", "")

      val sex = mapSex(clean(horseData(2).toString()))

      var sire = ""

      var dam = ""

      var owner = ""

      if (horseData.length == 6) {

        sire = clean(horseData(3).toString()).replace(",", "")

        dam = clean(horseData(4).toString()).replace(",", "")

        owner = clean(horseData(5).toString()).replace(",", "").replace("&amp;", "&")


      } else if (horseData.length == 5) {

        val sire = clean(horseData(3).toString())

        val dam = clean(horseData(4).toString())

      }

      Right(Horse(id, name, dob, trainer, sex, sire, dam, owner))


    } catch {
      case ex: Exception => Left(ex)
    }
  }


  def createRaceData(url: String,id:Int): Either[java.lang.Throwable, Seq[Race]] ={

    val seq: ListBuffer[Race] = ListBuffer()

    try{

      val doc = Jsoup.connect(url).get().toString

      val horseDataRegex: Regex = """<td class="Header__DataValue-xeaizz-4 cUoUJA">(.+?)</td>""".r

      val horseData  = horseDataRegex.findAllMatchIn(doc).toList

      val horseDob = getDob(clean(horseData(0).toString()))

      var horseAgeInMonths = 0L

      val thisdoc = doc.replaceAll("\n","")

      val rowRegex: Regex = """<tr class="FormTable__StyledTr-sc-1xr7jxa-3 foFtQM">(.+?)</tr>""".r

      val otherRegex: Regex = """ <td class="FormTable__StyledTd-sc-1xr7jxa-4 iSKYZy">(.*?)</td>""".r

      val rows = rowRegex.findAllMatchIn(thisdoc).toList

      for (elem <- rows) {
        val sequence = Array("", "", "", "", "", "", "", "", "", "","")

        val otherData = otherRegex.findAllMatchIn(elem.toString()).toList

        for (elems <- otherData) {
          if (sequence(0).trim().isEmpty) {
            sequence(0) = cleanRaceData(elems.toString().replace(",", ""))
            horseAgeInMonths = convertDobToMonths(horseDob,sequence(0))
          } else if (sequence(1).trim().isEmpty) {
            val x = cleanRaceData(elems.toString())
            if (x.contains("/")) {
              sequence(1) = x.split("/")(0).replace(",", "")
              sequence(2) = x.split("/")(1).replace(",", "")
              sequence(10) = mapPos(x.split("/")(0).replace(",","")).toString
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

       seq.append(
          Race(id,
            horseAgeInMonths,
            sequence(0),
            sequence(1),
            sequence(2),
            sequence(3),
            sequence(4),
            sequence(5),
            sequence(6),
            sequence(7),
            sequence(8),
            sequence(9),
            sequence(10)
          )
        )
      }

    }catch {
      case e: Exception => Left(e)
    }
    Right(seq.toList)
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
      output.trim
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

  def convertDobToMonths(birthDate:String, raceDate:String): Long={

    //Birthday deets
    val dayStr = birthDate.trim().split(" ")(0)

    val year = birthDate.trim().split(" ")(2)

    val month = birthDate.trim().split(" ")(1).toUpperCase

    val day = """\d+""".r findFirstIn(dayStr)

    val format = DateTimeFormat.forPattern("MMM")

    val instance = format.withLocale(Locale.ENGLISH).parseDateTime(month)

    val yearFormat = DateTimeFormat.forPattern("YYYY")

    val yearInstance = yearFormat.withLocale(Locale.ENGLISH).parseDateTime(year)

    val month_number = instance.getMonthOfYear

    val month_text = instance.monthOfYear.getAsText(Locale.ENGLISH)

    val year_number = yearInstance.getYear

    val birthday = LocalDate.of(year_number,month_number, day.get.toInt)

    //race day deets
    val raceDayStr = raceDate.trim().split("/")(0)

    val raceYear = raceDate.trim().split("/")(2)

    val raceMonth = raceDate.trim().split("/")(1)

    val raceDay = """\d+""".r findFirstIn(raceDayStr)

    val raceYearFormat = DateTimeFormat.forPattern("YY")

    val raceYearInstance = raceYearFormat.withLocale(Locale.ENGLISH).parseDateTime(raceYear)

    val raceMonthnumber = raceYearInstance.getMonthOfYear

    val raceday = LocalDate.of(raceYearInstance.getYear.toInt,raceMonth.toInt, raceDayStr.toInt)

    //output month details
    ChronoUnit.MONTHS.between(birthday, raceday)
  }

}

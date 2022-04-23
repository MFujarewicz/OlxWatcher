import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import java.util.Calendar
import java.text.SimpleDateFormat
import java.io._
import scala.io.Source
import scala.util.Using


object Main extends App {
  val aLotOfCarriageReturns = "\r"*100

  val LINKS_FILENAME = "offers.txt"
  //nuber with country code: "+XXXXXXXXXXX"
  val myNumber = ""
  //olx offer url to be watched, just copy the url from olx after inputting all the filtering parameters
  val OFFER_URL = "https://www.olx.pl/d/nieruchomosci/mieszkania/wynajem/biale-blota_41973/?search%5Bfilter_float_m:from%5D=40"
  var newOfferCounter = 0
  val CHECK_FREQUENCY_SECONDS = 60
  val startTime = timestamp()
  var newLinkList = List[String]()

  while (true) {
    val newOffers = newLinks()
    writeLinks(newOffers)
    newOfferCounter += newOffers.size
    newLinkList = newLinkList ++ newOffers.toList

    newOffers.foreach(s => {
      SmsTwilio.sendSms(s"new offer at olx: $s", myNumber)
    })

    print("\u001b[2J\u001b[H")
    println(AsciiLogo.getLogo())
    println(s"last offer check at: ${timestamp()}")
    println(s"new offers since $startTime: $newOfferCounter")
    newLinkList.foreach(println)


    Thread.sleep(CHECK_FREQUENCY_SECONDS*1000)
  }

  def newLinks(): Set[String] = {
    getLinks() -- readLinks()
  }

  def readLinks(): Set[String] = {
    Using(Source.fromFile(LINKS_FILENAME)) {
      source => source.getLines().toList
    }.get.toSet.filter(_.nonEmpty)
  }

  def writeLinks(links: Set[String]): Unit = {
    val linksToWrite = links -- readLinks()

    val bw = new FileWriter(LINKS_FILENAME, true)
    linksToWrite.foreach({ link =>
      bw.write(link)
      bw.write("\n")
    })
    bw.close()
  }

  def getLinks(): Set[String] = {

    val browser = JsoupBrowser()
    val soup = browser.get(OFFER_URL)

    val links = soup >> elementList("#offers_table a") >> attr("href")
    links.filter(_.startsWith("https://www.olx.pl/d/oferta/")).map(s => s.takeWhile(_ != '#')).toSet
  }

  def timestamp(): String = {
    val form = new SimpleDateFormat("dd/MM/yy HH:mm:ss")
    form.format(Calendar.getInstance().getTime)
  }

}



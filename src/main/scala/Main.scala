import sttp.client4._
import java.util.Calendar
import java.text.SimpleDateFormat
import java.io._
import java.io.File
import scala.io.Source
import scala.util.Using
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {

//  val config = ConfigSource.default.load[ServiceConf].toOption.get
  val config = ConfigSource.file("conf.txt").load[ServiceConf].toOption.get


  val smsTwilio = SmsTwilio(config.twilioAccountSid, config.twilioAuthToken, config.twilioNumber)





  val aLotOfCarriageReturns = "\r" * 100
  val backend = DefaultSyncBackend();

  val LINKS_FILENAME = "offers.txt"
  //nuber with country code: "+XXXXXXXXXXX"
  val myNumber = config.phoneNumber
  //olx offer url to be watched, just copy the url from olx after inputting all the filtering parameters
  val OFFER_URL = config.url
  var newOfferCounter = 0
  val CHECK_FREQUENCY_SECONDS = config.refreshSeconds
  val startTime = timestamp()
  var newLinkList = List[String]()

  val file = new File(LINKS_FILENAME)
  if (!file.exists()) {
    file.createNewFile()
  }

  while (true) {
    val newOffers = newLinks()
    writeLinks(newOffers)
    newOfferCounter += newOffers.size
    newLinkList = newLinkList ++ newOffers.toList

    if (config.sendSms){
      newOffers.foreach(s => {
        smsTwilio.sendSms(s"new offer at olx: $s", myNumber)
      })
    }

    print("\u001b[2J\u001b[H")
    println(AsciiLogo.getLogo())
    println(s"last offer check at: ${timestamp()}")
    println(s"new offers since $startTime: $newOfferCounter")
    newLinkList.foreach(println)


    Thread.sleep(CHECK_FREQUENCY_SECONDS * 1000)
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

    val text =
      basicRequest.get(uri"$OFFER_URL")
        .send(backend)
        .body
        .toOption
        .getOrElse("")

    """\/d\/oferta\/\S*.html""".r.findAllIn(text).toList
      .map(s => "www.olx.pl" + s).toSet
  }

  def timestamp(): String = {
    val form = new SimpleDateFormat("dd/MM/yy HH:mm:ss")
    form.format(Calendar.getInstance().getTime)
  }

}



case class ServiceConf(
                                                url: String,
                                                refreshSeconds: Int,
                                                sendSms: Boolean,
                                                phoneNumber: String,
                                                twilioAccountSid: String,
                                                twilioAuthToken: String,
                                                twilioNumber: String,
                      )
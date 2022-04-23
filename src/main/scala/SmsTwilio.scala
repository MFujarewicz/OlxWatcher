import com.twilio.`type`.PhoneNumber

object SmsTwilio {
  import com.twilio.Twilio
  import com.twilio.rest.api.v2010.account.Message

  //Twilio configuration, free tier is good for ~few hundred messages
  val ACCOUNT_SID = ""
  val AUTH_TOKEN = ""
  val MY_TWILIO_NUMBER = ""
  Twilio.init(ACCOUNT_SID, AUTH_TOKEN)

  def sendSms(message: String, recipient: String): Unit = {
    Message.creator(
      new PhoneNumber(recipient), //to
      new PhoneNumber(MY_TWILIO_NUMBER), //from
      message
    ).create()
  }
}

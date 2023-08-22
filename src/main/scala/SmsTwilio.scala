import com.twilio.`type`.PhoneNumber

class SmsTwilio(val accountSid: String, val authToken:String, val twilioNumber:String) {
  import com.twilio.Twilio
  import com.twilio.rest.api.v2010.account.Message

  //Twilio configuration, free tier is good for ~few hundred messages
  Twilio.init(accountSid, authToken)

  def sendSms(message: String, recipient: String): Unit = {
    Message.creator(
      new PhoneNumber(recipient), //to
      new PhoneNumber(twilioNumber), //from
      message
    ).create()
  }
}

object SmsTwilio {
  def apply(accountSid: String, authToken:String, twilioNumber:String): SmsTwilio = {
    new SmsTwilio(accountSid, authToken, twilioNumber  )
  }
}

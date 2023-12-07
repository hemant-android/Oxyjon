package app.oxyjon.smsauto


open interface SMSListener {
    fun onSuccess(message: String?)
    fun onError(message: String?)
}
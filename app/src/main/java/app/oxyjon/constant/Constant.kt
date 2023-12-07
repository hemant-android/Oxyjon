package app.oxyjon.constant

import app.oxyjon.BuildConfig

/**
 * Created by appsinvo on 24/11/18.
 */
object Constant {
    var BASE_URL: String = "https://api.oxyjon.com/" //Live Url
//    var BASE_URL: String = "https://devapi.oxyjon.com/" //Dev Url

    var BASE_API: String = BuildConfig.SERVER_URL + "api/auth/"
    var BASE_CCAVENUE: String = "http://166.62.10.199/"
    var CCAVENUE_RSA: String = BASE_CCAVENUE + "GetRSA.php"
    var CCAVENUE_RESPONSE_HANDLER: String = BASE_CCAVENUE + "ccavResponseHandler.php"

    //public static String CCAVENUE_RSA = ""+"https://appsinvo.com/payment_oxyjon/GetRSA.php";
    //    public static String CCAVENUE_RESPONSE_HANDLER = "https://appsinvo.com/payment_oxyjon/"+"ccavResponseHandler.php";
    var USER_TYPE: String = "2"
    var ERROR_OK: String = "0"
    val LOCATION_STATE: Int = 0x200
    val CALL_STATE: Int = 0x4400
    val PLACE_AUTOCOMPLETE_REQUEST_CODE: Int = 0x40
    val TERM_CONDITIONS_REQUEST_CODE: Int = 0x80
    val MIN_X_VISIBLE: Int = 4
    val MAX_X_VISIBLE: Int = 4
}
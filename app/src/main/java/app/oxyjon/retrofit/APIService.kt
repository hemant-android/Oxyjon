package app.oxyjon.retrofit

import app.oxyjon.bean.*
import app.oxyjon.retrofit.response.*
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by appsinvo on 13/11/18.
 */
open interface APIService {
    @POST("v3/send-otp")
    @FormUrlEncoded
    fun sendOtp(
        @Field("phone") mobileNumber: String?,
        @Field("deviceId") deviceId: String?,
        @Field("android_version") android_version: String?,
        @Field("app_installed_version") app_installed_version: String?,
        @Field("device_info") device_info: String?
    ): Call<SendOtpResponse?>?

    @POST("v3/truecaller-verification")
    @FormUrlEncoded
    fun loginWithTrueCaller(
        @Field("countryCode") countryCode: String?,
        @Field("firstName") firstName: String?,
        @Field("gender") gender: String?,
        @Field("isAmbassador") isAmbassador: Boolean,
        @Field("isBusiness") isBusiness: Boolean,
        @Field("isSimChanged") isSimChanged: Boolean,
        @Field("isTrueName") isTrueName: Boolean,
        @Field("lastName") lastName: String?,
        @Field("payload") payload: String?,
        @Field("phoneNumber") phoneNumber: String?,
        @Field("requestNonce") requestNonce: String?,
        @Field("signature") signature: String?,
        @Field("signatureAlgorithm") signatureAlgorithm: String?,
        @Field("userLocale") userLocale: String?,
        @Field("verificationTimestamp") verificationTimestamp: Long,
        @Field("deviceId") deviceId: String?,
        @Field("android_version") android_version: String?,
        @Field("app_installed_version") app_installed_version: String?,
        @Field("device_info") device_info: String?
    ): Call<SendOtpResponse?>?

    @POST("v3/verify-otp")
    @FormUrlEncoded
    fun verifyOtp(
        @Field("customer_id") customerId: String?,
        @Field("otp") otp: String?,
        @Field("type") type: String?
    ): Call<SendOtpResponse?>?

    @POST("v3/onboarding")
    fun getOnBoarding(): Call<OnBoardingResponse?>?

    @POST("v3/onboarding_profileupdate")
    @FormUrlEncoded
    fun updateQuestionFirst(
        @Field("company_code") companyCode: String?,
        @Field("name") name: String?,
        @Field("gender") gender: String?,
        @Field("age") age: String?
    ): Call<UpdateQuestionFirstResponse?>?

    @POST("v3/onboarding_activityupdate")
    @FormUrlEncoded
    fun updateQuestionSecond(
        @Field("height_ft") height_ft: String?,
        @Field("height_inches") height_inches: String?,
        @Field("weight") weight: String?,
        @Field("activity_score") activity_score: String?
    ): Call<UpdateQuestionFirstResponse?>?

    @POST("v3/getProfileStep3")
    fun getprofilestepthree(): Call<UpdateStepthreeResponce?>?

    @POST("v3/profileUpdateStep3")
    @FormUrlEncoded
    fun getprofileUpdateStepthree(
        @Field("allergyId") allergyId: String?
    ): Call<UserDataResponse?>?

    @POST("v3/profileUpdateStep7")
    fun getupdatestrepseven(@Body file: RequestBody?): Call<UploadDocumentResponse?>?

    @POST("v3/medicinelist")
    @FormUrlEncoded
    fun getMedicineList(
        @Field("page") page: String?
    ): Call<MedicineListResponse?>?

    @POST("v3/medicinesyncompleted")
    @FormUrlEncoded
    fun medicineSyncComplete(
        @Field("sync_id") sync_id: String?
    ): Call<MedicineListResponse?>?

    @POST("v3/getmymedicines")
    fun getMyMedicineList(): Call<MyMedicineListResponse?>?

    @POST("v3/deletemedicines")
    @FormUrlEncoded
    fun removeMyMedicineList(
        @Field("id") profileId: String?,
        @Field("time_slot") timeSlot: String?
    ): Call<MyMedicineListResponse?>?

    @POST("v3/getmyfooddiary")
    @FormUrlEncoded
    fun getMyFoodDiaryList(
        @Field("meal_date") mealDate: String?
    ): Call<GetMyFoodDiaryResponse?>?

    @POST("v3/mostsearchedfooditems")
    fun getMostSearchFoodDiaryList(): Call<GetMostSearchFoodItemResponse?>?

    @POST("v3/deletefromfooddiary")
    @FormUrlEncoded
    fun removeMyFoodDiary(
        @Field("id") profileId: String?
    ): Call<CommonResponse?>?

    @POST("v3/addnewmadicine")
    @FormUrlEncoded
    fun addNewMedicine(
        @Field("medicine_id") medicineId: String?,
        @Field("medicine_name") medicineName: String?,
        @Field("time_slot") timeSlot: String?,
        @Field("dose") dose: String?,
        @Field("sdate") sDate: String?
    ): Call<SaveMedicineResponse?>?

    @POST("v3/healthSummary")
    fun gethealthsummary(): Call<SummaryResponce?>?

    @POST("v3/prelogin")
    @FormUrlEncoded
    fun preLogin(
        @Field("fcmtoken") fcmtoken: String?,
        @Field("mobile_no") mobileNo: String?,
    ): Call<PreLoginResponse?>?

    @POST("v3/actioneventlog")
    @FormUrlEncoded
    fun trackScreen(
        @Field("event_name") eventName: String?,
        @Field("action_id") actionId: String?,
        @Field("action_heading") actionHeading: String?
    ): Call<CommonResponse?>?

    @POST("v3/updatestepcount")
    @FormUrlEncoded
    fun trackSteps(
        @Field("date") date: String?,
        @Field("steps") steps: String?
    ): Call<StepCountResponse?>?

    @POST("v3/updatecalories")
    @FormUrlEncoded
    fun trackCal(
        @Field("date") date: String?,
        @Field("calories") cals: String?
    ): Call<StepCountResponse?>?

    @POST("v3/update_location")
    @FormUrlEncoded
    fun updateUserLocation(
        @Field("location_lat") location_lat: String?,
        @Field("location_long") location_long: String?,
        @Field("location_pincode") location_pincode: String?,
        @Field("location_city") location_city: String?,
        @Field("location_state") location_state: String?,
        @Field("location_address") location_address: String?
    ): Call<CommonResponse?>?

    @POST("v3/buyhealthplan")
    @FormUrlEncoded
    fun buyPlanDetail(
        @Field("heathplan_id") heathplan_id: String?,
        @Field("healthplan_name") healthplan_name: String?,
        @Field("health_plan_price") health_plan_price: String?
    ): Call<BuyPlanResponse?>?

    @POST("v3/buyhealthplanconfirm")
    @FormUrlEncoded
    fun paymentConfirm(
        @Field("heathplan_request_id") heathplan_request_id: String?,
        @Field("payment_status") payment_status: String?,
        @Field("payment_ref_data") payment_ref_data: String?
    ): Call<CommonResponse?>?

    @POST("v3/fooditemlist")
    @FormUrlEncoded
    fun getFoodList(
        @Field("page") page: String?
    ): Call<FoodDiaryListResponse?>?

    @POST("v3/foodlistmaster")
    @FormUrlEncoded
    fun getFoodListMaster(
        @Field("fcmtoken") fcmtoken: String?,
        @Field("page") page: String?
    ): Call<FoodDiaryListResponse?>?

    @POST("v3/fooditemsyncompleted")
    @FormUrlEncoded
    fun foodSyncComplete(
        @Field("sync_id") sync_id: String?
    ): Call<FoodDiaryListResponse?>?

    @POST("v3/updatefooddiaryitem")
    @FormUrlEncoded
    fun updateFoodItem(
        @Field("id") id: String?,
        @Field("food_id") food_id: String?,
        @Field("food_name") food_name: String?,
        @Field("meal_time") meal_time: String?,
        @Field("meal_date") meal_date: String?,
        @Field("meal_quantity_type") meal_quantity_type: String?,
        @Field("meal_quantity") meal_quantity: String?,
        @Field("meal_quantity_unit") meal_quantity_unit: String?,
        @Field("food_type") food_type: String?,
        @Field("calorie_gm") calorie_gm: String?,
        @Field("protein_gm") protein_gm: String?,
        @Field("carbs_gm") carbs_gm: String?,
        @Field("fats_gm") fats_gm: String?,
        @Field("fiber_gm") fiber_gm: String?
    ): Call<CommonResponse?>?

    @POST("v3/app_benifit")
    fun getAllBenefit(): Call<BenefitResponse?>?

    @POST("v3/doctorconsultation")
    fun getDoctorConsultationResponse(): Call<DoctorConsultationResponse?>?

    @POST("v3/getprofile")
    fun getProfile(): Call<GetProfileResponse?>?

    @POST("v3/updateprofile")
    @FormUrlEncoded
    fun updateProfile(
        @Field("name") name: String?,
        @Field("age") age: String?,
        @Field("gender") gender: String?,
        @Field("email") email: String?,
        @Field("height_feet") feet: String?,
        @Field("height_inches") inch: String?,
        @Field("weight") weight: String?,
        @Field("activity_score") activityKey: String?,
    ): Call<GetProfileResponse?>?

    @POST("v3/dashboard")
    fun getDashboardData(): Call<DashboardResponse?>?

    @POST("v3/profileUpdateStep5")
    @FormUrlEncoded
    fun saveSugarData(
        @Field("sugar") sugar: String?
    ): Call<UserDataResponse?>?

    @POST("v3/profileUpdateStep5")
    @FormUrlEncoded
    fun saveBPData(
        @Field("bloodPressure") bloodPressure: String?
    ): Call<UserDataResponse?>?

    @POST("v3/profileUpdateStep5")
    @FormUrlEncoded
    fun saveWeightData(
        @Field("weight") weight: String?
    ): Call<UserDataResponse?>?

    @POST("v3/getmymedicines")
    fun getMyMedicine(): Call<MyMedicineResponse?>?

    @POST("v3/addnewfooddiaryitem")
    @FormUrlEncoded
    fun addFoodItem(
        @Field("food_id") food_id: String?,
        @Field("food_name") food_name: String?,
        @Field("meal_time") meal_time: String?,
        @Field("meal_date") meal_date: String?,
        @Field("meal_quantity_type") meal_quantity_type: String?,
        @Field("meal_quantity") meal_quantity: String?,
        @Field("meal_quantity_unit") meal_quantity_unit: String?,
        @Field("food_type") food_type: String?,
        @Field("calorie_gm") calorie_gm: String?,
        @Field("protein_gm") protein_gm: String?,
        @Field("carbs_gm") carbs_gm: String?,
        @Field("fats_gm") fats_gm: String?,
        @Field("fiber_gm") fiber_gm: String?
    ): Call<CommonResponse?>?

    @POST("v3/mycareteam")
    fun getMyCareTeam(): Call<MyCareTeamResponse?>?

    @POST("v3/myhealthplan")
    fun getMyCarePlan(): Call<MyCareTeamResponse?>?

    @POST("v3/callbackrequest")
    @FormUrlEncoded
    fun callBackRequest(
        @Field("user_type") user_type: String?,
        @Field("selected_service") selected_service: String?,
        @Field("callback_note") callback_note: String?,
    ): Call<CommonResponse?>?

    @POST("v3/sendfeedback")
    @FormUrlEncoded
    fun callFeedbackRequest(
        @Field("service_ratings") service_ratings: String?,
        @Field("selected_service") selected_service: String?,
        @Field("service_dont_like") service_dont_like: String?,
        @Field("feedback_details") feedback_details: String?,
    ): Call<CommonResponse?>?

    @POST("v3/addcaregiver")
    @FormUrlEncoded
    fun addFamilyMember(
        @Field("caregiver_name") caregiver_name: String?,
        @Field("caregiver_relation") caregiver_relation: String?,
        @Field("caregiver_contact_no") caregiver_contact_no: String?,
        @Field("caregiver_note") caregiver_note: String?
    ): Call<CommonResponse?>?

    @get:GET("v3/healthplan")
    val healthPlan: Call<PlanListResponse?>?

    @POST("v3/healthplandetail")
    @FormUrlEncoded
    fun getHealthPlanDetail(
        @Field("id") planId: String?
    ): Call<HealthPlanDetailResponse?>?

    @POST("v3/blogbyid")
    @FormUrlEncoded
    fun getBlogDetail(
        @Field("blogid") blogId: String?
    ): Call<BlogDetailResponse?>?

    @POST("v3/healthscore")
    fun getAnalyticDetail(): Call<AnalyticResponse?>?

    @POST("v3/testplandetail")
    @FormUrlEncoded
    fun getTestBookDetail(
        @Field("id") planId: String?
    ): Call<TestBookDetailResponse?>?

    @POST("v3/myhealthdiary")
    fun getMyHealthDiary(): Call<MyHealthDiaryResponse?>?

    @POST("v3/setgoal")
    @FormUrlEncoded
    fun setStepGoal(
        @Field("goal_data_point") goalDataPoint: String?
    ): Call<CommonResponse?>?

    @POST("v3/getmysugardata")
    @FormUrlEncoded
    fun getSugarDetail(@Field("noofdays") noofdays: String?): Call<SugarDetailResponse?>?

    @POST("v3/getbpslist")
    fun getBPDetail(): Call<BPResponse?>?

    @POST("v3/dietplanhome")
    fun getDietChartDetailHome(): Call<DietPlanHomeResponse?>?

    @POST("v3/mydietplan")
    fun getMyDietChartDetail(): Call<MyDietPlanResponse?>?

    @POST("v3/measurements")
    fun getMeasurementsChartDetail(): Call<MyMeasurementResponse?>?

    @POST("v3/thingstodoavoid")
    fun getThingsToDoAvoidDetail(): Call<ThingsToDoAvoidResponse?>?

    @POST("v3/stressmanagement")
    fun getStressManagementDetail(): Call<StressManagementResponse?>?

    @POST("v3/exerciseplan")
    fun getPhysicalActivityDetail(): Call<PhysicalActivityResponse?>?

    @POST("v3/getweightlist")
    fun getWeightDetail(): Call<WeightResponse?>?

    @POST("v3/medicinediscount")
    fun getCheckDiscount(): Call<CommonResponse?>?

    @POST("v3/getmyreports")
    fun getMyDocumentList(): Call<DocumentReportResponse?>?

    @POST("v3/updatemultiplesteps")
    fun saveMyStepsList(@Body obj: JsonObject): Call<CommonResponse?>?

    @POST("v3/ispremium")
    fun getUserPaidOrNot(): Call<PremiumResponse?>?
}
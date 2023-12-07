package app.oxyjon.retrofit

import app.oxyjon.bean.*
import app.oxyjon.retrofit.response.*
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


/**
 * Created by appsinvo on 13/11/18.
 */
class ApiCall constructor() {
    fun userLoginWithTrueCaller(
        countryCode: String?,
        firstName: String?,
        gender: String?,
        isAmbassador: Boolean,
        isBusiness: Boolean,
        isSimChanged: Boolean,
        isTrueName: Boolean,
        lastName: String?,
        payload: String?,
        phoneNumber: String?,
        requestNonce: String?,
        signature: String?,
        signatureAlgorithm: String?,
        userLocale: String?,
        verificationTimestamp: Long,
        deviceId: String?,
        androidVersion: String?,
        appInstalledVersion: String?,
        deviceInfo: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<SendOtpResponse?>? = service!!.loginWithTrueCaller(
            countryCode,
            firstName,
            gender,
            isAmbassador,
            isBusiness,
            isSimChanged,
            isTrueName,
            lastName,
            payload,
            phoneNumber,
            requestNonce,
            signature,
            signatureAlgorithm,
            userLocale,
            verificationTimestamp,
            deviceId,
            androidVersion,
            appInstalledVersion,
            deviceInfo
        )
        call!!.enqueue(object : Callback<SendOtpResponse?> {
            public override fun onResponse(
                call: Call<SendOtpResponse?>?,
                response: Response<SendOtpResponse?>
            ) {
                iApiCallback.onSuccess("truecallerLogin", response, null)
            }

            public override fun onFailure(call: Call<SendOtpResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun userLoginWithMobile(
        userName: String?,
        deviceId: String?,
        androidVersion: String?,
        appInstalledVersion: String?,
        deviceInfo: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<SendOtpResponse?>? =
            service!!.sendOtp(userName, deviceId, androidVersion, appInstalledVersion, deviceInfo)
        call!!.enqueue(object : Callback<SendOtpResponse?> {
            public override fun onResponse(
                call: Call<SendOtpResponse?>?,
                response: Response<SendOtpResponse?>
            ) {
                iApiCallback.onSuccess("login", response, null)
            }

            public override fun onFailure(call: Call<SendOtpResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun verifyOtp(otp: String?, customerId: String?, type: String?, iApiCallback: IApiCallback) {
        val call: Call<SendOtpResponse?>? = service!!.verifyOtp(customerId, otp, type)
        call!!.enqueue(object : Callback<SendOtpResponse?> {
            public override fun onResponse(
                call: Call<SendOtpResponse?>?,
                response: Response<SendOtpResponse?>
            ) {
                iApiCallback.onSuccess("verifyOtp", response, null)
            }

            public override fun onFailure(call: Call<SendOtpResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getOnBoarding(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<OnBoardingResponse?>? = service!!.getOnBoarding()
        call!!.enqueue(object : Callback<OnBoardingResponse?> {
            public override fun onResponse(
                call: Call<OnBoardingResponse?>?,
                response: Response<OnBoardingResponse?>
            ) {
                iApiCallback.onSuccess("onBoarding", response, null)
            }

            public override fun onFailure(call: Call<OnBoardingResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun updateQuestionFirst(
        companyCode: String?,
        name: String?,
        gander: String?,
        birthDate: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<UpdateQuestionFirstResponse?>? =
            service!!.updateQuestionFirst(companyCode,name, gander, birthDate)
        call!!.enqueue(object : Callback<UpdateQuestionFirstResponse?> {
            public override fun onResponse(
                call: Call<UpdateQuestionFirstResponse?>?,
                response: Response<UpdateQuestionFirstResponse?>
            ) {
                iApiCallback.onSuccess("updateQuestionFirst", response, null)
            }

            public override fun onFailure(call: Call<UpdateQuestionFirstResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun updateQuestionSecond(
        profileId: String?,
        height_ft: String?,
        height_inches: String?,
        weight: String?,
        activity_score: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<UpdateQuestionFirstResponse?>? = service!!.updateQuestionSecond(
            height_ft,
            height_inches,
            weight,
            activity_score
        )
        call!!.enqueue(object : Callback<UpdateQuestionFirstResponse?> {
            public override fun onResponse(
                call: Call<UpdateQuestionFirstResponse?>?,
                response: Response<UpdateQuestionFirstResponse?>
            ) {
                iApiCallback.onSuccess("updateQuestionSecond", response, null)
            }

            public override fun onFailure(call: Call<UpdateQuestionFirstResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getProfile(iApiCallback: IApiCallback) {
        val call: Call<GetProfileResponse?>? = service!!.getProfile()
        call!!.enqueue(object : Callback<GetProfileResponse?> {
            public override fun onResponse(
                call: Call<GetProfileResponse?>?,
                response: Response<GetProfileResponse?>
            ) {
                iApiCallback.onSuccess("getProfile", response, null)
            }

            public override fun onFailure(call: Call<GetProfileResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }


    fun getupdatestepthree(iApiCallback: IApiCallback) {
        val call: Call<UpdateStepthreeResponce?>? = service!!.getprofilestepthree()
        call!!.enqueue(object : Callback<UpdateStepthreeResponce?> {
            public override fun onResponse(
                call: Call<UpdateStepthreeResponce?>?,
                response: Response<UpdateStepthreeResponce?>
            ) {
                iApiCallback.onSuccess("getupdatestepthree", response, null)
            }

            public override fun onFailure(call: Call<UpdateStepthreeResponce?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun updateProfile(
        name: String?,
        age: String?,
        sex: String?,
        email: String?,
        feet: String?,
        inch: String?,
        weight: String?,
        activityKey: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<GetProfileResponse?>? =
            service!!.updateProfile(name, age, sex, email, feet, inch, weight, activityKey)
        call!!.enqueue(object : Callback<GetProfileResponse?> {
            public override fun onResponse(
                call: Call<GetProfileResponse?>?,
                response: Response<GetProfileResponse?>
            ) {
                iApiCallback.onSuccess("updateProfile", response, null)
            }

            public override fun onFailure(call: Call<GetProfileResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getprofileUpdateStepthree(allergyId: String?, iApiCallback: IApiCallback) {
        val call: Call<UserDataResponse?>? = service!!.getprofileUpdateStepthree(allergyId)
        call!!.enqueue(object : Callback<UserDataResponse?> {
            public override fun onResponse(
                call: Call<UserDataResponse?>?,
                response: Response<UserDataResponse?>
            ) {
                iApiCallback.onSuccess("updatethree", response, null)
            }

            public override fun onFailure(call: Call<UserDataResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getupdatestepseven(
        profileid: String?,
        documentType: String?,
        filePath: String?,
        documentName: String?,
        iApiCallback: IApiCallback
    ) {
        val builder: MultipartBody.Builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("profileId", profileid!!)
        builder.addFormDataPart("documentType", documentType!!)
        val file = File(filePath)
        builder.addFormDataPart(
            "documents",
            file.name,
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        )
        builder.addFormDataPart("documentName", documentName!!)
        val requestBody: MultipartBody = builder.build()
        val call: Call<UploadDocumentResponse?>? = service!!.getupdatestrepseven(requestBody)
        call!!.enqueue(object : Callback<UploadDocumentResponse?> {
            public override fun onResponse(
                call: Call<UploadDocumentResponse?>?,
                response: Response<UploadDocumentResponse?>
            ) {
                iApiCallback.onSuccess("updatestepseven", response, "")
            }

            public override fun onFailure(call: Call<UploadDocumentResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMedicineList(
        customerId: String?,
        profileId: String?,
        page: Int,
        iApiCallback: IApiCallback
    ) {
        val call: Call<MedicineListResponse?>? =
            service!!.getMedicineList(page.toString())
        call!!.enqueue(object : Callback<MedicineListResponse?> {
            public override fun onResponse(
                call: Call<MedicineListResponse?>?,
                response: Response<MedicineListResponse?>
            ) {
                iApiCallback.onSuccess("medicineListMaster", response, null)
            }

            public override fun onFailure(call: Call<MedicineListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun medicineSyncComplete(
        customerId: String?,
        profileId: String?,
        sync_id: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<MedicineListResponse?>? =
            service!!.medicineSyncComplete(sync_id)
        call!!.enqueue(object : Callback<MedicineListResponse?> {
            public override fun onResponse(
                call: Call<MedicineListResponse?>?,
                response: Response<MedicineListResponse?>
            ) {
                iApiCallback.onSuccess("medicineSync", response, null)
            }

            public override fun onFailure(call: Call<MedicineListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMedicineList(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<MyMedicineListResponse?>? = service!!.getMyMedicineList()
        call!!.enqueue(object : Callback<MyMedicineListResponse?> {
            public override fun onResponse(
                call: Call<MyMedicineListResponse?>?,
                response: Response<MyMedicineListResponse?>
            ) {
                iApiCallback.onSuccess("myMedicineList", response, null)
            }

            public override fun onFailure(call: Call<MyMedicineListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun removeMedicineList(id: String?, time_slot: String, iApiCallback: IApiCallback) {
        val call: Call<MyMedicineListResponse?>? = service!!.removeMyMedicineList(id, time_slot)
        call!!.enqueue(object : Callback<MyMedicineListResponse?> {
            public override fun onResponse(
                call: Call<MyMedicineListResponse?>?,
                response: Response<MyMedicineListResponse?>
            ) {
                iApiCallback.onSuccess("removeMedicine", response, null)
            }

            public override fun onFailure(call: Call<MyMedicineListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyFoodDiaryList(profileId: String?, mealDate: String?, iApiCallback: IApiCallback) {
        val call: Call<GetMyFoodDiaryResponse?>? = service!!.getMyFoodDiaryList(mealDate)
        call!!.enqueue(object : Callback<GetMyFoodDiaryResponse?> {
            public override fun onResponse(
                call: Call<GetMyFoodDiaryResponse?>?,
                response: Response<GetMyFoodDiaryResponse?>
            ) {
                iApiCallback.onSuccess("myFoodDiaryList", response, null)
            }

            public override fun onFailure(call: Call<GetMyFoodDiaryResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMostSearchFoodDiaryList(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<GetMostSearchFoodItemResponse?>? =
            service!!.getMostSearchFoodDiaryList()
        call!!.enqueue(object : Callback<GetMostSearchFoodItemResponse?> {
            public override fun onResponse(
                call: Call<GetMostSearchFoodItemResponse?>?,
                response: Response<GetMostSearchFoodItemResponse?>
            ) {
                iApiCallback.onSuccess("mostSearchFoodDiaryList", response, null)
            }

            public override fun onFailure(
                call: Call<GetMostSearchFoodItemResponse?>?,
                t: Throwable
            ) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun removeMyFoodDiaryList(id: String?, iApiCallback: IApiCallback) {
        val call: Call<CommonResponse?>? = service!!.removeMyFoodDiary(id)
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("removeFoodDiary", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun addNewMedicine(
        profileId: String?,
        medicineId: String?,
        medicineName: String?,
        timeSlot: String?,
        dose: String?,
        sDate: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<SaveMedicineResponse?>? =
            service!!.addNewMedicine(medicineId, medicineName, timeSlot, dose, sDate)
        call!!.enqueue(object : Callback<SaveMedicineResponse?> {
            public override fun onResponse(
                call: Call<SaveMedicineResponse?>?,
                response: Response<SaveMedicineResponse?>
            ) {
                iApiCallback.onSuccess("addNewMedicine", response, null)
            }

            public override fun onFailure(call: Call<SaveMedicineResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun gethealthSummary(profileid: String?, iApiCallback: IApiCallback) {
        val call: Call<SummaryResponce?>? = service!!.gethealthsummary()
        call!!.enqueue(object : Callback<SummaryResponce?> {
            public override fun onResponse(
                call: Call<SummaryResponce?>?,
                response: Response<SummaryResponce?>
            ) {
                iApiCallback.onSuccess("summary", response, null)
            }

            public override fun onFailure(call: Call<SummaryResponce?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getPreLogin(fcmToken: String?, mobileNo: String?, iApiCallback: IApiCallback) {
        val call: Call<PreLoginResponse?>? = service!!.preLogin(fcmToken, mobileNo)
        call!!.enqueue(object : Callback<PreLoginResponse?> {
            public override fun onResponse(
                call: Call<PreLoginResponse?>?,
                response: Response<PreLoginResponse?>
            ) {
                iApiCallback.onSuccess("preLogin", response, null)
            }

            public override fun onFailure(call: Call<PreLoginResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun trackScreen(
        customerId: String?,
        profileId: String?,
        eventName: String?,
        actionId: String?,
        actionHeading: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? =
            service!!.trackScreen(eventName, actionId, actionHeading)
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("trackScreen", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun trackSteps(
        customerId: String?,
        profileId: String?,
        dateTime: String?,
        steps: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<StepCountResponse?>? =
            service!!.trackSteps(dateTime, steps)
        call!!.enqueue(object : Callback<StepCountResponse?> {
            public override fun onResponse(
                call: Call<StepCountResponse?>?,
                response: Response<StepCountResponse?>
            ) {
                iApiCallback.onSuccess("trackSteps", response, null)
            }

            public override fun onFailure(call: Call<StepCountResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun trackCal(
        customerId: String?,
        profileId: String?,
        dateTime: String?,
        cals: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<StepCountResponse?>? =
            service!!.trackCal(dateTime, cals)
        call!!.enqueue(object : Callback<StepCountResponse?> {
            public override fun onResponse(
                call: Call<StepCountResponse?>?,
                response: Response<StepCountResponse?>
            ) {
                iApiCallback.onSuccess("trackCal", response, null)
            }

            public override fun onFailure(call: Call<StepCountResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun updateUserLocation(
        profileId: String?,
        location_lat: String?,
        location_long: String?,
        location_pincode: String?,
        location_city: String?,
        location_state: String?,
        location_address: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.updateUserLocation(
            location_lat,
            location_long,
            location_pincode,
            location_city,
            location_state,
            location_address
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("updateUserLocation", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun buyPlanDetail(
        profileId: String?,
        heathplan_id: String?,
        healthplan_name: String?,
        health_plan_price: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<BuyPlanResponse?>? =
            service!!.buyPlanDetail(heathplan_id, healthplan_name, health_plan_price)
        call!!.enqueue(object : Callback<BuyPlanResponse?> {
            public override fun onResponse(
                call: Call<BuyPlanResponse?>?,
                response: Response<BuyPlanResponse?>
            ) {
                iApiCallback.onSuccess("BuyPlan", response, null)
            }

            public override fun onFailure(call: Call<BuyPlanResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun paymentConfirm(
        profileId: String?,
        heathplan_request_id: String?,
        payment_status: String?,
        payment_ref_data: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.paymentConfirm(
            heathplan_request_id,
            payment_status,
            payment_ref_data
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("paymentConfirm", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun paymentConfirmFail(
        profileId: String?,
        heathplan_request_id: String?,
        payment_status: String?,
        payment_ref_data: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.paymentConfirm(
            heathplan_request_id,
            payment_status,
            payment_ref_data
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("paymentConfirmFail", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getFoodList(
        customerId: String?,
        profileId: String?,
        page: Int,
        iApiCallback: IApiCallback
    ) {
        val call: Call<FoodDiaryListResponse?>? =
            service!!.getFoodList(page.toString())
        call!!.enqueue(object : Callback<FoodDiaryListResponse?> {
            public override fun onResponse(
                call: Call<FoodDiaryListResponse?>?,
                response: Response<FoodDiaryListResponse?>
            ) {
                iApiCallback.onSuccess("foodList", response, null)
            }

            public override fun onFailure(call: Call<FoodDiaryListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getFoodListMaster(fcmtoken: String?, page: Int, iApiCallback: IApiCallback) {
        val call: Call<FoodDiaryListResponse?>? =
            service!!.getFoodListMaster(fcmtoken, page.toString())
        call!!.enqueue(object : Callback<FoodDiaryListResponse?> {
            public override fun onResponse(
                call: Call<FoodDiaryListResponse?>?,
                response: Response<FoodDiaryListResponse?>
            ) {
                iApiCallback.onSuccess("foodListMaster", response, null)
            }

            public override fun onFailure(call: Call<FoodDiaryListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun foodSyncComplete(
        customerId: String?,
        profileId: String?,
        sync_id: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<FoodDiaryListResponse?>? =
            service!!.foodSyncComplete(sync_id)
        call!!.enqueue(object : Callback<FoodDiaryListResponse?> {
            public override fun onResponse(
                call: Call<FoodDiaryListResponse?>?,
                response: Response<FoodDiaryListResponse?>
            ) {
                iApiCallback.onSuccess("foodSync", response, null)
            }

            public override fun onFailure(call: Call<FoodDiaryListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun updateFoodDiaryItem(
        id: String?,
        profileId: String?,
        food_id: String?,
        food_name: String?,
        meal_time: String?,
        meal_date: String?,
        meal_quantity_type: String?,
        meal_quantity: String?,
        meal_quantity_unit: String?,
        food_type: String?,
        calorie_gm: String?,
        protein_gm: String?,
        carbs_gm: String?,
        fats_gm: String?,
        fiber_gm: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.updateFoodItem(
            id,
            food_id,
            food_name,
            meal_time,
            meal_date,
            meal_quantity_type,
            meal_quantity,
            meal_quantity_unit,
            food_type,
            calorie_gm,
            protein_gm,
            carbs_gm,
            fats_gm,
            fiber_gm
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("updateFoodDiary", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getAllBenefit(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<BenefitResponse?>? = service!!.getAllBenefit()
        call!!.enqueue(object : Callback<BenefitResponse?> {
            public override fun onResponse(
                call: Call<BenefitResponse?>?,
                response: Response<BenefitResponse?>
            ) {
                iApiCallback.onSuccess("AllBenefit", response, null)
            }

            public override fun onFailure(call: Call<BenefitResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getDoctorConsultation(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<DoctorConsultationResponse?>? =
            service!!.getDoctorConsultationResponse()
        call!!.enqueue(object : Callback<DoctorConsultationResponse?> {
            public override fun onResponse(
                call: Call<DoctorConsultationResponse?>?,
                response: Response<DoctorConsultationResponse?>
            ) {
                iApiCallback.onSuccess("doctorconsultation", response, null)
            }

            public override fun onFailure(call: Call<DoctorConsultationResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getDashboardData(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<DashboardResponse?>? = service!!.getDashboardData()
        call!!.enqueue(object : Callback<DashboardResponse?> {
            public override fun onResponse(
                call: Call<DashboardResponse?>?,
                response: Response<DashboardResponse?>
            ) {
                iApiCallback.onSuccess("getDashboardList", response, null)
            }

            public override fun onFailure(call: Call<DashboardResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun saveSugarData(profileid: String?, sugar: String?, iApiCallback: IApiCallback) {
        val call: Call<UserDataResponse?>? = service!!.saveSugarData(sugar)
        call!!.enqueue(object : Callback<UserDataResponse?> {
            public override fun onResponse(
                call: Call<UserDataResponse?>?,
                response: Response<UserDataResponse?>
            ) {
                iApiCallback.onSuccess("addSugarData", response, null)
            }

            public override fun onFailure(call: Call<UserDataResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun saveBloodPresureData(profileid: String?, sugar: String?, iApiCallback: IApiCallback) {
        val call: Call<UserDataResponse?>? = service!!.saveBPData(sugar)
        call!!.enqueue(object : Callback<UserDataResponse?> {
            public override fun onResponse(
                call: Call<UserDataResponse?>?,
                response: Response<UserDataResponse?>
            ) {
                iApiCallback.onSuccess("addBPData", response, null)
            }

            public override fun onFailure(call: Call<UserDataResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun saveWeightData(profileid: String?, sugar: String?, iApiCallback: IApiCallback) {
        val call: Call<UserDataResponse?>? = service!!.saveWeightData(sugar)
        call!!.enqueue(object : Callback<UserDataResponse?> {
            public override fun onResponse(
                call: Call<UserDataResponse?>?,
                response: Response<UserDataResponse?>
            ) {
                iApiCallback.onSuccess("addWeightData", response, null)
            }

            public override fun onFailure(call: Call<UserDataResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyMedicineList(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<MyMedicineResponse?>? = service!!.getMyMedicine()
        call!!.enqueue(object : Callback<MyMedicineResponse?> {
            public override fun onResponse(
                call: Call<MyMedicineResponse?>?,
                response: Response<MyMedicineResponse?>
            ) {
                iApiCallback.onSuccess("myMedicine", response, null)
            }

            public override fun onFailure(call: Call<MyMedicineResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getDiscountCheck(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<CommonResponse?>? = service!!.getCheckDiscount()
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("checkDiscount", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun addFoodDiaryItem(
        profileId: String?,
        food_id: String?,
        food_name: String?,
        meal_time: String?,
        meal_date: String?,
        meal_quantity_type: String?,
        meal_quantity: String?,
        meal_quantity_unit: String?,
        food_type: String?,
        calorie_gm: String?,
        protein_gm: String?,
        carbs_gm: String?,
        fats_gm: String?,
        fiber_gm: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.addFoodItem(
            food_id,
            food_name,
            meal_time,
            meal_date,
            meal_quantity_type,
            meal_quantity,
            meal_quantity_unit,
            food_type,
            calorie_gm,
            protein_gm,
            carbs_gm,
            fats_gm,
            fiber_gm
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("addFoodDiaryItem", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyCareTeam(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<MyCareTeamResponse?>? = service!!.getMyCareTeam()
        call!!.enqueue(object : Callback<MyCareTeamResponse?> {
            public override fun onResponse(
                call: Call<MyCareTeamResponse?>?,
                response: Response<MyCareTeamResponse?>
            ) {
                iApiCallback.onSuccess("myCareTeam", response, null)
            }

            public override fun onFailure(call: Call<MyCareTeamResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyCarePlan(iApiCallback: IApiCallback) {
        val call: Call<MyCareTeamResponse?>? = service!!.getMyCarePlan()
        call!!.enqueue(object : Callback<MyCareTeamResponse?> {
            public override fun onResponse(
                call: Call<MyCareTeamResponse?>?,
                response: Response<MyCareTeamResponse?>
            ) {
                iApiCallback.onSuccess("myCarePlan", response, null)
            }

            public override fun onFailure(call: Call<MyCareTeamResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getCallBackRequest(
        userType: String?,
        selectedService: String?,
        callbackNote: String?, iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? =
            service!!.callBackRequest(userType, selectedService, callbackNote)
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("callBackRequest", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getFeedbackRequest(
        serviceRatings: String?,
        selectedService: String?,
        serviceDontLike: String?,
        feedbackDetails: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? =
            service!!.callFeedbackRequest(
                serviceRatings,
                selectedService,
                serviceDontLike,
                feedbackDetails
            )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("callFeedbackRequest", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun addFamilyMember(
        profileId: String?,
        caregiver_name: String?,
        caregiver_relation: String?,
        caregiver_contact_no: String?,
        caregiver_note: String?,
        iApiCallback: IApiCallback
    ) {
        val call: Call<CommonResponse?>? = service!!.addFamilyMember(
            caregiver_name,
            caregiver_relation,
            caregiver_contact_no,
            caregiver_note
        )
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("addFamilyMember", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getHealthplan(iApiCallback: IApiCallback) {
        val call: Call<PlanListResponse?>? = service!!.healthPlan
        call!!.enqueue(object : Callback<PlanListResponse?> {
            public override fun onResponse(
                call: Call<PlanListResponse?>?,
                response: Response<PlanListResponse?>
            ) {
                iApiCallback.onSuccess("healthplan", response, null)
            }

            public override fun onFailure(call: Call<PlanListResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getHealthPlanDetail(profileId: String?, planId: String?, iApiCallback: IApiCallback) {
        val call: Call<HealthPlanDetailResponse?>? =
            service!!.getHealthPlanDetail(planId)
        call!!.enqueue(object : Callback<HealthPlanDetailResponse?> {
            public override fun onResponse(
                call: Call<HealthPlanDetailResponse?>?,
                response: Response<HealthPlanDetailResponse?>
            ) {
                iApiCallback.onSuccess("healthPlanDetail", response, null)
            }

            public override fun onFailure(call: Call<HealthPlanDetailResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getAnalyticReportDetail(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<AnalyticResponse?>? =
            service!!.getAnalyticDetail()
        call!!.enqueue(object : Callback<AnalyticResponse?> {
            public override fun onResponse(
                call: Call<AnalyticResponse?>?,
                response: Response<AnalyticResponse?>
            ) {
                iApiCallback.onSuccess("analyticReport", response, null)
            }

            public override fun onFailure(call: Call<AnalyticResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getTestBookPlanDetail(profileId: String?, planId: String?, iApiCallback: IApiCallback) {
        val call: Call<TestBookDetailResponse?>? = service!!.getTestBookDetail(planId)
        call!!.enqueue(object : Callback<TestBookDetailResponse?> {
            public override fun onResponse(
                call: Call<TestBookDetailResponse?>?,
                response: Response<TestBookDetailResponse?>
            ) {
                iApiCallback.onSuccess("testBookDetail", response, null)
            }

            public override fun onFailure(call: Call<TestBookDetailResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyHealthDiary(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<MyHealthDiaryResponse?>? = service!!.getMyHealthDiary()
        call!!.enqueue(object : Callback<MyHealthDiaryResponse?> {
            public override fun onResponse(
                call: Call<MyHealthDiaryResponse?>?,
                response: Response<MyHealthDiaryResponse?>
            ) {
                iApiCallback.onSuccess("myHealthDiary", response, null)
            }

            public override fun onFailure(call: Call<MyHealthDiaryResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun setStepGoal(profileId: String?, goalDataPoint: String?, iApiCallback: IApiCallback) {
        val call: Call<CommonResponse?>? = service!!.setStepGoal(goalDataPoint)
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("setStepGoal", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getSugarDetail(noofdays: String?, iApiCallback: IApiCallback) {
        val call: Call<SugarDetailResponse?>? = service!!.getSugarDetail(noofdays)
        call!!.enqueue(object : Callback<SugarDetailResponse?> {
            public override fun onResponse(
                call: Call<SugarDetailResponse?>?,
                response: Response<SugarDetailResponse?>
            ) {
                iApiCallback.onSuccess("sugarViewDetail", response, null)
            }

            public override fun onFailure(call: Call<SugarDetailResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getBPDetail(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<BPResponse?>? = service!!.getBPDetail()
        call!!.enqueue(object : Callback<BPResponse?> {
            public override fun onResponse(
                call: Call<BPResponse?>?,
                response: Response<BPResponse?>
            ) {
                iApiCallback.onSuccess("BPViewDetail", response, null)
            }

            public override fun onFailure(call: Call<BPResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun geDietChartDetailHome(iApiCallback: IApiCallback) {
        val call: Call<DietPlanHomeResponse?>? = service!!.getDietChartDetailHome()
        call!!.enqueue(object : Callback<DietPlanHomeResponse?> {
            public override fun onResponse(
                call: Call<DietPlanHomeResponse?>?,
                response: Response<DietPlanHomeResponse?>
            ) {
                iApiCallback.onSuccess("DietChartHomeResponse", response, null)
            }

            public override fun onFailure(call: Call<DietPlanHomeResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun geMyDietChartDetail(iApiCallback: IApiCallback) {
        val call: Call<MyDietPlanResponse?>? = service!!.getMyDietChartDetail()
        call!!.enqueue(object : Callback<MyDietPlanResponse?> {
            public override fun onResponse(
                call: Call<MyDietPlanResponse?>?,
                response: Response<MyDietPlanResponse?>
            ) {
                iApiCallback.onSuccess("MyDietPlanResponse", response, null)
            }

            public override fun onFailure(call: Call<MyDietPlanResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun geMeasurementsChartDetail(iApiCallback: IApiCallback) {
        val call: Call<MyMeasurementResponse?>? = service!!.getMeasurementsChartDetail()
        call!!.enqueue(object : Callback<MyMeasurementResponse?> {
            public override fun onResponse(
                call: Call<MyMeasurementResponse?>?,
                response: Response<MyMeasurementResponse?>
            ) {
                iApiCallback.onSuccess("MeasurementResponse", response, null)
            }

            public override fun onFailure(call: Call<MyMeasurementResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun geThingsToDoAvoidDetail(iApiCallback: IApiCallback) {
        val call: Call<ThingsToDoAvoidResponse?>? = service!!.getThingsToDoAvoidDetail()
        call!!.enqueue(object : Callback<ThingsToDoAvoidResponse?> {
            public override fun onResponse(
                call: Call<ThingsToDoAvoidResponse?>?,
                response: Response<ThingsToDoAvoidResponse?>
            ) {
                iApiCallback.onSuccess("ThingsToDoAvoidResponse", response, null)
            }

            public override fun onFailure(call: Call<ThingsToDoAvoidResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun geStressManagementDetail(iApiCallback: IApiCallback) {
        val call: Call<StressManagementResponse?>? = service!!.getStressManagementDetail()
        call!!.enqueue(object : Callback<StressManagementResponse?> {
            public override fun onResponse(
                call: Call<StressManagementResponse?>?,
                response: Response<StressManagementResponse?>
            ) {
                iApiCallback.onSuccess("StressManagementResponse", response, null)
            }

            public override fun onFailure(call: Call<StressManagementResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getPhysicalActivityDetail(iApiCallback: IApiCallback) {
        val call: Call<PhysicalActivityResponse?>? = service!!.getPhysicalActivityDetail()
        call!!.enqueue(object : Callback<PhysicalActivityResponse?> {
            public override fun onResponse(
                call: Call<PhysicalActivityResponse?>?,
                response: Response<PhysicalActivityResponse?>
            ) {
                iApiCallback.onSuccess("PhysicalActivityResponse", response, null)
            }

            public override fun onFailure(call: Call<PhysicalActivityResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getWeightDetail(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<WeightResponse?>? = service!!.getWeightDetail()
        call!!.enqueue(object : Callback<WeightResponse?> {
            public override fun onResponse(
                call: Call<WeightResponse?>?,
                response: Response<WeightResponse?>
            ) {
                iApiCallback.onSuccess("WeightViewDetail", response, null)
            }

            public override fun onFailure(call: Call<WeightResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getMyDocumentList(profileId: String?, iApiCallback: IApiCallback) {
        val call: Call<DocumentReportResponse?>? = service!!.getMyDocumentList()
        call!!.enqueue(object : Callback<DocumentReportResponse?> {
            public override fun onResponse(
                call: Call<DocumentReportResponse?>?,
                response: Response<DocumentReportResponse?>
            ) {
                iApiCallback.onSuccess("myDocumentList", response, null)
            }

            public override fun onFailure(call: Call<DocumentReportResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getBlogDetail(profileId: String?, blogId: String?, iApiCallback: IApiCallback) {
        val call: Call<BlogDetailResponse?>? =
            service!!.getBlogDetail(blogId)
        call!!.enqueue(object : Callback<BlogDetailResponse?> {
            public override fun onResponse(
                call: Call<BlogDetailResponse?>?,
                response: Response<BlogDetailResponse?>
            ) {
                iApiCallback.onSuccess("blogDetail", response, null)
            }

            public override fun onFailure(call: Call<BlogDetailResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    fun getUserPaidOrNot(iApiCallback: IApiCallback) {
        val call: Call<PremiumResponse?>? = service!!.getUserPaidOrNot()
        call!!.enqueue(object : Callback<PremiumResponse?> {
            public override fun onResponse(
                call: Call<PremiumResponse?>?,
                response: Response<PremiumResponse?>
            ) {
                iApiCallback.onSuccess("IsPremium", response, null)
            }

            public override fun onFailure(call: Call<PremiumResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }


    fun saveMyStepsList(obj: JsonObject, iApiCallback: IApiCallback) {
        val call: Call<CommonResponse?>? = service!!.saveMyStepsList(obj)
        call!!.enqueue(object : Callback<CommonResponse?> {
            public override fun onResponse(
                call: Call<CommonResponse?>?,
                response: Response<CommonResponse?>
            ) {
                iApiCallback.onSuccess("saveStepList", response, null)
            }

            public override fun onFailure(call: Call<CommonResponse?>?, t: Throwable) {
                iApiCallback.onFailure("" + t.message)
            }
        })
    }

    companion object {
        private var service: APIService? = null
        val instance: ApiCall
            get() {
                if (service == null) {
                    service = RestClient.client
                }
                return ApiCall()
            }
    }
}
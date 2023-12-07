package app.oxyjon.bean

data class OnBoardingResponse(
    val `data`: Data,
    val errorCode: String,
    val errorMsg: String
) {
    data class Data(
        val customer_id: String,
        val onboard_screen_quest1: OnboardScreenQuest1,
        val onboard_screen_quest2: OnboardScreenQuest2,
        val onboard_screen_quest3: OnboardScreenQuest3,
        val profileId: String,
        val screen_quest1: Boolean,
        val screen_quest2: Boolean,
        val screen_quest3: Boolean
    ) {
        data class OnboardScreenQuest1(
            val is_required: String,
            val profiledata: Profiledata,
            val screen_message: String,
            val screen_title: String
        ) {
            data class Profiledata(
                val birthDate: String,
                val fullname: String,
                val gender: String,
                val patient_age: String
            )
        }

        data class OnboardScreenQuest2(
            val activity_list: ArrayList<Activity>,
            val is_required: String,
            val lifestyledata: Lifestyledata,
            val screen_message: String,
            val screen_title: String
        ) {
            data class Activity(
                val key: String,
                val value: String,
                var selectedOption: Boolean = false
            )

            data class Lifestyledata(
                val activity_score: String,
                val height_ft: String,
                val height_inches: String,
                val weight: String
            )
        }

        data class OnboardScreenQuest3(
            val is_required: String,
            val medical_condition_list: ArrayList<MedicalCondition>,
            val screen_message: String,
            val screen_title: String
        ) {
            data class MedicalCondition(
                val key: String,
                val value: String,
                var is_selected: Boolean,
            )
        }
    }
}
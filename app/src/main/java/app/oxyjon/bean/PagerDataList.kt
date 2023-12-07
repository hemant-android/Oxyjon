package app.oxyjon.bean


class PagerDataList {
    var name: String? = null
    var food: String? = null
    var location: String? = null
    var time: String? = null
    var fav: String? = null
    var distance: String? = null
    var pic: String? = null
    var latitude: String? = null
    var longitude: String? = null

    constructor() {}
    constructor(
        name: String?,
        food: String?,
        location: String?,
        time: String?,
        fav: String?,
        distance: String?,
        pic: String?,
        latitude: String?,
        longitude: String?
    ) {
        this.name = name
        this.food = food
        this.location = location
        this.time = time
        this.fav = fav
        this.distance = distance
        this.pic = pic
        this.latitude = latitude
        this.longitude = longitude
    }
}
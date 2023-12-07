package app.oxyjon.bean


class Tablist {
    private var isIsclicked = false
    var title: String? = null

    constructor() {}
    constructor(isclicked: Boolean, title: String?) {
        isIsclicked = isclicked
        this.title = title
    }

    fun setIsclicked(isclicked: Boolean) {
        isIsclicked = isclicked
    }
}
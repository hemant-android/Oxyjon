package app.oxyjon.interfaces


open interface IRecyclerClickListener {
    fun onRecyclerItemClick(pos: Any, data: Any, extraData: Any?)
}
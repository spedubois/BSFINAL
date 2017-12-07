package com.example.spenserdubois.battleship

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.game_text_view.view.*

import kotlinx.android.synthetic.main.game_text_view.view.statusWinner as importedGameTextView
import kotlinx.android.synthetic.main.game_text_view.view.statusTurn as importedStatusTextView
import kotlinx.android.synthetic.main.game_text_view.view.ships1 as importedShips1TextView
import kotlinx.android.synthetic.main.game_text_view.view.ships2 as importedShips2TextView


/**
 * Created by Spenser DuBois on 11/5/2017.
 */
class GameTextView : FrameLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var title: String
        get() = gameTextView.text.toString()
        set(newTitle) {gameTextView.text = newTitle}
    var status: String
        get() = gameTextView.text.toString()
        set(newTitle) {statusTextView.text = newTitle}
    var ships1 : String
        get()  = gameTextView.text.toString()
        set(newTitle) {ship1TextView.text = newTitle}
    var ships2 : String
        get()  = gameTextView.text.toString()
        set(newTitle) {ship2TextView.text = newTitle}


    private var gameTextView = TextView(context)
    private var statusTextView = TextView(context)
    private var ship1TextView = TextView(context)
    private var ship2TextView = TextView(context)


    override fun onFinishInflate() {
        super.onFinishInflate()
        gameTextView = importedGameTextView
        statusTextView = importedStatusTextView
        ship1TextView = importedShips1TextView
        ship2TextView = importedShips2TextView
    }

}
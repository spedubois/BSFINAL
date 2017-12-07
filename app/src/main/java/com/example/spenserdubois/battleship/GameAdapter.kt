package com.example.spenserdubois.battleship

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import kotlinx.android.synthetic.main.game_text_view.*

@Suppress("UNREACHABLE_CODE")
/**
 * Created by Spenser DuBois on 11/5/2017.
 */
class GameAdapter(private val dataset: Array<CustomAdapterItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface OnCustomAdapterItemSelectedListener{
        fun customeAdapterItemSelected(customAdapterItem: CustomAdapterItem)
    }

    private var onCustomAdapterItemSelectedListener : OnCustomAdapterItemSelectedListener? = null

    fun setOnCustomAdapterItemSelectedListener(onCustomAdapterItemSelectedListener: OnCustomAdapterItemSelectedListener)
    {
        this.onCustomAdapterItemSelectedListener = onCustomAdapterItemSelectedListener
    }

    fun setOnCustomAdapterItemSelectedListener(onCustomAdapterItemSelectedListener: ((customAdapterItem : CustomAdapterItem)->Unit))
    {
        this.onCustomAdapterItemSelectedListener = object : OnCustomAdapterItemSelectedListener{
            override fun customeAdapterItemSelected(customAdapterItem: CustomAdapterItem) {
                onCustomAdapterItemSelectedListener(customAdapterItem)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemViewType(position: Int): Int = dataset[position].adapterItemType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType){
            AdapterItemType.BS_GAME.ordinal -> {
                GameViewHolder(layoutInflater.inflate(R.layout.game_text_view, parent, false) as GameTextView).apply{
                    gameTextView.setOnClickListener{clickedView:View ->
                        onCustomAdapterItemSelectedListener?.customeAdapterItemSelected(dataset[adapterPosition])
                    }
                }
            }
            else -> {
                throw AssertionError()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if(holder !is GameViewHolder)
            throw AssertionError("Bad Holder")
        when(holder.itemViewType){
            AdapterItemType.BS_GAME.ordinal -> {

                val dataSetItem: CustomAdapterItem = dataset[position]
                if(dataSetItem !is BSGame)
                    throw AssertionError("Not BSGame")
                val manager = dataSetItem.manager
                if(manager.state.equals("Completed\n"))
                {
                    holder.gameTextView.title = manager.winner + "\n  WON!"
                    holder.gameTextView.status = manager.state+"\n"
                }
                else
                {
                    holder.gameTextView.title= manager.state
                    holder.gameTextView.status = manager.playerTurn+"'s\n     turn"
                }
                holder.gameTextView.ships1 = "Player 1\n" + "     "+ manager.players[0].boatsLeft
                holder.gameTextView.ships2 = "Player 2\n" + "     "+ manager.players[1].boatsLeft
            }
        }
    }

    override fun getItemCount(): Int = dataset.size


    enum class AdapterItemType{
        BS_GAME
    }


    interface CustomAdapterItem{
        val adapterItemType : AdapterItemType
    }

    data class BSGame(val manager: GameManager) : CustomAdapterItem{
        override val adapterItemType: AdapterItemType = AdapterItemType.BS_GAME
    }

    class GameViewHolder(val gameTextView: GameTextView) : RecyclerView.ViewHolder(gameTextView)
}
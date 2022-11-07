package com.example.covidtracker

import android.content.ClipData.Item
import android.graphics.Paint.Join
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat

class Adapter(private val countrylist: List<ModelClass>): RecyclerView.Adapter<Adapter.ViewHolder>() {
    var m: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var modelclass: ModelClass = countrylist[position]
        if(m == 1){
            holder.cases.text = modelclass.cases
        }
        else if(m == 2){
            holder.cases.text = modelclass.recovered
        }
        else if(m == 3){
            holder.cases.text = modelclass.deaths
        }
        else {
            holder.cases.text = modelclass.active
        }
        holder.country.text = modelclass.country
    }

    override fun getItemCount(): Int {
        return countrylist.size
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        var cases = ItemView.findViewById<TextView>(R.id.countrycase)
        var country = ItemView.findViewById<TextView>(R.id.countryname)
    }

    fun filter(charText: String) {
        if(charText == "cases"){
            m=1
        }
        else if(charText == "recovered"){
            m=2
        }
        else if(charText == "deaths"){
            m=3
        }
        else {
            m=4
        }
        notifyDataSetChanged()
    }

}


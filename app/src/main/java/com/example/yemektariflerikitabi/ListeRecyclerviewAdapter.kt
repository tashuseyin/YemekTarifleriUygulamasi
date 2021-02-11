package com.example.yemektariflerikitabi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cardview_tasarim_tutucu.view.*

class ListeRecyclerviewAdapter( val yemekListesi:ArrayList<String>,val idListesi:ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerviewAdapter.YemekHolder>() {

    class YemekHolder(view:View) : RecyclerView.ViewHolder(view){



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val tasarim =  LayoutInflater.from(parent.context).inflate(R.layout.cardview_tasarim_tutucu,parent,false)
        return YemekHolder(tasarim)
    }


    override fun getItemCount(): Int {
        return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.textViewyemekisim.text = yemekListesi[position]

        holder.itemView.setOnClickListener {

            val action  = YemekListeFragmentDirections.actionYemekListeFragmentToTarifFragment("recyclerdangeldim", idListesi[position])
            Navigation.findNavController(it).navigate(action)


        }

    }

}
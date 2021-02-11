package com.example.yemektariflerikitabi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_yemek_liste.*
import java.lang.Exception
import java.sql.Blob

class YemekListeFragment : Fragment() {

    var yemekIsmiListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecyclerviewAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yemek_liste, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecyclerviewAdapter(yemekIsmiListesi,yemekIdListesi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter  = listeAdapter

        sqlVeriAlma()

    }




    fun sqlVeriAlma(){


        try {

            context?.let {
                val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)

                val cursor = db.rawQuery("SELECT * FROM yemekler",null)

                val yemekId = cursor.getColumnIndex("id")
                val yemekismi = cursor.getColumnIndex("yemekismi")
                val yemekGorsel = cursor.getColumnIndex("gorsel")

                yemekIdListesi.clear()
                yemekIsmiListesi.clear()

                while (cursor.moveToNext()){
                    yemekIdListesi.add(cursor.getInt(yemekId))
                    yemekIsmiListesi.add(cursor.getString(yemekismi))


                }
                // VEriler değiştiğinde örnegin yeni veri eklendginde bunu anlayıp recyclerviewi güncelleyecek
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }


        }catch(e:Exception){
            e.printStackTrace()
        }


    }


}
package com.example.yemektariflerikitabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menu_tasarim = menuInflater.inflate(R.menu.menu_tasarim,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_yemekekle){

            val action = YemekListeFragmentDirections.actionYemekListeFragmentToTarifFragment("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragment).navigate(action)

        }
        return super.onOptionsItemSelected(item)
    }


}
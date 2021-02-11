package com.example.yemektariflerikitabi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream

class TarifFragment : Fragment() {



    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonKaydet.setOnClickListener {
            Kaydet(it)
        }

        imageViewgorsel.setOnClickListener {
            GorselSec(it)
        }

        arguments?.let{
            var gelenBilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if(gelenBilgi.equals("menudengeldim")){
                // yeni bir yemek eklemeye geldi
                editTextYemekismi.setText("")
                editTextYemekMalzemeleri.setText("")
                buttonKaydet.visibility = View.VISIBLE

                val gorselsecmearkaplanı = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                imageViewgorsel.setImageBitmap(gorselsecmearkaplanı)

            }else{
                // Daha önce olusturulan yemegi görmeye geldi
                buttonKaydet.visibility = View.INVISIBLE

                val secilenId = TarifFragmentArgs.fromBundle(it).id

                context?.let {

                    try {
                        val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(secilenId.toString()))

                        val yemekismiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekmalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorselIndex = cursor.getColumnIndex("gorsel")


                        while (cursor.moveToNext()){
                            editTextYemekismi.setText(cursor.getString(yemekismiIndex))
                            editTextYemekMalzemeleri.setText(cursor.getString(yemekmalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorselIndex)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageViewgorsel.setImageBitmap(bitmap)
                        }
                        cursor.close()
                        
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                }
            }
        }

    }

    fun Kaydet(view: View){
        // SQlite kaydetme

        val yemekismi = editTextYemekismi.text.toString()
        val yemekmalzemeleri = editTextYemekMalzemeleri.text.toString()

        if (secilenBitmap != null){

            val kucukBitmap = bitmapKucultmeOlustur(secilenBitmap!!,300) // Buradaki 300 degerini deneme yanılma yoluyla bulabiliriz

            // SEcilen görseli Sqlite kaydetmek için ilk başta o resmi veriye dönüştürmek lazım
             val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream) // quality 0-100 arası bir değerdir
            val ByteDizisi = outputStream.toByteArray()




            try {

                context?.let {
                    val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                    db.execSQL("CREATE TABLE IF NOT EXISTS  yemekler (id INTEGER PRIMARY KEY , yemekismi VARCHAR, yemekmalzemesi VARCHAR,gorsel BLOB)")
                    val sqlString = "INSERT INTO yemekler (yemekismi, yemekmalzemesi, gorsel) VALUES (?,?,?)"
                    val statement = db.compileStatement(sqlString)
                    statement.bindString(1,yemekismi)
                    statement.bindString(2,yemekmalzemeleri)
                    statement.bindBlob(3,ByteDizisi)
                    statement.execute()
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

            // Kayıt işlemi bittikten sonra yemek listesi fragmentina geri döndük
            val action = TarifFragmentDirections.actionTarifFragmentToYemekListeFragment()
            Navigation.findNavController(view).navigate(action)

        }





    }

    fun GorselSec(view: View){

        // ContextCompat kullanılır çünkü api 19 undan önce mi çalışacak yoksa api 19 dan sonra mı çalışacak bunlarla uğraşmamak için kullanılır Yani uyumlu olması nedeniyle kullanılır
        // PackageManager.PERMISSION_GRANTED izin verildi demek ama PackageManager.PERMISSION_DENIED izin verilmedi demektir

        // eger activity varsa çalış demektir
        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // izin verilmedi, izin istemek gerekli
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                // izin verilmiş tekrar isttemeden galeriye git

                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }

    }


 // istenilen izinlerin sonuçlarını degerlendirdiğimiz fonksiyon
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray // grantResults verilen sonuclar demektir
    ) {
     if (requestCode == 1){

         // bir sonucum var mı  ve sonucların sonuclar izin verildiyse
         if (grantResults.size > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED ){
             //izni aldık

             val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
             startActivityForResult(galeriIntent,2)
         }
     }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    // Galeriye gidilince ne olacak resulCode-> demek cevap kodu demektir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null ){

            secilenGorsel = data.data // secilen görselin telefonda nerede durdugu bilgisini aldık

            try {

                context?.let {
                    if (secilenGorsel != null ){
                        if (Build.VERSION.SDK_INT >=28){
                            val source = ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageViewgorsel.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageViewgorsel.setImageBitmap(secilenBitmap)

                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun bitmapKucultmeOlustur(kullanicininsectigbitmap : Bitmap,maximumBoyut: Int) : Bitmap{

        var width = kullanicininsectigbitmap.width
        var height = kullanicininsectigbitmap.height


        val BitmapOrani : Double = width.toDouble() /height.toDouble()

        // Görselin yatay mı dikey mi oldugunu anlayıp ona göre bir küçültme işlemi yapmalıyız
        if (BitmapOrani > 1){
            //görselimiz yatay
            width = maximumBoyut
            val kisaltilmisBoyut = width / BitmapOrani
            height = kisaltilmisBoyut.toInt()

        }else{
            //görselimiz dikey

            height = maximumBoyut
            val kisaltilmisBoyut = height * BitmapOrani
            width = kisaltilmisBoyut.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicininsectigbitmap,width,height,true)

    }


}
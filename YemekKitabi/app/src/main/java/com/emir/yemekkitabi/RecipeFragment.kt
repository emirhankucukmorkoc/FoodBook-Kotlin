package com.emir.yemekkitabi

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.emir.yemekkitabi.databinding.ActivityMainBinding
import com.emir.yemekkitabi.databinding.FragmentRecipeBinding
import java.io.ByteArrayOutputStream
import kotlin.Exception

class RecipeFragment : Fragment() {
    lateinit var binding: FragmentRecipeBinding
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            context?.let {
                val database = it.openOrCreateDatabase("yemekler", Context.MODE_PRIVATE, null)

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            save(it)

        }

        binding.imageView.setOnClickListener {
            chooseImage(it)
        }

        arguments?.let {

            var gelenBilgi = RecipeFragmentArgs.fromBundle(it).bilgi

            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir yemek eklemeye geldi
                binding.editTextText.setText("")
                binding.editTextText2.setText("")
                binding.button.visibility = View.VISIBLE

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.image)
                binding.imageView.setImageBitmap(gorselSecmeArkaPlani)

            } else {
                //daha önce oluşturulan yemeği görmeye geldi
                binding.button.visibility = View.INVISIBLE

                val secilenId = RecipeFragmentArgs.fromBundle(it).id

                context?.let {

                    try {

                        val db = it.openOrCreateDatabase("yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(secilenId.toString()))

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("yemekmalzemesi")
                        val yemekGorseli = cursor.getColumnIndex("gorsel")

                        while(cursor.moveToNext()){
                            binding.editTextText.setText(cursor.getString(yemekIsmiIndex))
                            binding.editTextText2.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            binding.imageView.setImageBitmap(bitmap)
                        }

                        cursor.close()

                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                }

            }

        }

    }

    fun save(view: View) {
        //Sqlite kaydetme
        var yemekIsmi = binding.editTextText.text.toString()
        val yemekMalzemeleri = binding.editTextText2.text.toString()

        if (secilenBitmap != null){
            val kucukBitmap = kucukBitmapOlusturma(secilenBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("yemekler", Context.MODE_PRIVATE,null)

                    val sqlString = "INSERT INTO yemekler(yemekismi,yemekmalzemesi,gorsel) VALUES(?, ? , ?)"
                    val statment = database.compileStatement(sqlString)

                    statment.bindString(1,yemekIsmi)
                    statment.bindString(2,yemekMalzemeleri)
                    statment.bindBlob(3,byteDizisi)
                    statment.execute()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

            val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)

        }


    }

    fun chooseImage(view: View) {
        // Galeriye erişim iznini kontrol edin
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //izin verilmedi, izin istememiz gerekiyor
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)

            } else {                //izin zaten verilmiş, tekrar istemeden galeriye git
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)

            }
        }

        /*

Tabii, kodu adım adım açıklayayım:

Bu kod, bir resim seçmek için galeriye erişim izni kontrolü yapar ve izin almak veya zaten alınmışsa kullanıcıyı galeriye yönlendirir.

fun chooseImage(view: View): Bu fonksiyon bir düğmeye tıklandığında çağrılır ve galeriye erişim iznini kontrol eder ve gerektiğinde izin isteme işlemi yapar veya izin zaten alınmışsa galeriye gitmeyi sağlar.

activity?.let { ... }: Bu yapı, mevcut fragmentin bağlı olduğu Activity'nin mevcut olup olmadığını kontrol eder. activity? ifadesi, Activity nesnesinin null olma durumuna karşı güvenli bir şekilde işlem yapmak için kullanılır.

ContextCompat.checkSelfPermission(...): Bu metot, belirtilen iznin verilip verilmediğini kontrol eder. Eğer izin verilmemişse, işlem yapılmadan önce izin alınmalıdır.

it.applicationContext: activity?.let yapısının içinde, Activity'nin mevcut olduğu varsayıldığından, "it" anahtar kelimesi Activity'yi temsil eder. Burada, izin durumunu kontrol etmek için Activity'nin uygulama bağlamını (applicationContext) kullanıyoruz.

requestPermissions(...): Eğer izin verilmemişse, izin isteme işlemini başlatırız. Burada, READ_MEDIA_IMAGES izni için "requestPermissions" metodunu çağırarak kullanıcıdan izin istiyoruz. İkinci parametre olan 1, izin talebi için bir request code'dur ve onRequestPermissionsResult metodunda bu kod ile izin sonuçlarını işleyebilirsiniz.

else: Eğer izin zaten verilmişse, kullanıcıya tekrar izin istemeden galeriye gitmek için bir Intent oluşturulur ve startActivityForResult çağrılır. startActivityForResult, galeriden bir resim seçildiğinde geri dönüş sonuçlarını almanıza olanak tanır.

Yukarıdaki adımlarla, kullanıcının izinleri kontrol edip, istemek veya izin zaten verilmişse doğrudan galeriye gitmek için kodları açıklamış olduk. Bu kod sayesinde kullanıcılar izin vermediğinde izin istemek ve galeriden resim seçmek için uygun yönlendirmeleri alabilirsiniz.
         */
    }


    // onRequestPermissionsResult ile izin durumunu kontrol edin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izni aldık

                val galeryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent, 2)
            } else {
                // İzin reddedildi, kullanıcıya bilgi ver
                Toast.makeText(
                    requireContext(),
                    "Galeriye erişim izni reddedildi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /*
        Bu onRequestPermissionsResult fonksiyonu, kullanıcı izin isteği sonucunu işlemek için kullanılır. İzin talep kodu olan 1 ile başarılı bir şekilde izin alındığında (PackageManager.PERMISSION_GRANTED), galeriye gitmek için bir Intent oluşturulur ve startActivityForResult çağrısı yapılır. Eğer izin reddedilirse veya kullanıcı "İptal" düğmesine basarsa, herhangi bir işlem yapılmaz.

Burada, kullanıcının izinleri ayarlardan verip geri döndüğünde uygulamanın durumunu güncellemek veya gerekli işlemleri yapmak için onRequestPermissionsResult fonksiyonuna işlem ekleyebilirsiniz.

Bir örnek olarak, kullanıcının izinleri reddettiğinde veya izinler ayarlardan verildiğinde bir Toast mesajı gösterebilirsiniz:
         */
    }




    // onActivityResult ile galeri seçim sonucunu işleyin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data


            try {
                context?.let {
                    if (secilenGorsel != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)

                        } else {
                            secilenBitmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /*
    bu kod, resim seçme işlemi sonucunu işlemek için onActivityResult fonksiyonudur. startActivityForResult çağrısı ile bir resim seçmek için galeriye gittikten sonra, kullanıcı geri döndüğünde bu fonksiyon çağrılır.

requestCode == 2 && resultCode == Activity.RESULT_OK && data != null: Bu satır, doğru isteğin sonucu olduğunu ve sonuç verisi olan bir Intent nesnesinin olduğunu kontrol eder. requestCode önceki galeriye gitme isteğinin request code'udur (2). resultCode, galeriye gitme işleminden dönen sonucun durumunu belirtir ve Activity.RESULT_OK sonucun başarıyla tamamlandığını gösterir. data != null, geri dönen verilerin boş olmadığını kontrol eder.

secilenGorsel = data.data: Geri dönen Intent içindeki verilerden, seçilen resmin URI'sini (Uri) elde ederiz ve secilenGorsel değişkenine atarız. Bu, resmi yüklemek ve göstermek için kullanacağımız URI'dir.

context?.let { ... }: Bu yapı, fragmentin bağlı olduğu Activity'nin bağlamını (context) güvenli bir şekilde kullanmak için kullanılır.

if (secilenGorsel != null) { ... }: Seçilen resmin URI'si null değilse, resmi yüklemek ve göstermek için gerekli işlemleri gerçekleştiririz.

Android 10 (API seviye 29) ve sonraki sürümlerde, resimleri işlemek için ImageDecoder sınıfı kullanılır. Build.VERSION.SDK_INT >= 28 ile bu durumu kontrol ederiz. Eğer cihaz Android 10 veya daha yeni bir sürümse, resmi ImageDecoder ile yükler ve secilenBitmap değişkenine atarız. Aksi takdirde, Android 9 ve altındaki sürümlerde, resmi MediaStore.Images.Media.getBitmap ile yükleriz.

binding.imageView.setImageBitmap(secilenBitmap): Son olarak, yüklenen resmi binding.imageView adlı ImageView içinde gösteririz.

Bu sayede, kullanıcının galeriden seçtiği resmi yükleyip göstermek için gerekli işlemleri yapmış olursunuz. Eğer seçilen resmi farklı bir şekilde işlemek isterseniz, kodu buna göre düzenleyebilirsiniz.
     */

    fun kucukBitmapOlusturma (kullanicininSecitigiBitmap : Bitmap , maximumBoy : Int) : Bitmap{

        var width = kullanicininSecitigiBitmap.width
        var height = kullanicininSecitigiBitmap.height

        val bitMapOrani : Double = width.toDouble() / height.toDouble()

        if (bitMapOrani>1){
            //görselim yatay
            width = maximumBoy
            val kisaltilmisHight = width / bitMapOrani
            height = kisaltilmisHight.toInt()

        }else {

            height = maximumBoy
            val kisaltilmisWidth = width* bitMapOrani
            width = kisaltilmisWidth.toInt()

        }




        return Bitmap.createScaledBitmap(kullanicininSecitigiBitmap,width,height,true)


    }







}



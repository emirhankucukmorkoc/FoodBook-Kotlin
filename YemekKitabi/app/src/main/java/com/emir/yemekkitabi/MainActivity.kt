package com.emir.yemekkitabi


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import com.emir.yemekkitabi.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById(R.id.tool_bar) as Toolbar?
        toolbar?.setTitle("Yemek Kitabı")// Toolbar başlığını değiştirdik.
        toolbar?.setTitleTextColor(Color.WHITE) //Toolbar başlık rengini değiştirdik.
        setSupportActionBar(toolbar);


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_food, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_food_item){
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragment).navigate(action)
        }

        return super.onOptionsItemSelected(item)
    }



}






package com.emir.yemekkitabi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.emir.yemekkitabi.databinding.RecyclerRowBinding

class ListRecyclerAdapter(val yemekListesi: ArrayList<String>, val idListesi: ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.YemekHolder>() {

    class YemekHolder(private val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // Yemek adı için bir setYemekAdi fonksiyonu oluşturuyoruz
        fun setYemekAdi(yemekAdi: String) {
            binding.recyclerViewTextView.text = yemekAdi
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater = LayoutInflater.from(parent.context)
        // Binding objesini oluşturup YemekHolder'a geçiyoruz
        val binding = RecyclerRowBinding.inflate(inflater, parent, false)
        return YemekHolder(binding)
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        val yemek = yemekListesi[position]
        holder.setYemekAdi(yemek)

        // Öğe tıklanması durumunda işlem yapmak için aşağıdaki gibi bir onClickListener ekleyebilirsiniz
        holder.itemView.setOnClickListener {
            // Tıklanan öğenin ID'sini almak için idListesi'nden pozisyona uygun ID'yi alabilirsiniz

            // Burada tıklanan öğenin ID'sini kullanarak istediğiniz işlemi gerçekleştirebilirsiniz
            // Örneğin, Navigation ile başka bir fragment açmak için:
            // val action = ListeFragmentDirections.actionListeFragmentToDetayFragment(id)
            // Navigation.findNavController(holder.itemView).navigate(action)


            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }
}

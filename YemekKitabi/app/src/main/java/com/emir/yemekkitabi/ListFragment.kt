package com.emir.yemekkitabi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.emir.yemekkitabi.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var yemekIsmiListesi = ArrayList<String>()
    private var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter: ListRecyclerAdapter
    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeAdapter = ListRecyclerAdapter(yemekIsmiListesi, yemekIdListesi)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = listeAdapter



        sqlVeriAlma()
    }


    private fun sqlVeriAlma() {
       try {
           context?.let {
               val database = it.openOrCreateDatabase("yemekler", Context.MODE_PRIVATE, null)

               val cursor = database.rawQuery("SELECT * FROM yemekler", null)
               val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
               val yemekIdIndex = cursor.getColumnIndex("id")


               yemekIsmiListesi.clear()
               yemekIdListesi.clear()

               while (cursor.moveToNext()) {
                   yemekIsmiListesi.add(cursor.getString(yemekIsmiIndex))
                   yemekIdListesi.add(cursor.getInt(yemekIdIndex))
               }

               listeAdapter.notifyDataSetChanged()
               cursor.close()
           }
       }catch (e :Exception){
           e.printStackTrace()
       }

    }
}

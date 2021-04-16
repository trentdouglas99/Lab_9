package com.csci448.trentdouglas.lab_9.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci448.trentdouglas.lab_9.R
import com.csci448.trentdouglas.lab_9.data.MarkerData
import com.csci448.trentdouglas.lab_9.databinding.FragmentListBinding
import java.util.*

class HistoryFragment : Fragment() {

    private var _callbacks: Callbacks? = null
    // only accessible between onAttach & onDetach
    private val callbacks get() = _callbacks!!


    interface Callbacks {
        //fun getOrientation(): Int
        fun onCrimeSelected(crimeId: UUID)
    }


    companion object {
        private const val LOG_TAG = "448.HistoryListFrag"
        public lateinit var markerListViewModel: MarkerListViewModel
    }

    override fun onAttach(context: Context){
        Log.d(LOG_TAG, "onAttach() called")
        super.onAttach(context)
//        try {
//            _callbacks = (context as Callbacks)
//        } catch (e: ClassCastException) {
//            throw ClassCastException("$context must implement HistoryListFragment.Callbacks")
//        }

    }
    override fun onCreate(savedInstanceState: Bundle?){
        Log.d(LOG_TAG, "onCreate() called")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val factory = MarkerListViewModelFactory(requireContext())
        markerListViewModel = ViewModelProvider(this@HistoryFragment, factory).get(MarkerListViewModel::class.java)

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.d(LOG_TAG, "onOptionsItemSelected() called")
//        return when(item.itemId) {
//            R.id.new_crime_menu_item -> {
//                val crime = Crime()
//                markerListViewModel.addCrime(crime)
//                val action = CrimeListFragmentDirections.actionCrimeListFragmentToCrimeDetailFragment(crime.id)
//                findNavController().navigate(action)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
//        inflater.inflate(R.menu.fragment_crime_list, menu)
//    }

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!


    private lateinit var adapter: MarkerListAdapter

    private fun updateUI(markers: List<MarkerData>) {
        adapter = MarkerListAdapter(markers) { marker: MarkerData ->
//            if(callbacks.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
//                // we are in one pane mode with a NavController
//                val action = CrimeListFragmentDirections.actionCrimeListFragmentToCrimeDetailFragment(crime.id)
//                findNavController().navigate(action)
//            } else {
//                // we are in two pane mode, we don’t know what else
//                // exists..Host, handle for us!
//                callbacks.onCrimeSelected(crime.id)
//            }
        }
        binding.markerListRecyclerView.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        Log.d(LOG_TAG, "onCreateView() called")
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.markerListRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(emptyList())

        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
        markerListViewModel.markerListLiveData.observe(
            viewLifecycleOwner,
            Observer { markers ->
                markers?.let{
                    Log.i(LOG_TAG, "Got markers ${markers.size}")
                    updateUI(markers)
                }
            }
        )
    }
    override fun onActivityCreated(savedInstanceState: Bundle?){
        Log.d(LOG_TAG, "onActivityCreated() called")
        super.onActivityCreated(savedInstanceState)
    }
    override fun onStart(){
        Log.d(LOG_TAG, "onStart() called")
        super.onStart()

    }
    override fun onResume(){
        Log.d(LOG_TAG, "onResume() called")
        super.onResume()
    }
    override fun onPause(){
        Log.d(LOG_TAG, "onPause() called")
        super.onPause()
    }
    override fun onStop(){
        Log.d(LOG_TAG, "onStop() called")
        super.onStop()
    }
    override fun onDestroyView(){
        Log.d(LOG_TAG, "onDestroyView() called")
        super.onDestroyView()
    }
    override fun onDestroy(){
        Log.d(LOG_TAG, "onDestroy() called")
        super.onDestroy()
        _binding = null
    }
    override fun onDetach(){
        Log.d(LOG_TAG, "onDetach() called")
        super.onDetach()
        _callbacks = null
    }


}

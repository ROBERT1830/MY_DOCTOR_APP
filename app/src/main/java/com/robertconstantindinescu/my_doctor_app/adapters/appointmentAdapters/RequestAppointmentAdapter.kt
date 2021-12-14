package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.app.Activity
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.RequestApointmentRowBinding
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.utils.CancerDiffUtil
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_cancer_records_row.view.*

class RequestAppointmentAdapter(
    private val activity: Activity,
    private val mainViewModel: MainViewModel,
    private val itemClickListener: OnItemClickListener
): RecyclerView.Adapter<RequestAppointmentAdapter.MyViewHolder>(), ActionMode.Callback {

    interface OnItemClickListener{
        fun onItemClickListener(cancerDataEntity: CancerDataEntity)
    }


    private var cancerList = emptyList<CancerDataEntity>()
    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var multiSelection = false
    private var selectedCancerRecord = arrayListOf<CancerDataEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()



    fun setData(newCancerList: List<CancerDataEntity>){
        val recipesDiffUtil = CancerDiffUtil(cancerList, newCancerList)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        cancerList = newCancerList
        diffUtilResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.itemView.rootView
        val currentRecord = cancerList[position]
        holder.bind(currentRecord)

        holder.itemView.cancerRecordsRowLayout.setOnLongClickListener {
            if (!multiSelection){
                multiSelection = true
                activity.startActionMode(this)
                itemClickListener.onItemClickListener(currentRecord)
                applySelection(holder, currentRecord)
                true
            }else{
                multiSelection = false
                false
            }
        }

        holder.itemView.cancerRecordsRowLayout.setOnClickListener {
            if (multiSelection){
                applySelection(holder, currentRecord)
                itemClickListener.onItemClickListener(currentRecord)

            }
        }

    }

    override fun getItemCount(): Int {
        return cancerList.size
    }

    class MyViewHolder(private val binding: RequestApointmentRowBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(cancerDataEntity: CancerDataEntity){
            binding.cancerEntity = cancerDataEntity
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestApointmentRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }



    }

    private fun applySelection(
        holder: MyViewHolder,
        currentRecord: CancerDataEntity
    ) {
        if (selectedCancerRecord.contains(currentRecord)) {
            selectedCancerRecord.remove(currentRecord)
            changeCancerRecordStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        }else{
            selectedCancerRecord.add(currentRecord)
            changeCancerRecordStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()

        }
        // TODO: 16/11/21
    }
    private fun applyActionModeTitle() {
        when(selectedCancerRecord.size){
            0 -> {
                mActionMode.finish()
            }
            1 -> { //if we select only one recipe change the title
                mActionMode.title = "${selectedCancerRecord.size} item selected"


            }
            //when we have more than one recipe selected
            else -> {
                mActionMode.title = "${selectedCancerRecord.size} items selected"
            }

        }
    }
    private fun changeCancerRecordStyle(
        holder: MyViewHolder,
        cardBackgroundColor: Int,
        strokeColor: Int
    ) {
        holder.itemView.cancerRecordsRowLayout.setBackgroundColor(
            ContextCompat.getColor(activity, cardBackgroundColor)
        )
        holder.itemView.cancerRecords_row_cardView.strokeColor =
            ContextCompat.getColor(activity, strokeColor)

    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.cancer_records_contextual_menu, menu)
        mActionMode = actionMode!! //set the global variable mActionMode to the actionMode of here. so from this function we get the actionmode and store taht in a global variable. Becasue we wil need taht to set the title of contextual action mode
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if(menu?.itemId == R.id.delete_cancer_record_menu){

            selectedCancerRecord.forEach {
                mainViewModel.deleteCancerRecord(it)
            }
            showSnackBar("${selectedCancerRecord.size} Recipe/s removed.")
            multiSelection = false
            selectedCancerRecord.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeCancerRecordStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        /**22*/
        multiSelection = false
        selectedCancerRecord.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }
    private fun applyStatusBarColor(color:Int){
        activity.window.statusBarColor =
            ContextCompat.getColor(activity, color)

    }
    private fun showSnackBar(message: String){
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).setAction("OK"){}.show()
    }
}
package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.*
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments.AppointmentsRequestsFragment
import com.robertconstantindinescu.my_doctor_app.ui.loginSignUp.LoginActivity
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.FROM_SAVE_DOCTOR_NOTES
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_patient.*
@AndroidEntryPoint
class DoctorActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navigationDrawerLayoutBinding: DoctorNavigationDrawerLayoutBinding
    private lateinit var doctorBinding: ActivityDoctorBinding
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var imgHeader: CircleImageView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationDrawerLayoutBinding = DoctorNavigationDrawerLayoutBinding.inflate(layoutInflater)
        setContentView(navigationDrawerLayoutBinding.root)

        //receive data from DoctorNotesFragment

        Log.d("onCreate", "CALLED")
        val intent = intent
        val fromDoctorSavedNotes: Boolean = intent.getBooleanExtra(FROM_SAVE_DOCTOR_NOTES, false)
        Log.d("fromDoctorSavedNotes", fromDoctorSavedNotes.toString())


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putBoolean(FROM_SAVE_DOCTOR_NOTES, fromDoctorSavedNotes)
        val appointmentsRequestsFragment = AppointmentsRequestsFragment()
        appointmentsRequestsFragment.arguments = bundle
        fragmentTransaction.replace(R.id.flFragment, appointmentsRequestsFragment).commit()





        doctorBinding = navigationDrawerLayoutBinding.doctorActivity
        toolbarLayoutBinding = doctorBinding.toolbarMain

        setSupportActionBar(toolbarLayoutBinding.toolbar)
        firebaseAuth = Firebase.auth
        val toogle = ActionBarDrawerToggle(
            this,
            navigationDrawerLayoutBinding.navDrawer,
            toolbarLayoutBinding.toolbar,
            R.string.open_navigation_drawer,
            R.string.close_navigation_drawer
        )
        navigationDrawerLayoutBinding.navDrawer.addDrawerListener(toogle)
        navigationDrawerLayoutBinding.navigationView.getMenu().findItem(R.id.btnLogOut).setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            true
        }
        toogle.syncState()
        val navController = Navigation.findNavController(this, R.id.doctorNavHostFragment)
        NavigationUI.setupWithNavController(
            navigationDrawerLayoutBinding.navigationView,
            navController,

            )

        val headerLayout = navigationDrawerLayoutBinding.navigationView.getHeaderView(0)
        //point to the differnt views from the header.
        imgHeader = headerLayout.findViewById(R.id.imgHeader)
        txtName = headerLayout.findViewById(R.id.txtHeaderName)
        txtEmail = headerLayout.findViewById(R.id.txtHeaderEmail)
        //get the user data from firebase
        getUserData()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.requestedAppointmentsFragment,
                R.id.aceptedAppointmentsFragment,
                R.id.contactToPatientFragment,


            )
        )
        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }


    private fun getUserData() {

        val database = Firebase.database.getReference("Users").child(firebaseAuth.uid!!)
        //event listener to listen for the data of an given database instance at a specific moment
        database.addValueEventListener(object: ValueEventListener {
            //used to read the database static instance
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //create a patientModel from the firebase snapshoot.
                    val patientModel = snapshot.getValue(PatientModel::class.java)
                    //set the data from the navigationView header views
                    Glide.with(this@DoctorActivity).load(patientModel?.image).into(imgHeader)
                    txtEmail.text = patientModel?.email
                    txtName.text = patientModel?.name
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        } )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ||  super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

        /**
         * para cerrar y abrir el drawer. si esta abierto que ponga el gravity a start, es decir a la
         * izquierda del padre. si clicas en otro sitio que no sea el navDrawer, haces un onBackpress y vuelves
         * al fragment.
         */
        if (navigationDrawerLayoutBinding.navDrawer.isDrawerOpen(GravityCompat.START))
            navigationDrawerLayoutBinding.navDrawer.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}
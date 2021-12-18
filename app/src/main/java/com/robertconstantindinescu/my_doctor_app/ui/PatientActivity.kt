package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientBinding
import com.robertconstantindinescu.my_doctor_app.databinding.NavigationDrawerLayoutBinding
import com.robertconstantindinescu.my_doctor_app.databinding.ToolbarLayoutBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.ui.loginSignUp.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_patient.*
import kotlinx.android.synthetic.main.navigation_drawer_layout.view.*

@AndroidEntryPoint
class PatientActivity : AppCompatActivity() {

    //private lateinit var mBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navigationDrawerLayoutBinding: NavigationDrawerLayoutBinding
    private lateinit var patientBinding: ActivityPatientBinding
    private lateinit var toolbarLayoutBinding: ToolbarLayoutBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var imgHeader: CircleImageView
    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate the navigationDrawerLayoutBinding. This contains the activity_patient
        navigationDrawerLayoutBinding = NavigationDrawerLayoutBinding.inflate(layoutInflater)
        setContentView(navigationDrawerLayoutBinding.root)

        patientBinding = navigationDrawerLayoutBinding.patientActivity
        toolbarLayoutBinding = patientBinding.toolbarMain

//        /**Navigation view to perform click*/
//        val nav_view: NavigationView = findViewById(R.id.navigationView)
//        nav_view.setNavigationItemSelectedListener(this)
//        nav_view.bringToFront();

        setSupportActionBar(toolbarLayoutBinding.toolbar)

        firebaseAuth = Firebase.auth

        //add the hamburger button to the toolbar.
        val toogle = ActionBarDrawerToggle(
            this,
            navigationDrawerLayoutBinding.navDrawer,
            toolbarLayoutBinding.toolbar,
            R.string.open_navigation_drawer,
            R.string.close_navigation_drawer
        )

        //drawer listener to capture the clics inside it


        navigationDrawerLayoutBinding.navDrawer.addDrawerListener(toogle)
        navigationDrawerLayoutBinding.navigationView.getMenu().findItem(R.id.btnLogOut).setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@PatientActivity, LoginActivity::class.java)
                startActivity(intent)
            true
        }



        /*will synchronise the icon’s state and display the hamburger icon or back arrow depending on
        whether the drawer is closed or open. Omitting this line of code won’t change the back arrow
        to the hamburger icon when the drawer is closed.*/
        toogle.syncState()

        //get the hostFragment
        //val navController = findNavController(R.id.navHostFragment)
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        //conect the navigationView with the controller to navigate from the togle.
        NavigationUI.setupWithNavController(
            navigationDrawerLayoutBinding.navigationView,
            navController,

        )
        //get the header from navigationView, using getHeaderView(0) because the header is the
        //first element from the navigationDrawer. Inside the header we will set the user data.
        val headerLayout = navigationDrawerLayoutBinding.navigationView.getHeaderView(0)
        //point to the differnt views from the header.
        imgHeader = headerLayout.findViewById(R.id.imgHeader)
        txtName = headerLayout.findViewById(R.id.txtHeaderName)
        txtEmail = headerLayout.findViewById(R.id.txtHeaderEmail)
        //get the user data from firebase
        getUserData()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.cancerRecordsFragment,
                R.id.radiationFragment,
                R.id.mapsFragment,
                R.id.recipesFragment,
                R.id.btnSettings,
                R.id.btnSavedPlaces,
                R.id.mapsFragment,
                R.id.btnShowAvailableDoctors,
                R.id.btnShowPendingAppointments,
                R.id.btnMyAppointments
            )
        )
        //set the bottom navigation with the navController.
        bottomNavigationView.setupWithNavController(navController)
        //sincronize the titles from each fragment in the action bar.
        setupActionBarWithNavController(navController, appBarConfiguration)



        //get the listener when the log out is cliked and go to loginactivity.



        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id){
                R.id.btnSettings -> bottomNavigationView.visibility = View.GONE
                R.id.btnSavedPlaces -> bottomNavigationView.visibility = View.GONE
                R.id.btnShowAvailableDoctors -> bottomNavigationView.visibility = View.GONE
                R.id.btnShowPendingAppointments -> bottomNavigationView.visibility = View.GONE
                R.id.btnMyAppointments -> bottomNavigationView.visibility = View.GONE
                R.id.cancerRecordsFragment -> bottomNavigationView.visibility = View.VISIBLE
            }

        }

    }

    private fun getUserData() {

        val database = Firebase.database.getReference("Users").child(firebaseAuth.uid!!)
        //event listener to listen for the data of an given database instance at a specific moment
        database.addValueEventListener(object: ValueEventListener{
            //used to read the database static instance
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //create a patientModel from the firebase snapshoot.
                    val patientModel = snapshot.getValue(PatientModel::class.java)
                    //set the data from the navigationView header views
                    Glide.with(this@PatientActivity).load(patientModel?.image).into(imgHeader)
                    txtEmail.text = patientModel?.email
                    txtName.text = patientModel?.patientName
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
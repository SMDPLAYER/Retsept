package uz.smd.retsept.ui.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import uz.smd.retsept.utils.openFragment
import uz.smd.retsept.utils.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.top_nav_view.view.*
import uz.smd.retsept.utils.hideKeyboard
import uz.smd.retsept.utils.showFragment
import uz.smd.retsept.R
import uz.smd.retsept.databinding.ActivityMainBinding
import uz.smd.retsept.ui.AuthFragment
import uz.smd.retsept.ui.SearchFragment
import uz.smd.retsept.ui.SearchResultFragment

class MainActivity : AppCompatActivity() {
   lateinit var  gso:GoogleSignInOptions
     var binding: ActivityMainBinding? = null

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this)
        viewModel.errorHandle.observe(this) {
            if (it.contains("UnknownHostException"))
                toast(getString(R.string.tvNoInternet))
            else
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            Log.e("TTT", it)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initGSO()
        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, AuthFragment())
            .commit()
        viewModel.isLoading.observe(this) {
            progressBar.isGone = !it
        }
        initNavView()
    }
    fun initGSO(){
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
         viewModel.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//         account = GoogleSignIn.getLastSignedInAccount(this)
        viewModel.auth = Firebase.auth
    }


    fun handleSearchClick(){
        showFragment(SearchFragment())
    }






     fun seacrh(text:String?,saveBackstack:Boolean=true) {
        viewModel.searchId = text?:""
        viewModel.search()
        hideKeyboard()
         if (saveBackstack)
             openFragment(SearchResultFragment())
         else
             showFragment(SearchResultFragment())
    }


    private fun initNavView() {
        binding?.navView?.itemIconTintList = null
        val header = navView.getHeaderView(0)
        header.tvNameUser.text=viewModel.auth.currentUser?.displayName?:""
        Glide.with(this).load(viewModel.auth.currentUser?.photoUrl?:"").into(header.imgUser)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_book_mark -> openBookMark()
                R.id.menu_point -> openPoints()
                R.id.menu_search -> openSearchFragment()
                R.id.menuSignOut -> signOut()
            }
            drawerLayout.closeDrawer(navView)
            true
        }
    }

    fun openNavView() {
        drawerLayout.openDrawer(navView)
    }

    fun openBookMark(){
//        openFragment(BookMarkFragment())
    }

    fun openPoints(){
//        openFragment(PointsFragment())
    }

    fun openSearchFragment(){
        openFragment(SearchFragment())
    }

    private fun signOut() {
//        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        viewModel.mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }



    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(navView)) {
            this.drawerLayout.closeDrawer(navView)
        } else {
            super.onBackPressed()
        }
    }
}
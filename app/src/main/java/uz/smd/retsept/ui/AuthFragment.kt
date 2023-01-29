package uz.smd.retsept.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.fragment_auth.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import uz.smd.retsept.R
import uz.smd.retsept.ui.base.MainActivity
import uz.smd.retsept.ui.base.MainViewModel


/**
 * Created by Siddikov Mukhriddin on 9/14/21
 */
class AuthFragment : Fragment(R.layout.fragment_auth) {
    lateinit var viewModel: MainViewModel
    private val RC_SIGN_IN = 123
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initauth()
    }

    private fun initauth() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
//        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        updateUI(viewModel.auth.currentUser)
        sign_in_button.setOnClickListener {
            signIn(viewModel.mGoogleSignInClient)
        }
    }

    private fun signIn(mGoogleSignInClient: GoogleSignInClient) {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            Log.e("TTT", "Logged: ${account}")
            Log.e("TTT", "account.id: ${account.uid}")
            Log.e("TTT", "account.idToken: ${account.getIdToken(false)}")
            authed()
        }
    }
//    fun firebaseLogged(user: FirebaseUser?) {
//            Log.e("TTT", "Logged user: ${user}")
//            Log.e("TTT", "user.uid: ${user?.uid}")
//            Log.e("TTT", "user.getIdToken: ${user?.getIdToken(false)}")
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.idToken!!)
//            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("TTT", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }
    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TTT", "signInWithCredential:success")
                    val user = viewModel.auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TTT", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    fun authed() {
        (requireActivity() as MainActivity).handleSearchClick()
    }
}
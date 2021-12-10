package com.ims.firebaseauth_kotlin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

//        auth.signOut()

        btnRegister.setOnClickListener{
            registerUser()
        }

        btnLogin.setOnClickListener {
            loginUser()
        }

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile(){
        auth.currentUser.let { user ->
            val username = etUsername.text.toString()
            val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.ic_launcher_background}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user?.updateProfile(profileUpdates)?.await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity, "Successfully updated user profile", Toast.LENGTH_SHORT).show()
                    }
                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        checkLoggedInState()

    }

    private fun registerUser(){
        val email = etEmailRegister.text.toString()
        val password = etPasswordRegister.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try{
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        
                    }
                }
            }

        }
    }

    private fun loginUser(){
        val email = etEmailLogin.text.toString()
        val password = etPasswordLogin.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try{
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }

    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null){
            tvLoggedIn.text = "You're not logged in"
        }else{
            tvLoggedIn.text = "You're logged in"
            etUsername.setTag(user.displayName)
            ivProfilePicture.setImageURI(user.photoUrl)
        }
    }
}
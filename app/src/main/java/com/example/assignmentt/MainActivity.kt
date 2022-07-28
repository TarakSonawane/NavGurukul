package com.example.assignmentt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentt.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private var pressedTime: Long = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var realtime_database : FirebaseDatabase
    private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
    override fun onStart() {
        super.onStart()

        realtime_database= FirebaseDatabase.getInstance()

        firebaseAuth=FirebaseAuth.getInstance()

        binding.forgot.setOnClickListener{
            startActivity(Intent(this,forgotpassActivity::class.java))
        }



        binding.loginButton.setOnClickListener {

            var f1=false
            var f2=true

            if(binding.usernameLogin.text.toString().isEmpty()) {
                binding.usernameLogin.error="username can not be empty!"
                binding.usernameLogin.requestFocus()

            }
            else if(binding.passLogin.text.toString().isEmpty()){
                binding.passLogin.error="password can not be empty!"
                binding.passLogin.requestFocus()
            }

            else {

                val postListener = object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //Log.d("NAMESS","${dataSnapshot}")

                        if (dataSnapshot.exists()) {
                            for (jobdata in dataSnapshot.children) {

                                val data = jobdata.getValue(object :
                                    GenericTypeIndicator<HashMap<String, Any>>() {})

                                for (set in data!!.values) {
                                    val x = data.get("username")
                                    if (binding.usernameLogin.text.toString() == x.toString()) {
                                        f2=false
                                        val y = data.get("email")
                                        if ((y.toString()
                                                .isNotEmpty()) and (binding.passLogin.text.toString()
                                                .isNotEmpty())
                                        )
                                            firebaseAuth.signInWithEmailAndPassword(
                                                y.toString(),
                                                binding.passLogin.text.toString()
                                            ).addOnSuccessListener {

                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Login successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Login failed due to ${e}!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
                                        break


                                    }


                                }

                            }

                            if(f2){
                                Toast.makeText(
                                    this@MainActivity,
                                    "Username does not exist!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                        Log.d("RAVAN","${f2}")

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                }





                realtime_database.getReference().child("users").addValueEventListener(postListener)



                //Log.d("RAVAN","lol")
            }

        }



        binding.signupButton.setOnClickListener {

            startActivity(Intent(this,signupActivity::class.java))



        }

    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}
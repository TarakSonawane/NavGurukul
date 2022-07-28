package com.example.assignmentt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.assignmentt.databinding.ActivityForgotpassBinding
import com.example.assignmentt.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class forgotpassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpassBinding
    private lateinit var realtime_database : FirebaseDatabase
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        realtime_database=FirebaseDatabase.getInstance()
        binding.forLogin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.forReset.setOnClickListener {
            if(binding.forUser.text.toString().isEmpty()) {
                binding.forUser.error="username can not be empty!"
                binding.forUser.requestFocus()

            }

            firebaseAuth=FirebaseAuth.getInstance()

            val postListener = object : ValueEventListener {

                var f2=true

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Log.d("NAMESS","${dataSnapshot}")

                    if (dataSnapshot.exists()) {
                        for (jobdata in dataSnapshot.children) {

                            val data = jobdata.getValue(object :
                                GenericTypeIndicator<HashMap<String, Any>>() {})

                            for (set in data!!.values) {
                                val x = data.get("username")
                                if (binding.forUser.text.toString() == x.toString()) {
                                    f2=false
                                    val y = data.get("email")
                                    if ((y.toString().isNotEmpty()) ) {
                                        firebaseAuth.sendPasswordResetEmail(y.toString()).addOnSuccessListener {

                                            Toast.makeText(
                                                this@forgotpassActivity,
                                                "RESET EMAIL HAS BEEN SENT ON YOUR EMAIL ID!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            startActivity(Intent(baseContext,MainActivity::class.java))
                                        }
                                            .addOnFailureListener {e->e
                                                Toast.makeText(
                                                    this@forgotpassActivity,
                                                    "eroor ${e}!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                    }

                                    break


                                }


                            }

                        }

                        if(f2){
                            Toast.makeText(
                                this@forgotpassActivity,
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

        }
    }
}
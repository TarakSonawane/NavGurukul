package com.example.assignmentt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.assignmentt.databinding.ActivityMainBinding
import com.example.assignmentt.databinding.ActivitySignupBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class signupActivity : AppCompatActivity() {

    private lateinit var firebase: FirebaseAuth
    private lateinit var binding : ActivitySignupBinding
    private lateinit var realtime_database : FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onStart() {
        super.onStart()

        binding.signupButton2.setOnClickListener{
            validate()
        }

        binding.loginButton2.setOnClickListener{


            startActivity(Intent(this,MainActivity::class.java))


        }

    }

    private fun validate() {
        val username= binding.userSign
        val email = binding.emailSign
        val phone = binding.phoneSign
        val pass1 = binding.pass1Sign
        val pass2 = binding.pass2Sign
        var flag  = false


        realtime_database= FirebaseDatabase.getInstance()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d("NAMESS","${dataSnapshot}")

                if (dataSnapshot.exists()) {
                    for (jobdata in dataSnapshot.children) {

                        val data = jobdata.getValue(object :
                            GenericTypeIndicator<HashMap<String, Any>>() {})

                        for (set in data!!.values) {
                            val x=data.get("username")
                            //Log.d("NAMESS","out   ${name.toString()} and ${x.toString()}")
                            if(binding.userSign.text.toString()==x.toString()){
                                Log.d("NAMESS","LUND   ${binding.userSign.text} and ${x}")
                                flag=true

                            }

                            //Log.d("TAAA", "${x}")

                        }

                    }
                    if(flag){
                        username.error="username already exists!"
                        username.requestFocus()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        }

        realtime_database.getReference().child("users").addValueEventListener(postListener)






        if(
            username.text.toString().isEmpty()) {
            username.error="username can not be empty!"
            username.requestFocus()

        }
        else if(email.text.toString().isEmpty()){
            email.error="email can not be empty!"
            email.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error="Invalid email format!!!"
            email.requestFocus()
        }
        else if(phone.text.toString().isEmpty()){
            phone.error="phone number can not be empty!"
            phone.requestFocus()
        }
        else if((phone.text.toString().length<10) or (phone.text.toString().length>10)){
            phone.error="phone number should be of 10 digits "
            phone.requestFocus()
        }

        else if(pass1.text.toString().isEmpty()){
            pass1.error="password can not be empty!"
            pass1.requestFocus()
        }
        else if (pass1.text.toString().length<6){
            pass1.error="password length should be at least 6 characters"
            pass1.requestFocus()
        }
        else if(pass2.text.toString().isEmpty()){
            pass2.error="password can not be empty!"
            pass2.requestFocus()
        }
        else if(pass1.text.toString()!=pass2.text.toString()){
            pass2.error="password mismatched!"
            pass2.requestFocus()

        }
        else{
            register()
        }

    }

    private fun register() {
        firebase=FirebaseAuth.getInstance()
        firebase.createUserWithEmailAndPassword(binding.emailSign.text.toString(),binding.pass1Sign.text.toString()).addOnSuccessListener {

            val user:MutableMap<String,Any> = HashMap()
            user["username"]=binding.userSign.text.toString()
            user["email"]=binding.emailSign.text.toString()
            user["phone"]=binding.phoneSign.text.toString()
            realtime_database=FirebaseDatabase.getInstance()

            databaseReference= realtime_database.getReference("users").child(binding.userSign.text.toString())
            databaseReference.setValue(user)



            Toast.makeText(this,"Your account has been created successfully!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,MainActivity::class.java))


        }
            .addOnFailureListener{e->
                Toast.makeText(this,"Registration failed due to $e ", Toast.LENGTH_SHORT).show()
            }
    }
}
package com.example.adminprintit

import android.R
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminprintit.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    var product=product()
    var categoryList= arrayOf("Weeding Card","T-Shirt","Birthday Card","Visiting Card","Banner","Poster")
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    binding.root.setBackgroundColor(Color.LTGRAY)
                    binding.main.visibility= View.GONE
                    binding.spinKit.visibility= View.VISIBLE
                    val fileUri = data?.data!!

                    var arrayAdapter=
                        ArrayAdapter(this@MainActivity, R.layout.simple_list_item_1,categoryList)
                    binding.spinner.adapter=arrayAdapter

                    Firebase.storage.reference.child("Imagefoldar/${UUID.randomUUID()}").putFile(fileUri).addOnCompleteListener{
                        if(it.isSuccessful){

                            it.result.storage.downloadUrl.addOnSuccessListener {
                                product.imageurl=it.toString()
                                binding.productName.setImageURI(fileUri)
                                binding.main.visibility= View.VISIBLE
                                binding.spinKit.visibility= View.GONE
                                binding.root.setBackgroundColor(Color.WHITE)
                            }



                        }
                    }

                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Add this in your onCreate or Application class
        FirebaseApp.initializeApp(this)

        binding.productName.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                product.category=categoryList.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.addProduct.setOnClickListener{
            if(binding.name.text.toString().isEmpty()){
                binding.name.setError("Enter name")
            }else if(binding.price.text.toString().isEmpty()){
                binding.price.setError("Enter Price")
            }else if(binding.disc.text.toString().isEmpty()){
                binding.disc.setError("Enter Description")
            }else if(product.imageurl.isEmpty()){
                Toast.makeText(this,"Pick Image", Toast.LENGTH_SHORT).show()
            }else if(binding.disc.text.toString().trim().length <30){
                binding.disc.setError("Description Should be atleast 30 character")
            }else if(binding.discount.text.toString().isEmpty()){
                binding.disc.setError("Enter Discount")
            }

            else{
                product.name=binding.name.text.toString()
                product.desc=binding.disc.text.toString()
                product.price=binding.price.text.toString().toDouble()
                product.discount=binding.discount.text.toString().toInt()

                Firebase.firestore.collection("products").document(UUID.randomUUID().toString()).set(product).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this@MainActivity,"Product Added!!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@MainActivity,"Error", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }

    }
}
package com.example.languageconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val src: AutoCompleteTextView =findViewById(R.id.dropdown_menu1);
        val dest:AutoCompleteTextView=findViewById(R.id.dropdown_menu);
        val srcText:TextInputLayout=findViewById(R.id.textInputLayout);
        val transText:TextView=findViewById(R.id.transText);
        val button:Button=findViewById(R.id.button)
        val list= ArrayList<String>();

        list.add("English")
        list.add("Marathi")
        list.add("Spanish")
        list.add("Hindi")
        list.add("French")
        list.add("Urdu")
        list.add("Japanese")

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list)
        src.setAdapter(adapter)
        dest.setAdapter(adapter)

        var REQUEST_PERMISSION_CODE=1
        var langCode = 0
        var fromLangCode = 0
        var toLangCode = 0


        src.setOnItemClickListener{parent,view,position,id ->
            val selectedLang=parent.getItemAtPosition(position).toString()
            fromLangCode=getLangCode(selectedLang)
    }
        dest.setOnItemClickListener{parent,view,position,id->
            val selectedLang=parent.getItemAtPosition(position).toString()
            toLangCode=getLangCode(selectedLang)

        }
        button.setOnClickListener{
            val inputText = srcText.editText?.text.toString()
            Log.d("inptext",inputText)
            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
            } else if (src.text.isEmpty()) {
                Toast.makeText(this, "Please select source language", Toast.LENGTH_SHORT).show()
            } else if (dest.text.isEmpty()) {
                Toast.makeText(this, "Please select target language", Toast.LENGTH_SHORT).show()
            } else {
                translateText(inputText, fromLangCode, toLangCode, transText)
            }
        }

    }
    private fun translateText(inputText: String, fromLangCode: Int, toLangCode: Int, transText: TextView) {
        transText.text = "Downloading model..."

        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLangCode)
            .setTargetLanguage(toLangCode)
            .build()

        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(inputText)
                    .addOnSuccessListener { translatedText ->
                        transText.text = translatedText // Show the translated text
                    }
                    .addOnFailureListener { e ->
                        transText.text = "Translation failed: ${e.message}" // Show error
                    }
            }
            .addOnFailureListener { e ->
                transText.text = "Model download failed: ${e.message}" // Handle download failure
            }
    }


    private fun getLangCode(selectedLang: String): Int {
        var code = 0
        when (selectedLang) {
            "English" -> code = FirebaseTranslateLanguage.EN
            "Spanish" -> code = FirebaseTranslateLanguage.ES
            "Marathi" -> code = FirebaseTranslateLanguage.MR
            "Hindi"-> code = FirebaseTranslateLanguage.HI
            "French"-> code=FirebaseTranslateLanguage.FR
            "Urdu"->code=FirebaseTranslateLanguage.UR
            "Japanese"-> code=FirebaseTranslateLanguage.JA
        }
        return code
    }






}
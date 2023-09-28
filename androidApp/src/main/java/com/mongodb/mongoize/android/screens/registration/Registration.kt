@file:OptIn(ExperimentalMaterial3Api::class)

package com.mongodb.mongoize.android.screens.registration

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mongodb.mongoize.android.MyApplicationTheme
import com.mongodb.mongoize.android.R
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Registration : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Container()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Container() {

        val viewModel: RegistrationViewModel = viewModel()
        val emailState = remember { mutableStateOf("") }
        val passwordState = remember { mutableStateOf("") }
        val firstNameState = remember { mutableStateOf("") }
        val surnameState = remember { mutableStateOf("") }
        val genderState = remember { mutableStateOf("") }
        val phoneState = remember { mutableStateOf("") }
        val datePickerState = rememberDatePickerState(yearRange = IntRange(1900, 2100))
        val context = LocalContext.current

        viewModel.registrationSuccess.observe(this) {
            if (it) {
                Toast.makeText(context, "Sign up successful. Login now", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        MyApplicationTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_realm_logo),
                        contentScale = ContentScale.Fit,
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .width(200.dp)
                            .defaultMinSize(minHeight = 200.dp)
                            .align(CenterHorizontally)
                            .padding(bottom = 20.dp)
                    )

                    TextField(
                        value = surnameState.value,
                        onValueChange = {
                            surnameState.value = it
                        },
                        label = { Text(text = "Surname") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    TextField(
                        value = firstNameState.value,
                        onValueChange = {
                            firstNameState.value = it
                        },
                        label = { Text(text = "First name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    DatePicker(
                        state = datePickerState,
                        title = {
                            Text(
                                text = "Select your date of birth"
                            )
                        }
                    )

                    TextField(
                        value = genderState.value,
                        onValueChange = {
                            genderState.value = it
                        },
                        label = { Text(text = "Gender") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    TextField(
                        value = phoneState.value,
                        onValueChange = {
                            phoneState.value = it
                        },
                        label = { Text(text = "Phone no.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    TextField(
                        value = emailState.value,
                        onValueChange = {
                            emailState.value = it
                        },
                        label = { Text(text = "Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    TextField(
                        value = passwordState.value,
                        onValueChange = {
                            passwordState.value = it
                        },
                        label = { Text(text = "Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Button(modifier = Modifier
                        .padding(top = 48.dp)
                        .align(Alignment.CenterHorizontally), onClick = {
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                        if (selectedDate == null) {
                            finish()
                        }
                        viewModel.register(
                            surnameState.value,
                            firstNameState.value,
                            selectedDate!!,
                            emailState.value,
                            phoneState.value.toLong(),
                            genderState.value,
                            passwordState.value
                        )
                    }) {
                        Text(text = "Sign up")
                    }
                }
            }
        }
    }

}
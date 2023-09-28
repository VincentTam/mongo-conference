@file:OptIn(ExperimentalMaterial3Api::class)

package com.mongodb.mongoize.android.screens.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mongodb.mongoize.android.MyApplicationTheme
import com.mongodb.mongoize.android.R
import com.mongodb.mongoize.android.screens.login.LoginActivity

class ProfileScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Container()
            }
        }
    }

    @Preview
    @Composable
    fun Container() {

        val context = LocalContext.current

        val isReadOnly = remember { mutableStateOf(true) }
        val editLabel =
            if (isReadOnly.value) stringResource(id = R.string.profile_edit) else stringResource(id = R.string.profile_save)
        val profileVM: ProfileViewModel = viewModel()

        val surname = remember { mutableStateOf("") }
        val firstName = remember { mutableStateOf("") }
        val gender = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val specification = remember { mutableStateOf("") }
        val phoneNumber = remember { mutableStateOf("") }
        val isReceptionist = remember { mutableStateOf(false) }

        val onValueChange = { type: String, value: String ->
            when (type) {
                "phoneNumber" -> phoneNumber.value = value
            }
        }

        profileVM.userInfo.observeAsState().apply {
            this.value?.let {
                surname.value = it.surname
                firstName.value = it.firstName
                gender.value = it.gender
                email.value = it.email
                isReceptionist.value = it.isReceptionist
                specification.value = it.specification
                phoneNumber.value = it.phoneNumber.toString()
            }
        }

        Scaffold(topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }, colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color(0xFF3700B3), titleContentColor = Color.White
            ), actions = {
                Text(
                    text = editLabel, modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            if (!isReadOnly.value) {
                                profileVM.save(phoneNumber.value)
                            }
                            isReadOnly.value = !isReadOnly.value
                        }, color = Color.White
                )

                Text(
                    text = stringResource(id = R.string.profile_logout),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            profileVM.onLogout()
                            goToLogin(context)
                        },
                    color = Color.White
                )
            })
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(it)
            ) {
                UserImage()
                UserName(initialValue = firstName.value + " " + surname.value)
                Gender(initialValue = gender.value)
                Email(initialValue = email.value)
                PhoneNumber(
                    isReadOnly = isReadOnly.value,
                    initialValue = phoneNumber.value,
                    onValueChange = onValueChange
                )
                Role(specification = specification.value, isReceptionist = isReceptionist.value)
            }
        }
    }

    @Preview
    @Composable
    fun UserImage() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 36.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Transparent, CircleShape)
                    .align(CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        }
    }

    @Composable
    fun UserName(initialValue: String) {
        TextField(
            value = initialValue,
            onValueChange = {},
            label = { Text(text = "Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true
        )
    }

    @Composable
    fun Email(initialValue: String) {
        TextField(
            value = initialValue,
            onValueChange = {},
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true
        )
    }

    @Composable
    fun PhoneNumber(
        isReadOnly: Boolean, initialValue: String, onValueChange: (String, String) -> Unit
    ) {
        TextField(
            value = initialValue,
            onValueChange = { onValueChange("phoneNumber", it) },
            label = { Text(text = "Phone number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(enabled = !isReadOnly, onClick = {}),
            readOnly = isReadOnly,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }

    @Composable
    fun Gender(initialValue: String) {
        TextField(
            value = initialValue,
            onValueChange = {},
            label = { Text(text = "Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true
        )
    }

    @Composable
    fun Role(specification: String, isReceptionist: Boolean) {
        val role = if (specification == "") "patient"
            else if (isReceptionist) "receptionist" else "doctor"
        TextField(
            value = role,
            onValueChange = {},
            label = { Text(text = "Role") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true
        )
    }

    private fun goToLogin(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }

}
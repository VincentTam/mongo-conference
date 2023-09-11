@file:OptIn(ExperimentalMaterial3Api::class)

package com.mongodb.mongoize.android.screens.addAppointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mongodb.mongoize.android.MyApplicationTheme
import com.mongodb.mongoize.android.R
import kotlinx.datetime.Clock
import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.Instant
import org.mongodb.kbson.ObjectId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddAppointmentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                View()
            }
        }
    }

    @Preview
    @Composable
    fun View() {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.appointment_activity),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }, colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF3700B3), titleContentColor = Color.White
                )
            )
        }) {
            ContentView(topPadding = it.calculateTopPadding())
        }
    }


    @Composable
    fun ContentView(topPadding: Dp) {

        val vm = viewModel<AddAppointmentViewModel>()

        val doctor = remember { mutableStateOf<ObjectId?>(null) }
        // First you need to remember a datePickerState.
        // This state is where you get the user selection from
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
            yearRange = IntRange(2023, 2100)
        )
        val timePickerState = rememberTimePickerState()

        Column(
            modifier = Modifier.padding(
                top = topPadding,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            Doctor(initialValue = doctor.value, onValueChange = onValueChange)
            AppointmentDate(initialState = datePickerState)
            AppointmentTime(initialState = timePickerState)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    colors = ButtonDefaults.buttonColors(),
                    content = {
                        Text(
                            text = stringResource(id = R.string.appointment_save),
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    },
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                        }
                        if (selectedDate == null) {
                            finish()
                        }
                        val selectedTime =
                            LocalTime(timePickerState.hour, timePickerState.minute, 0, 0)
                        val selectedDateTime = LocalDateTime(selectedDate!!, selectedTime)
                        vm.addAppointment(doctor.value, patient.value, selectedDateTime, endDate.value)
                        finish()
                    })
            }
        }
    }

    @Composable
    fun AppointmentName(
        initialValue: String, onValueChange: (String, String) -> Unit
    ) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds()
        )
        TextField(
            value = initialValue,
            onValueChange = {
                onValueChange("appointment", it)
            },
            label = { Text(text = "Appointment name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppointmentDate(initialState: DatePickerState) {
        // Second, you simply have to add the DatePicker component to your layout.
        DatePicker(state = initialState)
        }
    }

    @Composable
    fun AppointmentTime(initialState: TimePickerState) {
        TimePicker(state = initialState)
    }
}
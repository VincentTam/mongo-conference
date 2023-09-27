@file:OptIn(ExperimentalMaterial3Api::class)

package com.mongodb.mongoize.android.screens.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mongodb.mongoize.AppointmentInfo
import com.mongodb.mongoize.android.R
import com.mongodb.mongoize.android.screens.addAppointment.AddAppointmentActivity
import com.mongodb.mongoize.android.screens.appointmentDetail.AppointmentDetailView
import com.mongodb.mongoize.android.screens.profile.ProfileScreen
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mongodb.kbson.ObjectId

class HomeScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Container()
        }
    }

    @Preview
    @Composable
    fun Container() {
        val context = LocalContext.current

        Scaffold(topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF3700B3),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        startActivity(Intent(context, ProfileScreen::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
            )
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                startActivity(Intent(context, AddAppointmentActivity::class.java))
            }) {
                Icon(Icons.Filled.Add, "")
            }
        }, floatingActionButtonPosition = FabPosition.End
        ) {
            AppointmentList(it.calculateTopPadding())
        }
    }

    @Composable
    fun AppointmentList(topPaddingValue: Dp) {
        val homeVM = viewModel<HomeViewModel>()
        val appointments = homeVM.appointments.observeAsState(emptyList()).value

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPaddingValue, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceAround

        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Live Events", fontSize = 20.sp, fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .padding(bottom = 16.dp),
                    thickness = 1.dp
                )
            }

            items(count = appointments.size) {
                EventItem(appointments[it])
            }
        }
    }

    @Composable
    fun EventItem(appointmentInfo: AppointmentInfo) {
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .clickable {
                    goToAppointmentDetails(
                        context = context,
                        appointmentName = appointmentInfo.doctor,
                        appointmentId = appointmentInfo._id
                    )
                },
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically

            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = "${appointmentInfo.doctor}, ${appointmentInfo.notes}",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    val timestamp = appointmentInfo.time!!.epochSeconds // This is a Unix timestamp
                    val instant = Instant.fromEpochSeconds(timestamp)
                    val timeZone = TimeZone.currentSystemDefault()
                    val localDateTime = instant.toLocalDateTime(timeZone)

                    Text(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = localDateTime.toString(),
                        maxLines = 2
                    )
                }

                /*   Card(
                       modifier = Modifier
                           .fillMaxHeight()
                           .padding(8.dp),
                       shape = RoundedCornerShape(4.dp),
                       elevation = CardDefaults.outlinedCardElevation(),
                       border = BorderStroke(1.dp, Color.Black)
                   ) {
                       Text(
                           text = "",
                           modifier = Modifier
                               .padding(4.dp)
                               .padding(horizontal = 8.dp)

                       )
                   }*/
            }
        }
    }


    private fun goToAppointmentDetails(
        context: Context,
        appointmentId: ObjectId,
        appointmentName: String
    ) {
        val intent = Intent(context, AppointmentDetailView::class.java)
        intent.putExtra("name", appointmentName)
        intent.putExtra("id", appointmentId.toString())
        context.startActivity(intent)
    }

}
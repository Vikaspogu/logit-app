package com.vikaspogu.logit.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Summary
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.PieChart
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.util.Constants

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavHostController, modifier: Modifier, viewModel: SummaryViewModel
) {
    val summaryUiState by viewModel.summaryUiState.collectAsState()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                TopBar(false, navController, NavigationDestinations.Entries)
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            navController.navigate(
                "addEdit/{action}/0".replace(
                    oldValue = "{action}", newValue = Constants.ADD
                )
            )
        }, containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add),
            )
        }
    }, bottomBar = {
        BottomBar(navController = navController)
    }) { innerPadding ->
        if (summaryUiState.summaryList.isNotEmpty()) {
            SummaryList(
                summaryList = summaryUiState.summaryList,
                contentPadding = innerPadding,
                modifier = modifier.fillMaxSize(),
                navController
            )
        } else {
            EmptySummary(modifier = modifier)
        }

    }
}

@Composable
fun SummaryList(
    summaryList: List<Summary>,
    contentPadding: PaddingValues,
    modifier: Modifier,
    navController: NavHostController
) {
    LazyColumn(
        modifier = modifier, contentPadding = contentPadding
    ) {
        item {
            SummaryDetails(summaryList, modifier, navController)
        }
    }
}

@Composable
fun EntriesCircularBar(summaryList: List<Summary>) {
    val myMap = mutableMapOf<String, Int>()
    for (summary in summaryList) {
        myMap[summary.type] = summary.total
    }
    PieChart(
        myMap
    )
}

@Composable
fun EmptySummary(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You don't have any summaries",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.inverseSurface,
        )
        Text(
            text = "Click the + button to add",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.inverseSurface,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetails(
    summaryList: List<Summary>, modifier: Modifier, navController: NavHostController
) {
    var showPieView by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.summary),
            style = MaterialTheme.typography.headlineLarge
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 10.dp),
        ) {
            SegmentedButton(selected = !showPieView, onClick = {
                showPieView = !showPieView
            }, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp), label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_view_list),
                        contentDescription = stringResource(R.string.linear_layout),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(id = R.string.linear_layout),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                }
            })
            SegmentedButton(selected = showPieView, onClick = {
                showPieView = !showPieView
            }, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp), label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_donut),
                        contentDescription = stringResource(R.string.pie_layout),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(id = R.string.pie_layout),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            })
        }
    }
    if (!showPieView) {
        for (summary in summaryList) {
            SummaryCard(
                summary, modifier, navController
            )

        }
        Spacer(Modifier.height(16.dp))
    }
    AnimatedVisibility(visible = showPieView) {
        EntriesCircularBar(summaryList)
    }

}

@Composable
fun SummaryCard(summary: Summary, modifier: Modifier, navController: NavHostController) {
    Surface(shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            .clickable {
                navController.navigate(
                    "summaryDetails/{typeId}".replace(
                        oldValue = "{typeId}", newValue = summary.typeId.toString()
                    )
                )
            }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_patient_list),
                contentDescription = stringResource(R.string.patient_list),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = summary.type,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.weight(1f),
                color = MaterialTheme.colorScheme.inverseSurface,
            )
            Text(
                text = summary.total.toString(), style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.inverseSurface,
            )
        }
    }
}



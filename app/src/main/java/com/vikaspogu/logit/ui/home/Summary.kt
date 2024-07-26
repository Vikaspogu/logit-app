package com.vikaspogu.logit.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Summary
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
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
                    oldValue = "{action}",
                    newValue = Constants.ADD
                )
            )
        }, containerColor = MaterialTheme.colorScheme.tertiaryContainer) {
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
                modifier = modifier
                    .fillMaxSize(),
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
fun EmptySummary(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "You don't have any summaries", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Click the + button to add", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun SummaryDetails(summaryList: List<Summary>, modifier: Modifier, navController: NavHostController) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.summary),
            style = MaterialTheme.typography.headlineMedium
        )
        for (summary in summaryList) {
            SummaryCard(
                summary, modifier, navController
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SummaryCard(summary: Summary, modifier: Modifier, navController: NavHostController) {
    Card(
        modifier = modifier.padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
        onClick = {
            navController.navigate("summaryDetails/{typeId}".replace(oldValue = "{typeId}", newValue = summary.typeId.toString()))
        }
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement  =  Arrangement.SpaceBetween
        ) {
            Text(
                text = summary.type,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.weight(1f)
            )
            Text(
                text = summary.total.toString(), style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}



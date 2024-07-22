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
        FloatingActionButton(onClick = { navController.navigate(NavigationDestinations.Add.name) }) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "Add"
            )
        }
    }, bottomBar = {
        BottomBar(navController = navController)
    }) { innerPadding ->
        SummaryList(
            summaryList = summaryUiState.summaryList,
            contentPadding = innerPadding,
            modifier = modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun SummaryList(summaryList: List<Summary>, contentPadding: PaddingValues, modifier: Modifier) {
    LazyColumn(
        modifier = modifier, contentPadding = contentPadding
    ) {
        item { SummaryDetails(summaryList, modifier) }
    }
}

@Composable
fun SummaryDetails(summaryList: List<Summary>, modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.summary),
            style = MaterialTheme.typography.displayLarge
        )
        for (summary in summaryList) {
            SummaryCard(
                summary, modifier
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SummaryCard(summary: Summary, modifier: Modifier) {
    Card(
        modifier = modifier.padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
        ) {
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = summary.type,
                    style = MaterialTheme.typography.displayMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = summary.total.toString(), style = MaterialTheme.typography.displayMedium
                )
            }
        }
    }
}

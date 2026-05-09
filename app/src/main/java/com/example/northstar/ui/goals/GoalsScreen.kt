package com.example.northstar.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.ui.theme.NeutralLightGrey
import com.example.northstar.ui.theme.NeutralCharcoal
import com.example.northstar.ui.theme.PrimaryBlue

@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goals by viewModel.goals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NeutralLightGrey,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Section with Back Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // Centered for the back button
                ) {
                    // Back Button added here
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = NeutralCharcoal
                        )
                    }

                    Text(
                        text = "Savings Goals",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralCharcoal,
                        modifier = Modifier.weight(1f) // Push count to the end
                    )

                    Text(
                        text = "${goals.size} goals",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Main Content Logic (Remains unchanged)
            if (isLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = PrimaryBlue
                    )
                }
            } else if (goals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎯", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No goals yet!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeutralCharcoal
                            )
                            Text(
                                text = "Tap + to add a savings goal",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(goals) { goal ->
                    GoalCard(goal = goal, viewModel = viewModel)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount, date ->
                viewModel.addGoal(name, amount, date)
                showAddDialog = false
            }
        )
    }
}
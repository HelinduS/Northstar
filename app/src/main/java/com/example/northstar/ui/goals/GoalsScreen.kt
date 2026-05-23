package com.example.northstar.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.northstar.ui.theme.*
import java.text.NumberFormat
import java.util.*

@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goals     by viewModel.goals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog     by remember { mutableStateOf(false) }
    var showAllTemplates  by remember { mutableStateOf(false) }
    var prefilledTemplate by remember { mutableStateOf<GoalTemplate?>(null) }

    val currencyFormat = remember { NumberFormat.getInstance(Locale.getDefault()) }

    val totalSaved     = remember(goals) { goals.sumOf { it.savedAmount } }
    val totalTarget    = remember(goals) { goals.sumOf { it.targetAmount } }
    val completedCount = remember(goals) { goals.count { it.savedAmount >= it.targetAmount } }

    val totalRemaining = remember(goals) {
        goals.filter { it.savedAmount < it.targetAmount }.sumOf { it.targetAmount - it.savedAmount }
    }
    val nearestGoal = remember(goals) {
        goals.filter { it.savedAmount < it.targetAmount && it.targetAmount > 0 }
            .maxByOrNull { it.savedAmount.toDouble() / it.targetAmount.toDouble() }
    }
    val nearestGoalPct = remember(goals) {
        nearestGoal?.let {
            (it.savedAmount.toDouble() / it.targetAmount.toDouble() * 100).toInt().coerceIn(0, 99)
        } ?: 0
    }
    val overallProgressPct = remember(goals) {
        if (totalTarget > 0)
            (totalSaved.toDouble() / totalTarget.toDouble() * 100).toInt().coerceIn(0, 100)
        else 0
    }
    val onTrackCount = remember(goals) {
        goals.count { it.savedAmount < it.targetAmount && it.targetAmount > 0 && it.savedAmount > 0 }
    }

    val visibleTemplates = if (showAllTemplates) allGoalTemplates else allGoalTemplates.take(5)

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            item {
                GoalsHeader(
                    goals          = goals,
                    totalSaved     = totalSaved,
                    onTrackCount   = onTrackCount,
                    nearestGoal    = nearestGoal,
                    nearestGoalPct = nearestGoalPct,
                    currencyFormat = currencyFormat,
                    onBack         = { navController.popBackStack() }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        // ── Smart Insights ──
                        Column(
                            modifier = Modifier.padding(
                                top = 12.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 0.dp
                            )
                        ) {
                            Text(
                                "Smart Insights",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = InterFontFamily,
                                letterSpacing = (-0.2).sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        GoalInsightsRow(
                            totalRemaining     = totalRemaining,
                            overallProgressPct = overallProgressPct,
                            completedCount     = completedCount
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        } else if (goals.isEmpty()) {
                            GoalsEmptyState(
                                onCreateClick = {
                                    prefilledTemplate = null
                                    showAddDialog = true
                                }
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "My Goals",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = InterFontFamily,
                                    letterSpacing = (-0.2).sp
                                )
                                TextButton(
                                    onClick = {
                                        prefilledTemplate = null
                                        showAddDialog = true
                                    }
                                ) {
                                    Text(
                                        "+ Add goal",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = InterFontFamily
                                    )
                                }
                            }
                            goals.forEach { goal ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 13.dp)
                                ) {
                                    GoalCard(goal = goal, viewModel = viewModel)
                                }
                            }
                        }

                        GoalTemplatesSection(
                            visibleTemplates = visibleTemplates,
                            showAllTemplates = showAllTemplates,
                            onToggleSeeAll   = { showAllTemplates = !showAllTemplates },
                            onTemplateClick  = { template ->
                                prefilledTemplate = template
                                showAddDialog = true
                            }
                        )
                    }
                }
            }

            item {
                val navBarHeight = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
                Spacer(Modifier.height(navBarHeight + 80.dp))
            }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(
            prefillName   = prefilledTemplate?.label,
            prefillAmount = prefilledTemplate?.targetAmount,
            onDismiss     = { showAddDialog = false; prefilledTemplate = null },
            onConfirm     = { name, amount, date ->
                viewModel.addGoal(name, amount, date)
                showAddDialog = false
                prefilledTemplate = null
            }
        )
    }
}

@Composable
private fun GoalsHeader(
    goals: List<com.example.northstar.domain.model.Goal>,
    totalSaved: Long,
    onTrackCount: Int,
    nearestGoal: com.example.northstar.domain.model.Goal?,
    nearestGoalPct: Int,
    currencyFormat: java.text.NumberFormat,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(GreenDeep)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (goals.isNotEmpty()) {
                    GoalStatusPill(
                        icon      = Icons.Outlined.TrendingUp,
                        text      = "$onTrackCount of ${goals.size} on track",
                        bg        = GreenAccent.copy(alpha = 0.20f),
                        border    = GreenAccent.copy(alpha = 0.30f),
                        tint      = GreenAccent,
                        textColor = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "Savings Goals",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp,
                fontFamily = InterFontFamily
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                "Track and grow your wealth",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.50f),
                fontFamily = InterFontFamily
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "TOTAL SAVINGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.45f),
                            letterSpacing = 1.sp,
                            fontFamily = InterFontFamily
                        )
                        if (goals.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "${goals.size} active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.70f),
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "LKR ${currencyFormat.format(totalSaved / 100)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp,
                        fontFamily = InterFontFamily
                    )

                    if (nearestGoal != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 0.5.dp,
                            color = Color.White.copy(alpha = 0.10f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GreenAccent.copy(alpha = 0.20f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.TrendingUp,
                                        contentDescription = null,
                                        tint = GreenAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Closest to complete",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.40f),
                                        fontFamily = InterFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        nearestGoal.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontFamily = InterFontFamily
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreenAccent.copy(alpha = 0.20f))
                                    .border(
                                        0.5.dp,
                                        GreenAccent.copy(alpha = 0.30f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    "$nearestGoalPct%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GreenAccent,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (goals.isNotEmpty())
                                "Keep saving to see progress here"
                            else
                                "Add your first goal to start tracking",
                            fontSize = if (goals.isNotEmpty()) 11.sp else 12.sp,
                            color = Color.White.copy(alpha = 0.35f),
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsEmptyState(onCreateClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.surface)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            "My Goals",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = cs.onSurface,
            fontFamily = InterFontFamily,
            letterSpacing = (-0.2).sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cs.surface)
                .border(0.5.dp, cs.outline, RoundedCornerShape(20.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(cs.primary.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        .border(1.dp, cs.primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = cs.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No savings goals yet",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cs.onSurface,
                    textAlign = TextAlign.Center,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Start building smarter savings habits\nby creating your first goal today.",
                    fontSize = 13.sp,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCreateClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(listOf(GreenDeep, GreenAccent))
                            )
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Create first goal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalTemplatesSection(
    visibleTemplates: List<GoalTemplate>,
    showAllTemplates: Boolean,
    onToggleSeeAll: () -> Unit,
    onTemplateClick: (GoalTemplate) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .background(cs.surface)
            .padding(top = 9.dp, bottom = 9.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Goal Templates",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = cs.onSurface,
                fontFamily = InterFontFamily,
                letterSpacing = (-0.2).sp
            )
            TextButton(onClick = onToggleSeeAll) {
                Text(
                    if (showAllTemplates) "See less" else "See all",
                    fontSize = 13.sp,
                    color = cs.primary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFontFamily
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (showAllTemplates) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                visibleTemplates.chunked(3).forEach { rowTemplates ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowTemplates.forEach { template ->
                            GoalTemplateCard(
                                template = template,
                                modifier = Modifier.weight(1f),
                                onClick  = { onTemplateClick(template) }
                            )
                        }
                        repeat(3 - rowTemplates.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleTemplates) { template ->
                    GoalTemplateCard(
                        template = template,
                        onClick  = { onTemplateClick(template) }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalStatusPill(
    icon: ImageVector,
    text: String,
    bg: Color,
    border: Color,
    tint: Color,
    textColor: Color = tint
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(0.5.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontFamily = InterFontFamily
            )
        }
    }
}
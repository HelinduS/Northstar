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
import com.example.northstar.ui.theme.Navy900
import java.text.NumberFormat
import java.util.*

// ── Design tokens ─────────────────────────────────────────────────────────────
private val NavyLight  = Color(0xFF161B27)
private val NavyDeep   = Color(0xFF1A2035)
private val Blue       = Color(0xFF2979FF)
private val Indigo     = Color(0xFF3B5BDB)
private val Purple     = Color(0xFF7048E8)
private val BodyBg     = Color(0xFFF2F4F7)
private val CardBorder = Color(0xFFE1E4E8)
private val TextPri    = Color(0xFF0D1117)
private val TextMut    = Color(0xFF8E8E93)
private val Amber      = Color(0xFFF9A825)
private val AmberBg    = Color(0xFFFFF8E1)
private val RedAccent  = Color(0xFFE53935)
private val RedBg      = Color(0xFFFFEBEE)
private val BlueBg     = Color(0xFFE3F2FD)

// ── GoalsScreen ───────────────────────────────────────────────────────────────

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

    // ── Derived stats ─────────────────────────────────────────────────────
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
        if (totalTarget > 0) (totalSaved.toDouble() / totalTarget.toDouble() * 100).toInt().coerceIn(0, 100) else 0
    }
    val onTrackCount = remember(goals) {
        goals.count { it.savedAmount < it.targetAmount && it.targetAmount > 0 && it.savedAmount > 0 }
    }

    // ── Insights list ─────────────────────────────────────────────────────
    val insights = listOf(
        InsightCard(Icons.Outlined.Savings,     if (goals.isEmpty()) "LKR 0" else "LKR ${currencyFormat.format(totalRemaining / 100)}", "Remaining",  RedBg,   RedAccent),
        InsightCard(Icons.Outlined.TrendingUp,  "$overallProgressPct%",                                                                 "Overall",    BlueBg,  Blue),
        InsightCard(Icons.Outlined.EmojiEvents, "$completedCount",                                                                      "Completed",  AmberBg, Amber)
    )

    val visibleTemplates = if (showAllTemplates) allGoalTemplates else allGoalTemplates.take(5)

    // ── Root ──────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(BodyBg)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── Header ───────────────────────────────────────────────────
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

            // ── Smart Insights ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .background(BodyBg)
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text("Smart Insights", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPri)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        insights.forEach { insight ->
                            InsightChip(insight = insight, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Goals list or empty state ─────────────────────────────────
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue)
                    }
                }
            } else if (goals.isEmpty()) {
                item { GoalsEmptyState(onCreateClick = { prefilledTemplate = null; showAddDialog = true }) }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(BodyBg).padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("My Goals", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPri)
                        TextButton(onClick = { prefilledTemplate = null; showAddDialog = true }) {
                            Text("+ Add goal", fontSize = 13.sp, color = Blue, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                items(goals) { goal ->
                    Box(
                        modifier = Modifier.fillMaxWidth().background(BodyBg)
                            .padding(horizontal = 16.dp).padding(bottom = 13.dp)
                    ) {
                        GoalCard(goal = goal, viewModel = viewModel)
                    }
                }
            }

            // ── Goal Templates ───────────────────────────────────────────
            item {
                GoalTemplatesSection(
                    visibleTemplates  = visibleTemplates,
                    showAllTemplates  = showAllTemplates,
                    onToggleSeeAll    = { showAllTemplates = !showAllTemplates },
                    onTemplateClick   = { template -> prefilledTemplate = template; showAddDialog = true }
                )
            }

            // Bottom spacer
            item {
                val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
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
                showAddDialog = false; prefilledTemplate = null
            }
        )
    }
}

// ── GoalsHeader ───────────────────────────────────────────────────────────────

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
            .background(Brush.verticalGradient(colors = listOf(Navy900, NavyLight, NavyDeep)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp)
        ) {
            // Nav row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                if (goals.isNotEmpty()) {
                    GoalStatusPill(
                        icon      = Icons.Outlined.TrendingUp,
                        text      = "$onTrackCount of ${goals.size} on track",
                        bg        = Blue.copy(alpha = 0.20f),
                        border    = Blue.copy(alpha = 0.30f),
                        tint      = Blue,
                        textColor = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("Savings Goals", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)
            Spacer(modifier = Modifier.height(3.dp))
            Text("Track and grow your wealth", fontSize = 13.sp, color = Color.White.copy(alpha = 0.50f))

            Spacer(modifier = Modifier.height(14.dp))

            // Balance card
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
                        Text("TOTAL SAVINGS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.45f), letterSpacing = 1.sp)
                        if (goals.isNotEmpty()) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.10f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text("${goals.size} active", fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.70f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("LKR ${currencyFormat.format(totalSaved / 100)}", fontSize = 32.sp,
                        fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.5).sp)

                    if (nearestGoal != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp),
                            thickness = 0.5.dp, color = Color.White.copy(alpha = 0.10f))
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                                        .background(Blue.copy(alpha = 0.20f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = Blue, modifier = Modifier.size(15.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Closest to complete", fontSize = 10.sp, color = Color.White.copy(alpha = 0.40f))
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(nearestGoal.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                    .background(Blue.copy(alpha = 0.20f))
                                    .border(0.5.dp, Blue.copy(alpha = 0.30f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("$nearestGoalPct%", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Blue)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            if (goals.isNotEmpty()) "Keep saving to see progress here" else "Add your first goal to start tracking",
                            fontSize = if (goals.isNotEmpty()) 11.sp else 12.sp,
                            color = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }
}

// ── GoalsEmptyState ───────────────────────────────────────────────────────────

@Composable
private fun GoalsEmptyState(onCreateClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(BodyBg).padding(16.dp)) {
        Spacer(modifier = Modifier.height(9.dp))
        Text("My Goals", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPri)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                .background(Color.White).border(0.5.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Indigo.copy(alpha = 0.15f), Color.Transparent)))
                        .border(1.dp, Indigo.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Flag, contentDescription = null, tint = Indigo.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("No savings goals yet", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPri, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Start building smarter savings habits\nby creating your first goal today.",
                    fontSize = 13.sp, color = TextMut, textAlign = TextAlign.Center, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCreateClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(Blue, Purple)))
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create first goal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ── GoalTemplatesSection ──────────────────────────────────────────────────────

@Composable
private fun GoalTemplatesSection(
    visibleTemplates: List<GoalTemplate>,
    showAllTemplates: Boolean,
    onToggleSeeAll: () -> Unit,
    onTemplateClick: (GoalTemplate) -> Unit
) {
    Column(modifier = Modifier.background(BodyBg).padding(top = 9.dp, bottom = 9.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Goal Templates", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = TextPri)
            TextButton(onClick = onToggleSeeAll) {
                Text(if (showAllTemplates) "See less" else "See all",
                    fontSize = 13.sp, color = Blue, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (showAllTemplates) {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                visibleTemplates.chunked(3).forEach { rowTemplates ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        rowTemplates.forEach { template ->
                            GoalTemplateCard(template = template, modifier = Modifier.weight(1f), onClick = { onTemplateClick(template) })
                        }
                        repeat(3 - rowTemplates.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        } else {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visibleTemplates) { template ->
                    GoalTemplateCard(template = template, onClick = { onTemplateClick(template) })
                }
            }
        }
    }
}

// ── GoalStatusPill ────────────────────────────────────────────────────────────

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
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}
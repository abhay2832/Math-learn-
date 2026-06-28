package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.MathLearnViewModel
import com.example.viewmodel.Screen
import java.util.Locale

// --- Shared Utility: Header ---
@Composable
fun FeatureHeader(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions()
        }
    }
}

// --- Multiplication Tables Screen ---
@Composable
fun TablesScreen(viewModel: MathLearnViewModel) {
    val context = LocalContext.current
    val favorites by viewModel.favoritesList.collectAsState()

    var tableNumInput by remember { mutableStateOf("12") }
    val activeTable = tableNumInput.toIntOrNull() ?: 2

    var multiplierSearchQuery by remember { mutableStateOf("") }
    var isFullscreen by remember { mutableStateOf(false) }

    val multiples = (1..50).toList()
    val filteredMultiples = multiples.filter { m ->
        multiplierSearchQuery.isEmpty() || 
        m.toString().contains(multiplierSearchQuery) ||
        (activeTable * m).toString().contains(multiplierSearchQuery)
    }

    val itemKey = "table_$activeTable"
    val isFav = favorites.any { it.type == "TABLE" && it.itemKey == itemKey }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (!isFullscreen) {
            FeatureHeader(
                title = "Multiplication Tables",
                onBack = { viewModel.navigateTo(Screen.Dashboard) }
            ) {
                // Favorite Toggle
                IconButton(
                    onClick = { viewModel.toggleFavorite("TABLE", itemKey) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) AmberWarning else MaterialTheme.colorScheme.onBackground
                    )
                }

                // Copy Table
                IconButton(
                    onClick = {
                        val text = (1..50).joinToString("\n") { "$activeTable × $it = ${activeTable * it}" }
                        copyToClipboard(context, text)
                        Toast.makeText(context, "Table $activeTable copied to Clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Table")
                }

                // Share / Print
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Opening Print/Share Spooler for Table $activeTable... Done!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share/Print")
                }
            }
        }

        // Action controls bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Table selection field
            OutlinedTextField(
                value = tableNumInput,
                onValueChange = { newValue ->
                    val clean = newValue.filter { it.isDigit() }
                    if (clean.isEmpty()) {
                        tableNumInput = ""
                    } else {
                        val parsed = clean.toInt()
                        if (parsed in 2..1000) {
                            tableNumInput = clean
                        }
                    }
                },
                label = { Text("Select Table (2-1000)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary
                )
            )

            // Previous button
            IconButton(
                onClick = {
                    if (activeTable > 2) {
                        tableNumInput = (activeTable - 1).toString()
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Table")
            }

            // Next button
            IconButton(
                onClick = {
                    if (activeTable < 1000) {
                        tableNumInput = (activeTable + 1).toString()
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Table")
            }

            // Fullscreen toggle
            IconButton(
                onClick = { isFullscreen = !isFullscreen },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Fullscreen"
                )
            }
        }

        // Multiplier search box
        OutlinedTextField(
            value = multiplierSearchQuery,
            onValueChange = { multiplierSearchQuery = it },
            placeholder = { Text("Search by multiplication factor or product (e.g. 12 or 144)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IndigoPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Multiplication lists
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredMultiples) { multiplier ->
                val result = activeTable * multiplier
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$activeTable  ×  $multiplier",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "=   $result",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// --- Squares 1-1000 Screen ---
@Composable
fun SquaresScreen(viewModel: MathLearnViewModel) {
    val context = LocalContext.current
    val favorites by viewModel.favoritesList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val itemsList = (1..1000).toList()
    val filteredItems = itemsList.filter { n ->
        searchQuery.isEmpty() ||
        n.toString() == searchQuery ||
        (n * n).toString().contains(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Squares (x²)",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by number or squared value (e.g. 25)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems) { n ->
                val result = n * n
                val itemKey = "square_$n"
                val isFav = favorites.any { it.type == "SQUARE" && it.itemKey == itemKey }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            copyToClipboard(context, "$n² = $result")
                            Toast.makeText(context, "Copied: $n² = $result", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$n²",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Icon(
                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        viewModel.toggleFavorite("SQUARE", itemKey)
                                    }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "$result",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// --- Cubes 1-1000 Screen ---
@Composable
fun CubesScreen(viewModel: MathLearnViewModel) {
    val context = LocalContext.current
    val favorites by viewModel.favoritesList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val itemsList = (1..1000).toList()
    val filteredItems = itemsList.filter { n ->
        searchQuery.isEmpty() ||
        n.toString() == searchQuery ||
        (n.toLong() * n * n).toString().contains(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Cubes (x³)",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by number or cubed value (e.g. 5)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems) { n ->
                val result = n.toLong() * n * n
                val itemKey = "cube_$n"
                val isFav = favorites.any { it.type == "CUBE" && it.itemKey == itemKey }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            copyToClipboard(context, "$n³ = $result")
                            Toast.makeText(context, "Copied: $n³ = $result", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$n³",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Icon(
                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        viewModel.toggleFavorite("CUBE", itemKey)
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "$result",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VioletTertiary,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// --- Square Roots Screen ---
@Composable
fun RootsScreen(viewModel: MathLearnViewModel) {
    val context = LocalContext.current
    val favorites by viewModel.favoritesList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val itemsList = (1..1000).toList()
    val filteredItems = itemsList.filter { n ->
        searchQuery.isEmpty() ||
        n.toString().contains(searchQuery) ||
        String.format(Locale.US, "%.4f", kotlin.math.sqrt(n.toDouble())).contains(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FeatureHeader(
            title = "Square Roots (√x)",
            onBack = { viewModel.navigateTo(Screen.Dashboard) }
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by number or root decimal (e.g. 144)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems) { n ->
                val root = kotlin.math.sqrt(n.toDouble())
                val formattedStr = String.format(Locale.US, "%.4f", root)
                val itemKey = "root_$n"
                val isFav = favorites.any { it.type == "ROOT" && it.itemKey == itemKey }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            copyToClipboard(context, "√$n = $formattedStr")
                            Toast.makeText(context, "Copied: √$n = $formattedStr", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "√$n",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Icon(
                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) AmberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        viewModel.toggleFavorite("ROOT", itemKey)
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = formattedStr,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoseError,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// Clipboard copy helper
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("MathLearn", text)
    clipboard.setPrimaryClip(clip)
}

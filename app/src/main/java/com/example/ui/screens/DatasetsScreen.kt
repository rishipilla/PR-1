package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoicePhrase
import com.example.ui.components.GlassCard
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaError
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaSaffron
import com.example.ui.theme.PrayogaSuccess
import com.example.ui.theme.PrayogaTeal
import com.example.ui.theme.PrayogaTextPrimary
import com.example.ui.theme.PrayogaTextSecondary
import com.example.viewmodel.PrayogaViewModel

@Composable
fun DatasetsScreen(
    viewModel: PrayogaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val vocabulary by viewModel.vocabulary.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var exportedText by remember { mutableStateOf("") }
    var importInputText by remember { mutableStateOf("") }

    // Dialog state for adding phrase
    var newPhraseName by remember { mutableStateOf("") }
    var newSpokenText by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Needs") }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VOICE DATASETS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = PrayogaCyanDark
                        )
                        Text(
                            text = "Manage & Train Articulation Vocabulary",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrayogaTextSecondary
                        )
                    }

                    // Count Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE0F2FE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrayogaCyan.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${vocabulary.size} Phrases",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrayogaCyanDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Actions Banner (Add, Import, Export in text format)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 20.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Dataset Operations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrayogaTextPrimary
                        )
                        Text(
                            text = "Add voice datasets via text, or import/export structured JSON",
                            fontSize = 12.sp,
                            color = PrayogaTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddDialog = true },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("add_phrase_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Dataset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    exportedText = viewModel.exportDatasetsToJson()
                                    showExportDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Export", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    importInputText = ""
                                    showImportDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = "Import", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Import", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Vocabulary List header
            item {
                Text(
                    text = "Active Vocabulary & Voice Outputs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrayogaTextPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Vocabulary List Items
            items(vocabulary, key = { it.id }) { item ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 16.dp,
                    borderColor = if (item.isCustom) PrayogaSaffron.copy(alpha = 0.4f) else PrayogaGlassBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.phrase,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PrayogaTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (item.isCustom) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = item.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (item.isCustom) PrayogaSaffron else PrayogaTextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Output: \"${item.spokenText}\"",
                                fontSize = 13.sp,
                                color = PrayogaTextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Test Playback Button
                            IconButton(
                                onClick = { viewModel.speakText(item.spokenText) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Phrase",
                                    tint = PrayogaCyanDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Practice Articulation
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { viewModel.triggerArticulationRecognition(targetPhrase = item) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCCFBF1))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Practice Phrase",
                                    tint = PrayogaTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (item.isCustom) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteVoicePhrase(item.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = PrayogaError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // ==========================================
        // DIALOG: ADD VOICE DATASET
        // ==========================================
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text("Add Voice Dataset", fontWeight = FontWeight.Bold, color = PrayogaTextPrimary)
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Define the silent articulation phrase and natural spoken voice output text:",
                            fontSize = 12.sp,
                            color = PrayogaTextSecondary
                        )

                        OutlinedTextField(
                            value = newPhraseName,
                            onValueChange = { newPhraseName = it },
                            label = { Text("Phrase (e.g. CALL NURSE)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newSpokenText,
                            onValueChange = { newSpokenText = it },
                            label = { Text("Spoken Voice Output (e.g. Please call the nurse)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newCategory,
                            onValueChange = { newCategory = it },
                            label = { Text("Category (e.g. Emergency, Medical, Needs)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Immediate Voice Test button inside dialog
                        Button(
                            onClick = {
                                val textToSpeak = newSpokenText.ifEmpty { newPhraseName }
                                if (textToSpeak.isNotBlank()) {
                                    viewModel.speakText(textToSpeak)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2FE), contentColor = PrayogaCyanDark)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Test voice", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Voice Output Aloud", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPhraseName.isNotBlank()) {
                                viewModel.addVoicePhrase(newPhraseName, newSpokenText, newCategory)
                                newPhraseName = ""
                                newSpokenText = ""
                                showAddDialog = false
                                Toast.makeText(context, "Voice dataset added!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                    ) {
                        Text("Add to Dataset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // ==========================================
        // DIALOG: EXPORT DATASETS (JSON / TEXT FORMAT)
        // ==========================================
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Export Voice Datasets", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Formatted dataset representation (JSON/Text):",
                            fontSize = 12.sp,
                            color = PrayogaTextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = exportedText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = PrayogaTextPrimary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(exportedText))
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showExportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy to Clipboard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // ==========================================
        // DIALOG: IMPORT DATASETS (TEXT FORMAT)
        // ==========================================
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Voice Datasets", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Paste your JSON dataset array below:",
                            fontSize = 12.sp,
                            color = PrayogaTextSecondary
                        )
                        OutlinedTextField(
                            value = importInputText,
                            onValueChange = { importInputText = it },
                            placeholder = {
                                Text("""[{"phrase":"MEDICINE","spokenText":"I need my medicine","category":"Medical"}]""", fontSize = 11.sp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val success = viewModel.importDatasetsFromJson(importInputText)
                            if (success) {
                                Toast.makeText(context, "Datasets imported successfully!", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                Toast.makeText(context, "Invalid JSON format", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrayogaCyan)
                    ) {
                        Text("Import Phrases")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

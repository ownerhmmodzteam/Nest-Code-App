package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.ui.components.IosGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.CodeNestViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun GoogleLoginScreen(
    viewModel: CodeNestViewModel,
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val credentialManager = remember { CredentialManager.create(context) }
    val serverClientId = "169259639630-5bcjqkeardf0s76eehjdc2g0hh9cl09c.apps.googleusercontent.com"

    // System Back Press handling
    BackHandler {
        if (onBackClick != null) {
            onBackClick()
        } else {
            onContinueAsGuest()
        }
    }

    // Function to initiate Credential Manager or show quick account picker
    fun triggerGoogleSignIn() {
        isLoading = true
        coroutineScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context as Activity
                )

                val credential = result.credential
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleCredential.idToken
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                    val firebaseUser = authResult.user ?: throw IllegalStateException("Akun Firebase tidak tersedia.")
                    val email = firebaseUser.email ?: googleCredential.id
                    val displayName = firebaseUser.displayName ?: googleCredential.displayName ?: email.substringBefore("@")
                    val photoUrl = firebaseUser.photoUrl?.toString() ?: googleCredential.profilePictureUri?.toString() ?: ""

                    viewModel.signInWithGoogle(
                        uid = firebaseUser.uid,
                        displayName = displayName,
                        email = email,
                        photoUrl = photoUrl,
                        onComplete = {
                            Toast.makeText(context, "Selamat datang, $displayName!", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            onLoginSuccess()
                        }
                    )
                } else {
                    isLoading = false
                    
                }
            } catch (e: GetCredentialCancellationException) {
                isLoading = false
            } catch (e: GetCredentialException) {
                isLoading = false
                Toast.makeText(context, "Google Sign-In tidak dapat digunakan saat ini.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                isLoading = false
                Toast.makeText(context, e.message ?: "Google Sign-In gagal.", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        IosDarkBackground,
                        Color(0xFF090D1C),
                        IosDarkBackground
                    )
                )
            )
    ) {
        // Ambient Liquid Mesh Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(IosNeonCyan.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.25f),
                    radius = size.width * 0.7f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(IosNeonPurple.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.7f),
                    radius = size.width * 0.65f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with optional back navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(IosDarkGlassSurface)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(40.dp))
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = IosDarkGlassSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = IosNeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "iOS 28 Secure Auth",
                            style = MaterialTheme.typography.labelSmall,
                            color = IosTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Hero Emblem & Intro
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Liquid Glow Emblem
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    IosNeonCyan.copy(alpha = 0.8f),
                                    IosNeonPurple.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "CodeNest",
                        tint = IosNeonCyan,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "CodeNest",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.8).sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Platform Belajar Koding Berstandar Industri",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IosTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Feature Highlights Glass Card
                IosGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FeatureRow(
                            icon = Icons.Default.Sync,
                            iconColor = IosNeonCyan,
                            title = "Sinkronisasi Akun Otomatis",
                            subtitle = "Nama profil otomatis terhubung dengan akun Google Anda"
                        )
                        FeatureRow(
                            icon = Icons.Default.Lock,
                            iconColor = IosNeonAmber,
                            title = "Proteksi Ganti Nama 1x",
                            subtitle = "Kesempatan ubah nama akun 1 kali per akun untuk integritas sertifikat"
                        )
                        FeatureRow(
                            icon = Icons.Default.CheckCircle,
                            iconColor = IosNeonEmerald,
                            title = "Progres Cloud & XP Terverifikasi",
                            subtitle = "Penyimpanan otomatis riwayat belajar, quiz, dan project"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PRIMARY: Sign in with Google Button
                Button(
                    onClick = { triggerGoogleSignIn() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("google_login_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1F2937)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF1F2937),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Menghubungkan ke Google...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    } else {
                        GoogleLogoIcon(modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Masuk dengan Google",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        )
                    }
                }

                // SECONDARY: Continue as Guest / Explore
                OutlinedButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("guest_login_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Lanjut sebagai Tamu (Mode Cepat)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White.copy(alpha = 0.85f)
                    )
                }

                Text(
                    text = "Dengan masuk, Anda menyetujui Ketentuan & Privasi CodeNest",
                    style = MaterialTheme.typography.labelSmall,
                    color = IosTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = IosTextSecondary
            )
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.46f

        // Draw 4 Google Brand colored arcs
        // Red (Top / Top-Left)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 100f,
            useCenter = true,
            size = androidx.compose.ui.geometry.Size(w, h)
        )
        // Yellow (Bottom-Left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 60f,
            useCenter = true,
            size = androidx.compose.ui.geometry.Size(w, h)
        )
        // Green (Bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 40f,
            sweepAngle = 80f,
            useCenter = true,
            size = androidx.compose.ui.geometry.Size(w, h)
        )
        // Blue (Right bar and sector)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 310f,
            sweepAngle = 90f,
            useCenter = true,
            size = androidx.compose.ui.geometry.Size(w, h)
        )

        // Inner cutout for G shape
        drawCircle(
            color = Color.White,
            radius = r * 0.58f,
            center = Offset(cx, cy)
        )

        // Center crossbar for 'G'
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(cx - 1f, cy - (h * 0.13f)),
            size = androidx.compose.ui.geometry.Size(w * 0.46f, h * 0.26f)
        )
    }
}

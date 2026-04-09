package com.example.studentnestfinder.ui.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import com.example.studentnestfinder.db.entities.UserPreference
import com.example.studentnestfinder.security.BCryptPasswordHasher
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor

@Composable
fun AuthScreen(viewModel: AuthViewModel, onNavigateHome: (role: String) -> Unit) {
    val state by viewModel.uiState.collectAsState()

    if (state.authSuccess) {
        LaunchedEffect(Unit) { onNavigateHome(state.role) }
    }

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                title = { Text("StudentNestFinder", color = NeutralColor) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (state.isLogin) "Login" else "Sign Up",
                color = NeutralColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Student Nest Finder Gaborone", color = TextSecondaryColor)

            Spacer(modifier = Modifier.height(32.dp))

            // Role Toggle
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AuthTab(text = "Student", selected = state.role == "STUDENT") { viewModel.updateRole("STUDENT") }
                AuthTab(text = "Landlord", selected = state.role == "PROVIDER") { viewModel.updateRole("PROVIDER") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!state.isLogin) {
                AuthInput(value = state.name, label = "Full Name", onValueChange = viewModel::updateName, icon = Icons.Default.Person)
                AuthInput(value = state.email, label = "Email", onValueChange = viewModel::updateEmail, icon = Icons.Default.Email)
                
                if (state.role == "STUDENT") {
                    UniversityDropdown(
                        selectedUniversity = state.selectedUniversity,
                        onUniversitySelected = viewModel::updateUniversity
                    )
                }
            }

            AuthInput(
                value = state.studentId,
                label = if (state.role == "STUDENT") "Student ID" else "Landlord ID",
                onValueChange = viewModel::updateStudentId,
                icon = Icons.Default.Badge
            )

            AuthInput(
                value = state.password,
                label = "Password",
                onValueChange = viewModel::updatePassword,
                icon = Icons.Default.Lock,
                isPassword = true
            )

            state.error?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { if (state.isLogin) viewModel.login() else viewModel.register() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(if (state.isLogin) "Login" else "Create Account", color = Color.White, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = { viewModel.toggleMode() }) {
                Text(
                    if (state.isLogin) "New here? Create an account" else "Already have an account? Login",
                    color = TextSecondaryColor
                )
            }
        }
    }
}

@Composable
fun AuthInput(value: String, label: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, isPassword: Boolean = false) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondaryColor) },
        leadingIcon = { Icon(icon, contentDescription = "$label icon", tint = PrimaryColor) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = NeutralColor,
            unfocusedTextColor = NeutralColor,
            cursorColor = PrimaryColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun UniversityDropdown(selectedUniversity: String, onUniversitySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val universities = listOf(
        "University of Botswana (UB)",
        "Botho University",
        "Botswana Accountancy College (BAC)",
        "Imperial School of Business and Science (ISBS)",
        "Boitekanelo College"
    )

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        TextField(
            value = selectedUniversity,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Institution", color = TextSecondaryColor) },
            leadingIcon = { Icon(Icons.Default.School, contentDescription = "Institution icon", tint = PrimaryColor) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Open institutions", tint = NeutralColor)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = NeutralColor,
                unfocusedTextColor = NeutralColor,
                cursorColor = PrimaryColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            universities.forEach { uni ->
                DropdownMenuItem(
                    text = { Text(uni, color = NeutralColor) },
                    onClick = {
                        onUniversitySelected(uni)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AuthTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.padding(4.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PrimaryColor else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, TextSecondaryColor)
    ) {
        Text(text, color = if (selected) Color.White else NeutralColor, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
    }
}

// Previews
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthScreenLoginPreview() {
    val viewModel = AuthViewModel(MockUserDao(), MockUserPreferenceDao(), BCryptPasswordHasher())
    AuthScreen(viewModel = viewModel, onNavigateHome = { })
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthScreenSignUpPreview() {
    val viewModel = AuthViewModel(MockUserDao(), MockUserPreferenceDao(), BCryptPasswordHasher())
    // Trigger sign up mode
    viewModel.toggleMode()
    AuthScreen(viewModel = viewModel, onNavigateHome = { })
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthInputPreview() {
    AuthInput(
        value = "john.doe@example.com",
        label = "Email",
        onValueChange = {},
        icon = Icons.Default.Email
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthInputPasswordPreview() {
    AuthInput(
        value = "password123",
        label = "Password",
        onValueChange = {},
        icon = Icons.Default.Lock,
        isPassword = true
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthTabSelectedPreview() {
    AuthTab(text = "Student", selected = true, onClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthTabUnselectedPreview() {
    AuthTab(text = "Provider", selected = false, onClick = {})
}

// Mock UserDao for preview
class MockUserDao : com.example.studentnestfinder.db.dao.UserDao {
    override suspend fun insert(user: com.example.studentnestfinder.db.entities.User): Long = 1
    override suspend fun insertAll(users: List<com.example.studentnestfinder.db.entities.User>) {}
    override suspend fun update(user: com.example.studentnestfinder.db.entities.User) {}
    override suspend fun login(studentId: String, passwordHash: String): com.example.studentnestfinder.db.entities.User? = null
    override suspend fun getById(id: Int): com.example.studentnestfinder.db.entities.User? = null
    override suspend fun getByStudentId(studentId: String): com.example.studentnestfinder.db.entities.User? = null
    override suspend fun count(): Int = 0
}

class MockUserPreferenceDao : com.example.studentnestfinder.db.dao.UserPreferenceDao {
    override suspend fun upsert(preference: UserPreference) {}
    override fun getForUser(userId: Int): kotlinx.coroutines.flow.Flow<UserPreference?> =
        kotlinx.coroutines.flow.flowOf(null)
    override suspend fun getForUserOnce(userId: Int): UserPreference? = null
    override suspend fun getAllWithNotificationsEnabled(): List<UserPreference> = emptyList()
}

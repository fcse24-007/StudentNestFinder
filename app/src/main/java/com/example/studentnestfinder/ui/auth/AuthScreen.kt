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
import androidx.compose.ui.platform.LocalContext
import com.example.studentnestfinder.db.AppDatabase

@Composable
fun AuthScreen(viewModel: AuthViewModel, onNavigateHome: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    if (state.authSuccess) {
        LaunchedEffect(Unit) { onNavigateHome() }
    }

    Scaffold(
        containerColor = Color(0xFF121212)
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
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text("Student Nest Finder Gaborone", color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            // Role Toggle
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AuthTab(text = "Student", selected = state.role == "STUDENT") { viewModel.updateRole("STUDENT") }
                AuthTab(text = "Provider", selected = state.role == "PROVIDER") { viewModel.updateRole("PROVIDER") }
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
                label = if (state.role == "STUDENT") "Student ID" else "Provider ID",
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                else Text(if (state.isLogin) "Login" else "Create Account", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = { viewModel.toggleMode() }) {
                Text(
                    if (state.isLogin) "New here? Create an account" else "Already have an account? Login",
                    color = Color.LightGray
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
        label = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFFBB86FC)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1E1E1E),
            unfocusedContainerColor = Color(0xFF1E1E1E),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFFBB86FC),
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
            label = { Text("Select Institution", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFBB86FC)) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFBB86FC),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1E1E))
        ) {
            universities.forEach { uni ->
                DropdownMenuItem(
                    text = { Text(uni, color = Color.White) },
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
        color = if (selected) Color(0xFFBB86FC) else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, Color.Gray)
    ) {
        Text(text, color = if (selected) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
    }
}

// Previews
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthScreenLoginPreview() {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val viewModel = AuthViewModel(MockUserDao(), db)
    AuthScreen(viewModel = viewModel, onNavigateHome = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun AuthScreenSignUpPreview() {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val viewModel = AuthViewModel(MockUserDao(), db)
    // Trigger sign up mode
    viewModel.toggleMode()
    AuthScreen(viewModel = viewModel, onNavigateHome = {})
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


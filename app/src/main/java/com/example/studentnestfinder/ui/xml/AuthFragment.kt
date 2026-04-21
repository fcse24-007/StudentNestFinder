package com.example.studentnestfinder.ui.xml

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studentnestfinder.R
import com.example.studentnestfinder.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {
    interface Callbacks {
        fun onAuthSuccess()
    }

    private val viewModel: AuthViewModel by viewModels()
    private var callbacks: Callbacks? = null

    private lateinit var modeTitle: TextView
    private lateinit var studentIdInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var universityInput: EditText
    private lateinit var errorText: TextView
    private lateinit var submitButton: Button
    private lateinit var switchModeButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        modeTitle = view.findViewById(R.id.modeTitle)
        studentIdInput = view.findViewById(R.id.studentIdInput)
        nameInput = view.findViewById(R.id.nameInput)
        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        roleSpinner = view.findViewById(R.id.roleSpinner)
        universityInput = view.findViewById(R.id.universityInput)
        errorText = view.findViewById(R.id.errorText)
        submitButton = view.findViewById(R.id.submitButton)
        switchModeButton = view.findViewById(R.id.switchModeButton)

        roleSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("STUDENT", "PROVIDER")
        )

        submitButton.setOnClickListener {
            val role = roleSpinner.selectedItem?.toString() ?: "STUDENT"
            viewModel.updateRole(role)
            viewModel.updateStudentId(studentIdInput.text.toString())
            viewModel.updatePassword(passwordInput.text.toString())
            viewModel.updateName(nameInput.text.toString())
            viewModel.updateEmail(emailInput.text.toString())
            val university = universityInput.text.toString().ifBlank {
                getString(R.string.default_university)
            }
            viewModel.updateUniversity(university)
            if (viewModel.uiState.value.isLogin) viewModel.login() else viewModel.register()
        }
        switchModeButton.setOnClickListener { viewModel.toggleMode() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    modeTitle.text = if (state.isLogin) getString(R.string.login) else getString(R.string.register)
                    submitButton.text = if (state.isLogin) getString(R.string.login) else getString(R.string.register)
                    switchModeButton.text = if (state.isLogin) {
                        getString(R.string.switch_to_register)
                    } else {
                        getString(R.string.switch_to_login)
                    }

                    val showRegisterFields = !state.isLogin
                    nameInput.isVisible = showRegisterFields
                    emailInput.isVisible = showRegisterFields
                    universityInput.isVisible = showRegisterFields
                    roleSpinner.isVisible = showRegisterFields

                    errorText.text = state.error ?: ""
                    errorText.isVisible = !state.error.isNullOrBlank()

                    if (state.authSuccess) {
                        callbacks?.onAuthSuccess()
                    }
                }
            }
        }
    }
}

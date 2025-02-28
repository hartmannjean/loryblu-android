package com.loryblu.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loryblu.core.ui.R
import com.loryblu.core.ui.components.LBButton
import com.loryblu.core.ui.components.LBEmailTextField
import com.loryblu.core.ui.components.LBIconButton
import com.loryblu.core.ui.components.LBPasswordTextField
import com.loryblu.core.ui.components.LBRadioButton
import com.loryblu.core.ui.components.LBTitle
import com.loryblu.core.ui.theme.LBDarkBlue
import com.loryblu.core.ui.theme.LBErrorColor
import com.loryblu.core.ui.theme.LBLightGray
import com.loryblu.core.ui.theme.LBSilverGray
import com.loryblu.core.ui.theme.LBShadowGray
import com.loryblu.core.ui.theme.LBSkyBlue
import com.loryblu.core.ui.theme.LBSoftGray
import com.loryblu.core.util.validators.EmailInputValid
import com.loryblu.core.util.validators.PasswordInputValid

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    authenticated: Boolean,
    onLoginButtonClicked: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    navigateToRegisterNow: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var isEmailFieldFocused by remember { mutableStateOf(false) }
    var isPasswordFieldFocused by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        LBTitle(textRes = R.string.login_title)

        Spacer(modifier = Modifier.height(32.dp))

        LBEmailTextField(
            onValueChange = { email: String ->
                viewModel.updateEmail(email)
                viewModel.emailState()
            },
            placeholderRes = stringResource(id = R.string.email),
            value = uiState.email,
            error = uiState.emailState,
            fieldFocus = { isEmailFieldFocused = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LBPasswordTextField(
            onValueChange = { password: String ->
                viewModel.run {
                    updatePassword(password)
                    passwordState()
                }
            },
            onButtonClick = { passwordHidden = !passwordHidden },
            placeholderRes = stringResource(id = R.string.password),
            value = uiState.password,
            error = uiState.passwordState,
            hidden = passwordHidden,
            fieldFocus = { isPasswordFieldFocused = it }
        )

        if (
            uiState.emailState is EmailInputValid.Error && isEmailFieldFocused
            || uiState.passwordState is PasswordInputValid.Error && isPasswordFieldFocused
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                when {

                    uiState.emailState is EmailInputValid.Error && isEmailFieldFocused -> {
                        val emailError = uiState.emailState as EmailInputValid.Error

                        Text(
                            fontSize = 14.sp,
                            modifier = Modifier,
                            text = stringResource(id = emailError.messageId),
                            fontWeight = FontWeight.Bold,
                            color = LBErrorColor
                        )
                    }

                    uiState.passwordState is PasswordInputValid.Error && isPasswordFieldFocused -> {
                        val passwordError = uiState.passwordState as PasswordInputValid.Error

                        Text(
                            fontSize = 14.sp,
                            modifier = Modifier,
                            text = stringResource(id = passwordError.messageId),
                            fontWeight = FontWeight.Bold,
                            color = LBErrorColor
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)
        ) {
            LBRadioButton(
                isChecked = uiState.isLoginSaved,
                onCheckedChange = { viewModel.toggleIsLoginSaved() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = LBShadowGray,
                    unselectedColor = LBShadowGray
                ),
                )

            Text(
                text = stringResource(R.string.remember),
                fontWeight = FontWeight.ExtraBold,
                color = LBShadowGray,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.padding(top = 44.dp))

        LBButton(
            areAllFieldsValid = uiState.emailState is EmailInputValid.Valid
                    && uiState.passwordState is PasswordInputValid.Valid,
            textRes = R.string.sign_in,
            onClick = { onLoginButtonClicked() },
            buttonColors = ButtonDefaults.buttonColors(
                disabledContainerColor = LBLightGray,
                containerColor = LBSkyBlue
            ),
            textColor = if (
                uiState.emailState is EmailInputValid.Valid
                && uiState.passwordState is PasswordInputValid.Valid
                ) LBSoftGray else LBSkyBlue
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(2f)
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = LBShadowGray,
                            start = Offset(0.dp.toPx(), size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    .graphicsLayer(clip = true),
                thickness = 2.dp
            )
            Text(
                text = stringResource(R.string.or),
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(2f)
                    .drawWithContent {
                        drawContent()
                        drawLine(
                            color = LBShadowGray,
                            start = Offset(0.dp.toPx(), size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    .graphicsLayer(clip = true),
                thickness = 2.dp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp)
        ) {

            Text(
                text = stringResource(R.string.forgot_your_password),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { navigateToForgotPassword() }
                    .drawBehind {
                        drawLine(
                            color = LBShadowGray,
                            strokeWidth = 1.dp.toPx(),
                            start = Offset(-1.dp.toPx(), size.height + 2.dp.toPx()),
                            end = Offset(size.width + 1.dp.toPx(), size.height + 2.dp.toPx()),
                            cap = StrokeCap.Round
                        )
                    },
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 64.dp)
                .fillMaxWidth()
        ) {
            LBIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = stringResource(R.string.google_logo),
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            LBIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                content = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = stringResource(R.string.facebook_logo),
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = stringResource(R.string.don_t_have_an_account),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = LBSilverGray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 40.dp)
            )

            Text(
                text = stringResource(R.string.register_now),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = LBDarkBlue,
                modifier = Modifier
                    .padding(end = 40.dp)
                    .clickable { navigateToRegisterNow() }
                    .drawBehind {
                        drawLine(
                            color = LBDarkBlue,
                            strokeWidth = 2.dp.toPx(),
                            start = Offset(-1.dp.toPx(), size.height + 2.dp.toPx()),
                            end = Offset(size.width + 1.dp.toPx(), size.height + 2.dp.toPx()),
                            cap = StrokeCap.Round
                        )
                    },
            )
        }

    }

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHomeScreen()
        }
    }
}


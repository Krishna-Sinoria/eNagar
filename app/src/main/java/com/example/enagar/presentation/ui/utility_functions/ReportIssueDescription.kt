package com.example.enagar.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enagar.presentation.viewModel.CitizenViewModel

@Composable
fun ReportIssueDescription(vm : CitizenViewModel) {
    val report = vm.issueReport.value
    Text(
        text = "Describe the Issue in Brief (Optional)",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    OutlinedTextField(
        value =report.description ?: "",
        onValueChange = {

            vm.setDescription(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        placeholder = { Text("Enter description...") }
    )


}
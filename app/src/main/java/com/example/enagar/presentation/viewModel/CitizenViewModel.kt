package com.example.enagar.presentation.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.enagar.domain.models.IssueReportItem
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.util.UUID

@HiltViewModel
class CitizenViewModel @Inject constructor() : ViewModel(){



    private val _issueReport = mutableStateOf(IssueReportItem())
    val issueReport : State<IssueReportItem> = _issueReport

    private val _description = mutableStateOf<String?>(null)
    val description : State<String?> = _description

    private val _reportId = mutableStateOf<String?>(null)
    val reportId :State<String?> = _reportId

    fun generateReportId(){
        val reportId =  "REP-${UUID.randomUUID().toString().take(8).uppercase()}"
        _reportId.value = reportId
    }

    fun desriptionValue(desc: String?){
        _description.value = desc
    }


    fun setImageUri(uri: Uri){
        _issueReport.value = issueReport.value.copy(imageUri = uri)
    }

    fun setLocation(location: String){
        _issueReport.value = issueReport.value.copy(location = location)
    }

    fun setDescription(description: String){
        _issueReport.value = issueReport.value.copy(description = description)
    }

    fun setIssueType(issueType: String){
        _issueReport.value = issueReport.value.copy(issueType = issueType)
    }






}
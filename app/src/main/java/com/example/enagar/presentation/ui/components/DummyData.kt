package com.example.enagar.presentation.ui.components


import com.example.enagar.domain.models.NotificationItem
import com.example.enagar.domain.models.Report

object DummyData {
    val reports = listOf(
        Report(1, "Pothole on Main Street", "2025-09-01", "Pending"),
        Report(2, "Streetlight Outage", "2025-09-02", "In Progress"),
        Report(3, "Graffiti on Building", "2025-09-05", "Resolved")
    )

    val notifications = listOf(
        NotificationItem(1, "Report Resolved: Pothole on Main Street has been fixed.", "1h ago"),
        NotificationItem(2, "New Comment added: 'We are looking into this issue.'", "2h ago"),
        NotificationItem(3, "Report Assigned: Your issue 'Park Bench Broken' assigned to city staff.", "5h ago"),
        NotificationItem(4, "Report Received: Overflowing Trash Can noted.", "2d ago")
    )
}

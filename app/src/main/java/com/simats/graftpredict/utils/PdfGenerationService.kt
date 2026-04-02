package com.simats.graftpredict.utils

import android.content.Context
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.simats.graftpredict.data.models.ReportData
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerationService {

    private fun generateReportHtml(reportData: ReportData): String {
        // Format date
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val generatedDate = dateFormat.format(Date())

        // Helper function to convert any value to string
        fun anyToString(value: Any?): String {
            return when (value) {
                null -> "N/A"
                is String -> value
                is Number -> value.toString()
                else -> value.toString()
            }
        }

        // Helper function to convert any value to double
        fun anyToDouble(value: Any?): Double {
            return when (value) {
                null -> 0.0
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }

        val graftD1 = anyToDouble(reportData.graft_diameter_1)
        val graftD2 = anyToDouble(reportData.graft_diameter_2)
        val hamstring = anyToDouble(reportData.hamstring_autograft)
        val quad = anyToDouble(reportData.quadriceps_tendon_diameter)

        // Calculate bar heights based on values (scaled for visual representation)
        val maxDiameter = maxOf(graftD1, graftD2, hamstring, quad, 1.0)

        // Calculate percentages for bar heights (60-85% range as in HTML)
        fun calculateBarHeight(value: Double): String {
            val normalized = value / maxDiameter
            val height = 60 + (normalized * 25) // Scale between 60% and 85%
            return "${height.toInt()}%"
        }

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8"/>
            <meta content="width=device-width, initial-scale=1.0" name="viewport"/>
            <title>Graft Size Analysis Report</title>
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
            <style>
                @page {
                    margin: 0;
                    size: A4;
                }
                
                body {
                    font-family: 'Inter', sans-serif;
                    color: #1e293b;
                    background-color: white;
                    margin: 0;
                    padding: 0;
                    width: 100%;
                }
                
                .report-container {
                    width: 100%;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 40px;
                    box-sizing: border-box;
                }
                
                header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-bottom: 40px;
                    padding-top: 20px;
                    border-bottom: 2px solid #137fec;
                    padding-bottom: 20px;
                }
                
                .header-left {
                    display: flex;
                    align-items: center;
                    gap: 15px;
                }
                
                .logo-icon {
                    background-color: #137fec;
                    padding: 12px;
                    border-radius: 12px;
                    color: white;
                    font-size: 28px;
                    width: 50px;
                    height: 50px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                
                .app-title {
                    font-size: 24px;
                    font-weight: 700;
                    line-height: 1.2;
                    margin: 0;
                }
                
                .app-subtitle {
                    font-size: 12px;
                    font-weight: 700;
                    color: #137fec;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    margin: 0;
                }
                
                .header-right {
                    text-align: right;
                }
                
                .report-title {
                    font-size: 20px;
                    font-weight: 700;
                    color: #334155;
                    line-height: 1.2;
                    margin: 0;
                }
                
                .report-date {
                    font-size: 12px;
                    color: #64748b;
                    margin-top: 8px;
                    margin-bottom: 4px;
                }
                
                .report-ref {
                    font-size: 11px;
                    color: #94a3b8;
                    margin: 0;
                }
                
                .grid-2col {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 30px;
                    margin-bottom: 40px;
                }
                
                .section {
                    margin-bottom: 30px;
                }
                
                .section-header {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    margin-bottom: 20px;
                    padding-bottom: 10px;
                    border-bottom: 1px solid #e2e8f0;
                }
                
                .section-title {
                    font-size: 14px;
                    font-weight: 700;
                    color: #64748b;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                    margin: 0;
                }
                
                .info-row {
                    display: flex;
                    justify-content: space-between;
                    border-bottom: 1px solid #f1f5f9;
                    padding: 10px 0;
                }
                
                .info-label {
                    font-size: 14px;
                    color: #64748b;
                }
                
                .info-value {
                    font-size: 14px;
                    font-weight: 600;
                    color: #1e293b;
                }
                
                .chart-container {
                    background-color: #f8fafc;
                    border: 1px solid #e2e8f0;
                    border-radius: 12px;
                    padding: 30px;
                    height: 250px;
                    margin-bottom: 40px;
                }
                
                .chart-bars {
                    height: 100%;
                    display: flex;
                    align-items: flex-end;
                    justify-content: space-around;
                }
                
                .bar-group {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    gap: 10px;
                    width: 60px;
                }
                
                .bar-value {
                    font-size: 14px;
                    font-weight: 700;
                }
                
                .bar {
                    width: 20px;
                    border-radius: 4px 4px 0 0;
                    min-height: 10px;
                }
                
                .bar-label {
                    font-size: 12px;
                    font-weight: 700;
                    color: #64748b;
                    white-space: nowrap;
                }
                
                .table-container {
                    border: 1px solid #e2e8f0;
                    border-radius: 12px;
                    overflow: hidden;
                    margin-bottom: 40px;
                }
                
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
                
                th {
                    background-color: #f8fafc;
                    font-size: 12px;
                    font-weight: 700;
                    color: #94a3b8;
                    text-transform: uppercase;
                    padding: 16px;
                    text-align: left;
                    border-bottom: 1px solid #e2e8f0;
                }
                
                td {
                    padding: 16px;
                    font-size: 14px;
                    border-bottom: 1px solid #e2e8f0;
                }
                
                tr:last-child td {
                    border-bottom: none;
                }
                
                .metric-name {
                    font-weight: 600;
                }
                
                .metric-value {
                    text-align: center;
                    font-weight: 700;
                }
                
                .metric-unit {
                    text-align: center;
                    color: #94a3b8;
                }
                
                .metric-note {
                    font-size: 12px;
                    color: #64748b;
                }
                
                .highlighted {
                    color: #137fec !important;
                }
                
                .highlighted-bg {
                    background-color: #eff6ff !important;
                }
                
                .section-divider {
                    height: 1px;
                    background-color: #e2e8f0;
                    margin: 25px 0;
                }
                
                footer {
                    margin-top: 50px;
                    padding-top: 30px;
                    border-top: 2px solid rgba(19, 127, 236, 0.1);
                }
                
                .footer-content {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    gap: 30px;
                }
                
                .legal-disclaimer {
                    flex: 1;
                }
                
                .legal-title {
                    font-size: 12px;
                    font-weight: 700;
                    color: #94a3b8;
                    text-transform: uppercase;
                    margin-bottom: 8px;
                }
                
                .legal-text {
                    font-size: 11px;
                    color: #94a3b8;
                    font-style: italic;
                    line-height: 1.4;
                }
                
                .qr-container {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                }
                
                .qr-code {
                    background-color: rgba(148, 163, 184, 0.1);
                    padding: 10px;
                    border-radius: 8px;
                }
                
                .qr-label {
                    font-size: 10px;
                    color: #94a3b8;
                    font-weight: 500;
                    margin-top: 5px;
                }
                
                .signature-section {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-end;
                    margin-top: 40px;
                }
                
                .signature-line {
                    border-bottom: 1px solid #cbd5e1;
                    width: 200px;
                    height: 40px;
                    margin-bottom: 8px;
                }
                
                .signature-label {
                    font-size: 12px;
                    font-weight: 700;
                    color: #475569;
                }
                
                .signature-subtext {
                    font-size: 10px;
                    color: #94a3b8;
                    font-style: italic;
                }
                
                .page-info {
                    text-align: right;
                }
                
                .page-text {
                    font-size: 10px;
                    color: #94a3b8;
                    line-height: 1.4;
                }
                
                @media print {
                    body {
                        padding: 0 !important;
                        margin: 0 !important;
                    }
                    
                    .report-container {
                        padding: 20px !important;
                        margin: 0 !important;
                        width: 100% !important;
                    }
                    
                    .chart-container {
                        page-break-inside: avoid;
                    }
                    
                    .table-container {
                        page-break-inside: avoid;
                    }
                }
            </style>
        </head>
        <body>
            <div class="report-container">
                <header>
                    <div class="header-left">
                        <div class="logo-icon">
                            📊
                        </div>
                        <div>
                            <h1 class="app-title">GraftPredict</h1>
                            <p class="app-subtitle">Medical Analysis Report</p>
                        </div>
                    </div>
                    <div class="header-right">
                        <h2 class="report-title">Graft Size<br/>Analysis Report</h2>
                        <p class="report-date">Generated Date: $generatedDate</p>
                        <p class="report-ref">Ref: GP-${System.currentTimeMillis().toString().takeLast(6)}</p>
                    </div>
                </header>
                
                <div class="grid-2col">
                    <section class="section">
                        <div class="section-header">
                            <div style="font-size: 20px;">👤</div>
                            <h3 class="section-title">Patient Information</h3>
                        </div>
                        <div class="info-grid">
                            <div class="info-row">
                                <span class="info-label">Full Name</span>
                                <span class="info-value">${anyToString(reportData.name)}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Age</span>
                                <span class="info-value">${anyToString(reportData.age)} Years</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Gender</span>
                                <span class="info-value">${anyToString(reportData.gender)}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">BMI</span>
                                <span class="info-value">${anyToString(reportData.bmi)} kg/m²</span>
                            </div>
                        </div>
                    </section>
                    
                    <section class="section">
                        <div class="section-header">
                            <div style="font-size: 20px;">📋</div>
                            <h3 class="section-title">Clinical Data</h3>
                        </div>
                        <div class="info-grid">
                            <div class="info-row">
                                <span class="info-label">Height / Weight</span>
                                <span class="info-value">${anyToString(reportData.height)} cm / ${anyToString(reportData.weight)} kg</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Affected Side</span>
                                <span class="info-value">${anyToString(reportData.affected_side)}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Injury Date</span>
                                <span class="info-value">${anyToString(reportData.affected_date)}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">MRI Verified</span>
                                <span class="info-value" style="color: #10b981;">Yes</span>
                            </div>
                        </div>
                    </section>
                </div>
                
                <div class="section-divider"></div>
                
                <section class="section">
                    <div class="section-header" style="justify-content: space-between;">
                        <div style="display: flex; align-items: center; gap: 10px;">
                            <div style="font-size: 20px;">📈</div>
                            <h3 class="section-title">Analysis Visual: Diameter Projection</h3>
                        </div>
                        <span style="font-size: 11px; color: #94a3b8; font-style: italic;">Values expressed in mm</span>
                    </div>
                    
                    <div class="chart-container">
                        <div class="chart-bars">
                            <!-- Graft D1 Bar -->
                            <div class="bar-group">
                                <div class="bar-value" style="color: #137fec;">${String.format("%.1f", graftD1)}</div>
                                <div class="bar" style="background-color: #137fec; height: ${calculateBarHeight(graftD1)};"></div>
                                <span class="bar-label">Graft D1</span>
                            </div>
                            
                            <!-- Graft D2 Bar -->
                            <div class="bar-group">
                                <div class="bar-value" style="color: #8b5cf6;">${String.format("%.1f", graftD2)}</div>
                                <div class="bar" style="background-color: #8b5cf6; height: ${calculateBarHeight(graftD2)};"></div>
                                <span class="bar-label">Graft D2</span>
                            </div>
                            
                            <!-- Hamstring Bar -->
                            <div class="bar-group">
                                <div class="bar-value" style="color: #14b8a6;">${String.format("%.1f", hamstring)}</div>
                                <div class="bar" style="background-color: #14b8a6; height: ${calculateBarHeight(hamstring)};"></div>
                                <span class="bar-label">Hamstring</span>
                            </div>
                            
                            <!-- Quad Bar -->
                            <div class="bar-group">
                                <div class="bar-value" style="color: #ec4899;">${String.format("%.1f", quad)}</div>
                                <div class="bar" style="background-color: #ec4899; height: ${calculateBarHeight(quad)};"></div>
                                <span class="bar-label">Quad</span>
                            </div>
                        </div>
                    </div>
                </section>
                
                <section class="section">
                    <div class="section-header">
                        <div style="font-size: 20px;">📊</div>
                        <h3 class="section-title">Calculated Results</h3>
                    </div>
                    
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Metric Description</th>
                                    <th style="text-align: center;">Value</th>
                                    <th style="text-align: center;">Unit</th>
                                    <th>Clinical Note</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="metric-name">Graft Diameter 1</td>
                                    <td class="metric-value">${String.format("%.1f", graftD1)}</td>
                                    <td class="metric-unit">mm</td>
                                    <td class="metric-note">Standard projection</td>
                                </tr>
                                <tr class="highlighted-bg">
                                    <td class="metric-name">Graft Diameter 2</td>
                                    <td class="metric-value">${String.format("%.1f", graftD2)}</td>
                                    <td class="metric-unit">mm</td>
                                    <td class="metric-note">Upper confidence limit</td>
                                </tr>
                                <tr>
                                    <td class="metric-name highlighted">Predicted ST Value</td>
                                    <td class="metric-value highlighted">${anyToString(reportData.predicted_st_value)}</td>
                                    <td class="metric-unit">mm</td>
                                    <td class="metric-note">Sufficient length confirmed</td>
                                </tr>
                                <tr class="highlighted-bg">
                                    <td class="metric-name">Gracilis Length</td>
                                    <td class="metric-value">${anyToString(reportData.gracilis_length)}</td>
                                    <td class="metric-unit">mm</td>
                                    <td class="metric-note">Measured from clinical</td>
                                </tr>
                                <tr>
                                    <td class="metric-name">Quadriceps Tendon</td>
                                    <td class="metric-value">${String.format("%.1f", quad)}</td>
                                    <td class="metric-unit">mm</td>
                                    <td class="metric-note">Recommended for BMI</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
                
                <footer>
                    <div class="footer-content">
                        <div class="legal-disclaimer">
                            <p class="legal-title">Legal Disclaimer</p>
                            <p class="legal-text">
                                Generated based on predictive algorithms. Clinical decision support only. 
                                Intraoperative measurements may vary. This report is for informational purposes 
                                and should not replace professional medical advice.
                            </p>
                        </div>
                        <div class="qr-container">
                            <div class="qr-code">
                                <div style="width: 60px; height: 60px; background-color: #cbd5e1; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #64748b; font-size: 10px; font-weight: bold;">
                                    QR<br>CODE
                                </div>
                            </div>
                            <span class="qr-label">Scan to Verify</span>
                        </div>
                    </div>
                    
                    <div class="signature-section">
                        <div>
                            <div class="signature-line"></div>
                            <p class="signature-label">Reviewing Physician</p>
                            <p class="signature-subtext">Date signed: _________</p>
                        </div>
                        <div class="page-info">
                            <p class="page-text">
                                Powered by GraftPredict Suite v4.2<br/>
                                Document ID: GP-${System.currentTimeMillis()}<br/>
                                Page 1 of 1
                            </p>
                        </div>
                    </div>
                </footer>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    fun generatePdfFromWebView(context: Context, reportData: ReportData, onSuccess: (File) -> Unit, onError: (String) -> Unit) {
        try {
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true

            val htmlContent = generateReportHtml(reportData)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    // Create PDF after page is loaded
                    createPdfFromWebView(context, webView, reportData, onSuccess, onError)
                }
            }

            // Load the HTML content
            webView.loadDataWithBaseURL(
                null,
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )

        } catch (e: Exception) {
            onError("Failed to generate PDF: ${e.message}")
        }
    }

    private fun createPdfFromWebView(
        context: Context,
        webView: WebView,
        reportData: ReportData,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

            // Create a PrintDocumentAdapter
            val printAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.createPrintDocumentAdapter("GraftPredict_Report")
            } else {
                webView.createPrintDocumentAdapter()
            }

            // Generate file name
            val fileName = "GraftPredict_Report_${System.currentTimeMillis()}.pdf"
            val downloadsDir = context.getExternalFilesDir(null)
            val pdfFile = File(downloadsDir, fileName)

            // Print to file
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()

            val printJob = printManager.print(
                "GraftPredict Report",
                printAdapter,
                printAttributes
            )

            // For API 26+, we can write to file directly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    // Write HTML to file as backup method
                    val htmlContent = generateReportHtml(reportData)
                    pdfFile.writeText(htmlContent)

                    // Show toast and callback
                    Toast.makeText(context, "Report saved as HTML file", Toast.LENGTH_SHORT).show()
                    onSuccess(pdfFile)
                } catch (e: Exception) {
                    onError("Failed to save file: ${e.message}")
                }
            } else {
                // For older versions, we'll save the HTML content
                try {
                    val htmlContent = generateReportHtml(reportData)
                    pdfFile.writeText(htmlContent)

                    Toast.makeText(context, "Report saved as HTML file", Toast.LENGTH_SHORT).show()
                    onSuccess(pdfFile)
                } catch (e: Exception) {
                    onError("Failed to save file: ${e.message}")
                }
            }

        } catch (e: Exception) {
            onError("Printing failed: ${e.message}")
        }
    }
}

package com.example.graftpredict.ml

/**
 * Predicts autograft measurements from anthropometric measurements.
 * Uses exact clinical formulas based on peer-reviewed medical research.
 */
object AutograftPredictor {

    /**
     * Calculates graft diameter 1 (MRI-based)
     * Formula: Graft diameter 1 = √[2(AB + CD)]
     * Where A,B,C,D represent posterior/lateral measurements
     */
    fun calculateGraftDiameter1(
        heightCm: Double,
        weightKg: Double,
        legLengthCm: Double,
        thighLengthCm: Double,
        circumferenceThighCm: Double,
        posteriorSTmm: Double,
        posteriorGracilisMm: Double,
        lateralSTmm: Double,
        lateralGracilisMm: Double,
        isFemale: Boolean
    ): Double {
        // A = posteriorST, B = lateralST, C = posteriorGracilis, D = lateralGracilis
        val a = posteriorSTmm
        val b = lateralSTmm
        val c = posteriorGracilisMm
        val d = lateralGracilisMm
        
        // Formula: √[2(AB + CD)]
        val ab = a * b
        val cd = c * d
        val result = kotlin.math.sqrt(2.0 * (ab + cd))
        
        return roundToOneDecimal(result)
    }

    /**
     * Calculates graft diameter 2 (Anthropometric-only, no MRI)
     * Formula: Graft diameter 2 (mm) = 0.079 × height + 0.068 × thigh circumference − 9.031
     */
    fun calculateGraftDiameter2(
        heightCm: Double,
        weightKg: Double,
        legLengthCm: Double,
        thighLengthCm: Double,
        circumferenceThighCm: Double,
        isFemale: Boolean
    ): Double {
        // Formula: 0.079 × height + 0.068 × thigh circumference − 9.031
        val result = 0.079 * heightCm + 0.068 * circumferenceThighCm - 9.031
        return roundToOneDecimal(result)
    }

    /**
     * Calculates hamstring autograft (mm)
     * Formula: 2.074 − 0.198 (if female) + 0.025 × height + 0.623 × ln(BMI) + 0.523 (if 5-strand graft)
     * Note: Using default assumption of not female and 4-strand graft for this calculation
     */
    fun calculateHamstringAutograft(
        heightCm: Double,
        weightKg: Double,
        isFemale: Boolean,
        isStrandGraft5: Boolean = false
    ): Double {
        val bmi = weightKg / (heightCm / 100.0) / (heightCm / 100.0)
        
        // Formula: 2.074 − 0.198 (if female) + 0.025 × height + 0.623 × ln(BMI) + 0.523 (if 5-strand)
        var result = 2.074
        
        if (isFemale) {
            result -= 0.198
        }
        
        result += 0.025 * heightCm
        result += 0.623 * kotlin.math.ln(bmi)
        
        if (isStrandGraft5) {
            result += 0.523
        }
        
        return roundToOneDecimal(result)
    }

    /**
     * Calculates quadriceps tendon diameter
     * Formula: Quad Tendon = (0.065 × thigh length + 0.028 × height) − 0.931
     */
    fun calculateQuadricepsTendonDiameter(
        thighLengthCm: Double,
        heightCm: Double
    ): Double {
        // Formula: (0.065 × thigh length + 0.028 × height) − 0.931
        val result = (0.065 * thighLengthCm + 0.028 * heightCm) - 0.931
        return roundToOneDecimal(result)
    }

    /**
     * Calculates minimum ST length
     * Formula: Minimum ST length = (Height − 3) / 6.01
     */
    fun calculateMinimumSTLength(heightCm: Double): Double {
        // Formula: (Height − 3) / 6.01
        val result = (heightCm - 3.0) / 6.01
        return roundToOneDecimal(result)
    }

    /**
     * Calculates predicted ST value
     * Formula: predicted st value = 4.569 × leg length − 101.733
     */
    fun calculatePredictedSTValue(legLengthCm: Double): Double {
        // Formula: 4.569 × leg length − 101.733
        val result = 4.569 * legLengthCm - 101.733
        return roundToOneDecimal(result)
    }

    /**
     * Calculates gracilis length
     * Formula: Gracilis length = 3.698 × height − 358.985
     */
    fun calculateGracilisLength(heightCm: Double): Double {
        // Formula: 3.698 × height − 358.985
        val result = 3.698 * heightCm - 358.985
        return roundToOneDecimal(result)
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {
        return value.coerceIn(min, max)
    }

    private fun roundToOneDecimal(value: Double): Double {
        return kotlin.math.round(value * 10.0) / 10.0
    }
}

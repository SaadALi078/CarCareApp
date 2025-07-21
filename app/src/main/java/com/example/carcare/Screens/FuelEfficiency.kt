import com.example.carcare.viewmodels.FuelLog


object FuelEfficiencyCalculator {
    fun calculateEfficiency(logs: List<FuelLog>): Pair<Double, Double> {
        if (logs.size < 2) return 0.0 to 0.0

        val sortedLogs = logs.sortedBy { it.odometer }
        var totalDistance = 0
        var totalFuel = 0.0

        for (i in 1 until sortedLogs.size) {
            val distance = sortedLogs[i].odometer - sortedLogs[i - 1].odometer
            val fuel = sortedLogs[i].amount

            totalDistance += distance
            totalFuel += fuel
        }

        if (totalFuel <= 0) return 0.0 to 0.0

        val efficiency = totalDistance / totalFuel
        val costPerKm = if (totalDistance > 0) {
            logs.sumOf { it.cost } / totalDistance
        } else {
            0.0
        }

        return efficiency to costPerKm
    }
}
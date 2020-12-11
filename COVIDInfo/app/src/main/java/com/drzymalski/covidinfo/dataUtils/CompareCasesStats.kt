package com.drzymalski.covidinfo.dataUtils

import com.drzymalski.covidinfo.apiUtils.models.DataDay
import com.drzymalski.covidinfo.apiUtils.models.DataProvider
import com.drzymalski.covidinfo.apiUtils.models.old.CovidDay
import com.drzymalski.covidinfo.config.CountryConfig

class CompareCasesStats{
    val totalCasesList = mutableListOf<Int>()

    private val newDeathsList = mutableListOf<Int>()
    private val newRecoveredList = mutableListOf<Int>()
    private val newCasesList = mutableListOf<Int>()

    val activeCasesList = mutableListOf<Int>()

    val newCasesWeeklyList = mutableListOf<Float>()
    val newDeathsWeeklyList = mutableListOf<Float>()
    val newRecoveredWeeklyList = mutableListOf<Float>()

    var datesList = mutableListOf<String>()
    //val datesFullList = mutableListOf<String>()

    var country: CountryConfig = CountryConfig()

    private fun clearData(){
        totalCasesList.clear()

        newDeathsList.clear()
        newRecoveredList.clear()
        newCasesList.clear()

        activeCasesList.clear()

        newCasesWeeklyList.clear()
        newDeathsWeeklyList.clear()
        newRecoveredWeeklyList.clear()

        datesList.clear()
        //datesFullList.clear()
    }

    private fun getWeeklyAverage(){
        newCasesWeeklyList.clear()
        newDeathsWeeklyList.clear()
        newRecoveredWeeklyList.clear()

        for (i in 0 until newCasesList.size){
            val from = if (i-3<0) 0 else i-3
            val to = if (i+3>=newCasesList.size-1) newCasesList.size-1 else i+3
            var sumNew = 0f
            var sumDeaths = 0f
            var sumRecovered = 0f
            val count = to - from + 1f
            for (j in from..to) {
                if (j<newCasesList.size) sumNew += newCasesList[j].toFloat()
                if (j<newDeathsList.size) sumDeaths += newDeathsList[j].toFloat()
                if (j<newRecoveredList.size) sumRecovered += newRecoveredList[j].toFloat()
            }
            newCasesWeeklyList += (sumNew/count)
            newDeathsWeeklyList += (sumDeaths/count)
            newRecoveredWeeklyList += (sumRecovered/count)
        }

        newCasesList.clear()
        newDeathsList.clear()
        newRecoveredList.clear()
    }

    fun calculateStats(covidData: List<DataDay>){
        clearData()
        var lastCases = 0
        var lastDeaths = 0
        var lastRecovered = 0

        covidData.forEach { casesOnDay ->
            run {
                totalCasesList += casesOnDay.cnt_confirmed

                activeCasesList += casesOnDay.cnt_active

                datesList.plusAssign(DateConverter.formatDateShort(casesOnDay.date_stamp))

                if (lastDeaths==0) {lastDeaths = casesOnDay.cnt_death}
                else {
                    newDeathsList += (casesOnDay.cnt_death - lastDeaths)
                    lastDeaths = casesOnDay.cnt_death
                }

                if (lastRecovered==0) {lastRecovered = casesOnDay.cnt_recovered}
                else {
                    newRecoveredList += (casesOnDay.cnt_recovered - lastRecovered)
                    lastRecovered = casesOnDay.cnt_recovered
                }

                if (lastCases==0) {lastCases = casesOnDay.cnt_confirmed}
                else {
                    newCasesList += (casesOnDay.cnt_confirmed - lastCases)
                    lastCases = casesOnDay.cnt_confirmed
                }
            }
        }
        getWeeklyAverage()
    }
}
package com.example.Ctrip.home

import android.content.Context
import com.example.Ctrip.model.City
import com.example.Ctrip.model.CitySelectionData
import com.example.Ctrip.model.LocationRegion
import com.example.Ctrip.home.DepartureSelectTabContract
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class DepartureSelectTabModel(private val context: Context) : DepartureSelectTabContract.Model {
    
    private val gson = Gson()
    private var cachedCities: List<City>? = null
    private val searchHistory = mutableListOf<City>()
    
    override fun getCitySelectionData(): CitySelectionData? {
        return try {
            val cities = loadCitiesFromAssets()
            
            val domesticCities = cities
                .filter { city -> !city.isInternational }
                .sortedBy { city -> city.firstLetter }
                .groupBy { city -> city.firstLetter }
                
            val internationalCities = cities
                .filter { city -> city.isInternational }
                .sortedBy { city -> city.firstLetter }
                .groupBy { city -> city.firstLetter }
                
            val hotCities = cities.filter { city -> city.isHot && !city.isInternational }
            
            CitySelectionData(
                history = searchHistory.toList(),
                hotCities = hotCities,
                domesticCities = domesticCities,
                internationalCities = internationalCities,
                isLocationEnabled = false
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun searchCities(query: String): List<City> {
        val cities = loadCitiesFromAssets()
        return cities.filter { city ->
            city.cityName.contains(query, ignoreCase = true) ||
            city.pinyin.contains(query, ignoreCase = true) ||
            city.cityCode.contains(query, ignoreCase = true) ||
            city.province.contains(query, ignoreCase = true)
        }
    }
    
    override fun getCitiesByRegion(region: LocationRegion): Map<String, List<City>> {
        val cities = loadCitiesFromAssets()
        return when (region) {
            LocationRegion.DOMESTIC -> {
                cities.filter { city -> !city.isInternational }
                    .sortedBy { city -> city.firstLetter }
                    .groupBy { city -> city.firstLetter }
            }
            LocationRegion.INTERNATIONAL -> {
                cities.filter { city -> city.isInternational }
                    .sortedBy { city -> city.firstLetter }
                    .groupBy { city -> city.firstLetter }
            }
        }
    }
    
    override fun updateSearchHistory(city: City) {
        searchHistory.removeAll { existingCity -> existingCity.cityId == city.cityId }
        searchHistory.add(0, city)
        if (searchHistory.size > 5) {
            searchHistory.removeAt(searchHistory.lastIndex)
        }
    }
    
    override fun getLocationStatus(): Boolean {
        return false // For now, return false as location is not enabled
    }
    
    private fun loadCitiesFromAssets(): List<City> {
        if (cachedCities != null) {
            return cachedCities!!
        }
        
        return try {
            val inputStream = context.assets.open("data/cities.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<City>>() {}.type
            val cities: List<City> = gson.fromJson(reader, type)
            cachedCities = cities
            reader.close()
            cities
        } catch (e: Exception) {
            emptyList()
        }
    }
}
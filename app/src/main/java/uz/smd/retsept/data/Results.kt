package uz.smd.retsept.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Created by Siddikov Mukhriddin on 9/1/21
 */
data class Results(
    val searchRes: List<SearchResult>?
)

data class SearchResult(
    val ID: String,
    val title: String,
//    val langCode: String,
//    val link: String,
)

fun fromSearchResult(value: List<SearchResult>): String {
    val gson = Gson()
    val type = object : TypeToken<List<SearchResult>>() {}.type
    return gson.toJson(value, type)
}

fun toSearchResult(value: String): MutableList<SearchResult> {
    val gson = Gson()
    val type = object : TypeToken<List<SearchResult>>() {}.type
    return gson.fromJson(value, type)
}
package com.mikel.filmaffinity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.URL

enum class VideoType {
    show,
    movie,
}

enum class VideoLanguage {
    en,
    es,
}


data class FilmInformation(
    val title: String,
    val url: URL,
    val year: Int,
    val type: VideoType,
    val language: VideoLanguage,
    val provider: String
)

data class FilmaffinitySearch(
    val results: List<FilmaffinitySearchResult> = emptyList(),
    val redirectURL: URL? = null
)

data class FilmaffinitySearchResult(
    val title: String,
    val type: VideoType,
    val id: String,
    val url: URL,
    val year: Int
)

@Parcelize
data class FilmaffinityRatting(
    val title: String,
    val url: URL,
    val id: String,
    val rating: Float?,
    val ratingOverview: FilmaffinityRatingOverview?
) : Parcelable

@Parcelize
data class FilmaffinityRatingOverview(
    val positive: FilmaffinityOverviewItem,
    val neutral: FilmaffinityOverviewItem,
    val negative: FilmaffinityOverviewItem
) : Parcelable

@Parcelize
data class FilmaffinityOverviewItem(
    val count: Int,
    val percentage: Float
) : Parcelable



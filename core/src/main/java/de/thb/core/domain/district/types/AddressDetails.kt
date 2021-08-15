package de.thb.core.domain.district.types

data class AddressDetails(
    val admin_level: Int,
    val `class`: String,
    val distance: Int,
    val isaddress: Boolean,
    val localname: String,
    val osm_id: Long,
    val osm_type: String,
    val place_id: Int,
    val place_type: Any,
    val rank_address: Int,
    val type: String
)
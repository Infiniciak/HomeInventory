package pl.edu.ur.bw131534.homeinventory.data.dao


data class InitialItemDto(
    val modelId: String = "",
    val serialNumber: String = "",
    val name: String = "",
    val description: String? = null,
    val price: Double? = 0.0,
    val category: String? = null,
    val location: String? = null
)

data class InitialWarrantyDto(
    val modelId: String,
    val expiryDate: String,
    val provider: String? = null,
    val notes: String? = null
)
package pl.edu.ur.bw131534.homeinventory.data.local.entity

import androidx.room.*

@Entity(tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"),Index("locationId")]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val modelId: String,
    val serialNumber: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val categoryId: Long?,
    val locationId: Long?,
    val dateAdded: String,
    val imageUri: String? = null,

)
package pl.edu.ur.bw131534.homeinventory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "warranties",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["itemId"])]
)
data class WarrantyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Long,
    val expiryDate: String,
    val provider: String? = null,
    val notes: String? = null
)
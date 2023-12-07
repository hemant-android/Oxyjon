package app.oxyjon.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "medicine")
class Medicine constructor(
    @field:PrimaryKey(autoGenerate = true) var id: Int,
    @field:ColumnInfo(name = "name") var medicineName: String,
    @field:ColumnInfo(name = "form") var medicineForm: String,
    @field:ColumnInfo(name = "category") var medicineCategory: String,
    @field:ColumnInfo(name = "created") var createdAt: String
)
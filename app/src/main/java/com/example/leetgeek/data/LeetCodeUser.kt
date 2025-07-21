import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class LeetCodeUser(
    val name: String = "",
    val username: String = "",
    val problems_solved: Map<String, Long> = emptyMap(),
    val last_submission: LastSubmission? = null,
    val rank : Int = 0,
    @kotlinx.serialization.Transient
    val last_updated: Timestamp? = null
)

@Serializable
data class LastSubmission(
    val title: String = "",
    val lang: String = "",
    val url: String = "",
    val timestamp: String = ""
)

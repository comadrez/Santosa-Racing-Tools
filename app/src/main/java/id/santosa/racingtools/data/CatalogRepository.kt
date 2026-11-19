package id.santosa.racingtools.data

import android.content.Context
import org.json.JSONObject

object CatalogRepository {
    private const val DIR = "specs"

    fun load(context: Context): Catalog {
        return try {
            val index = JSONObject(read(context, DIR + "/index.json"))
            val files = index.getJSONArray("files")
            val parts = List(files.length()) {
                CatalogParser.parse(read(context, DIR + "/" + files.getString(it)))
            }
            CatalogParser.merge(parts)
        } catch (e: Exception) {
            Catalog.EMPTY
        }
    }

    private fun read(context: Context, path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }
}

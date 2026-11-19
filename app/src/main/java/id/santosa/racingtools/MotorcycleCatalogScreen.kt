package id.santosa.racingtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.santosa.racingtools.data.CatalogRepository
import id.santosa.racingtools.data.SpecFormat

private data class MotorcycleModelEntry(
    val name: String,
    val category: String
)

/*
 * Initial model-family directory for Indonesia.
 *
 * This is deliberately NOT a factory-specification database. Exact model/type
 * codes, year ranges and generations are kept unavailable until supported by
 * model-specific documentation. Do not infer them from a family name.
 */
private val srtBrandModels: Map<String, List<MotorcycleModelEntry>> = mapOf(
    "Honda" to listOf(
        MotorcycleModelEntry("BeAT", "Scooter / Matic"),
        MotorcycleModelEntry("Vario", "Scooter / Matic"),
        MotorcycleModelEntry("Scoopy", "Scooter / Matic"),
        MotorcycleModelEntry("Genio", "Scooter / Matic"),
        MotorcycleModelEntry("PCX", "Scooter / Matic"),
        MotorcycleModelEntry("ADV", "Scooter / Matic"),
        MotorcycleModelEntry("Forza", "Scooter / Matic"),
        MotorcycleModelEntry("Supra", "Underbone / Cub"),
        MotorcycleModelEntry("Revo", "Underbone / Cub"),
        MotorcycleModelEntry("Blade", "Underbone / Cub"),
        MotorcycleModelEntry("Sonic 150R", "Underbone / Cub"),
        MotorcycleModelEntry("CB150R Streetfire", "Naked"),
        MotorcycleModelEntry("CB150X", "Adventure / Touring"),
        MotorcycleModelEntry("CBR150R", "Sport"),
        MotorcycleModelEntry("CBR250RR", "Sport"),
        MotorcycleModelEntry("CRF150L", "Trail / Enduro"),
        MotorcycleModelEntry("CRF250 Rally", "Trail / Enduro"),
        MotorcycleModelEntry("Tiger", "Naked"),
        MotorcycleModelEntry("MegaPro", "Naked"),
        MotorcycleModelEntry("Verza", "Naked"),
        MotorcycleModelEntry("GL Pro / GL Max", "Naked")
    ),
    "Yamaha" to listOf(
        MotorcycleModelEntry("Mio", "Scooter / Matic"),
        MotorcycleModelEntry("Mio Soul", "Scooter / Matic"),
        MotorcycleModelEntry("Fino", "Scooter / Matic"),
        MotorcycleModelEntry("Xeon", "Scooter / Matic"),
        MotorcycleModelEntry("X-Ride", "Scooter / Matic"),
        MotorcycleModelEntry("Gear", "Scooter / Matic"),
        MotorcycleModelEntry("FreeGo", "Scooter / Matic"),
        MotorcycleModelEntry("Fazzio", "Scooter / Matic"),
        MotorcycleModelEntry("Grand Filano", "Scooter / Matic"),
        MotorcycleModelEntry("NMAX", "Scooter / Matic"),
        MotorcycleModelEntry("Aerox", "Scooter / Matic"),
        MotorcycleModelEntry("Lexi", "Scooter / Matic"),
        MotorcycleModelEntry("XMAX", "Scooter / Matic"),
        MotorcycleModelEntry("Jupiter", "Underbone / Cub"),
        MotorcycleModelEntry("Vega", "Underbone / Cub"),
        MotorcycleModelEntry("Mio M3", "Scooter / Matic"),
        MotorcycleModelEntry("MX King", "Underbone / Cub"),
        MotorcycleModelEntry("Vixion", "Naked"),
        MotorcycleModelEntry("Byson", "Naked"),
        MotorcycleModelEntry("R15", "Sport"),
        MotorcycleModelEntry("R25", "Sport"),
        MotorcycleModelEntry("MT-15", "Naked"),
        MotorcycleModelEntry("MT-25", "Naked"),
        MotorcycleModelEntry("WR155R", "Trail / Enduro"),
        MotorcycleModelEntry("YZF-R6", "Sport"),
        MotorcycleModelEntry("TMAX", "Scooter / Matic")
    ),
    "Suzuki" to listOf(
        MotorcycleModelEntry("Nex", "Scooter / Matic"),
        MotorcycleModelEntry("Address", "Scooter / Matic"),
        MotorcycleModelEntry("Burgman", "Scooter / Matic"),
        MotorcycleModelEntry("Spin", "Scooter / Matic"),
        MotorcycleModelEntry("Skywave", "Scooter / Matic"),
        MotorcycleModelEntry("Satria", "Underbone / Cub"),
        MotorcycleModelEntry("Shogun", "Underbone / Cub"),
        MotorcycleModelEntry("Smash", "Underbone / Cub"),
        MotorcycleModelEntry("Thunder", "Naked"),
        MotorcycleModelEntry("GSX-R150", "Sport"),
        MotorcycleModelEntry("GSX-S150", "Naked"),
        MotorcycleModelEntry("GSX-8S", "Naked"),
        MotorcycleModelEntry("V-Strom", "Adventure / Touring"),
        MotorcycleModelEntry("DR-Z", "Trail / Enduro"),
        MotorcycleModelEntry("TS", "Trail / Enduro")
    ),
    "Kawasaki" to listOf(
        MotorcycleModelEntry("Ninja 150", "Sport"),
        MotorcycleModelEntry("Ninja 250", "Sport"),
        MotorcycleModelEntry("Ninja ZX-25R", "Sport"),
        MotorcycleModelEntry("Ninja ZX-4R", "Sport"),
        MotorcycleModelEntry("Ninja ZX-6R", "Sport"),
        MotorcycleModelEntry("Ninja ZX-10R", "Sport"),
        MotorcycleModelEntry("KLX150", "Trail / Enduro"),
        MotorcycleModelEntry("KLX230", "Trail / Enduro"),
        MotorcycleModelEntry("KLX250", "Trail / Enduro"),
        MotorcycleModelEntry("D-Tracker", "Trail / Enduro"),
        MotorcycleModelEntry("W175", "Naked"),
        MotorcycleModelEntry("W250", "Naked"),
        MotorcycleModelEntry("Versys", "Adventure / Touring"),
        MotorcycleModelEntry("Vulcan", "Cruiser"),
        MotorcycleModelEntry("Kaze", "Underbone / Cub"),
        MotorcycleModelEntry("KRR / Ninja RR", "Sport")
    ),
    "Vespa / Piaggio" to listOf(
        MotorcycleModelEntry("Vespa LX", "Scooter / Matic"),
        MotorcycleModelEntry("Vespa S", "Scooter / Matic"),
        MotorcycleModelEntry("Vespa Primavera", "Scooter / Matic"),
        MotorcycleModelEntry("Vespa Sprint", "Scooter / Matic"),
        MotorcycleModelEntry("Vespa GTS", "Scooter / Matic"),
        MotorcycleModelEntry("Vespa PX", "Scooter / Matic"),
        MotorcycleModelEntry("Piaggio Liberty", "Scooter / Matic"),
        MotorcycleModelEntry("Piaggio Medley", "Scooter / Matic"),
        MotorcycleModelEntry("Piaggio MP3", "Scooter / Matic")
    ),
    "TVS" to listOf(
        MotorcycleModelEntry("Dazz", "Scooter / Matic"),
        MotorcycleModelEntry("Ntorq", "Scooter / Matic"),
        MotorcycleModelEntry("RockZ", "Underbone / Cub"),
        MotorcycleModelEntry("Apache RTR", "Sport"),
        MotorcycleModelEntry("Ronin", "Naked"),
        MotorcycleModelEntry("XL100", "Underbone / Cub")
    ),
    "Bajaj" to listOf(
        MotorcycleModelEntry("Pulsar", "Naked"),
        MotorcycleModelEntry("Avenger", "Cruiser"),
        MotorcycleModelEntry("Dominar", "Naked"),
        MotorcycleModelEntry("Discover", "Underbone / Cub"),
        MotorcycleModelEntry("KTM Duke (Bajaj-era imports)", "Naked")
    ),
    "BMW Motorrad" to listOf(
        MotorcycleModelEntry("G 310 R", "Naked"),
        MotorcycleModelEntry("G 310 GS", "Adventure / Touring"),
        MotorcycleModelEntry("F 700 / F 750 GS", "Adventure / Touring"),
        MotorcycleModelEntry("F 800 GS", "Adventure / Touring"),
        MotorcycleModelEntry("F 850 GS", "Adventure / Touring"),
        MotorcycleModelEntry("R 1200 GS", "Adventure / Touring"),
        MotorcycleModelEntry("R 1250 GS", "Adventure / Touring"),
        MotorcycleModelEntry("R  nineT", "Naked"),
        MotorcycleModelEntry("S 1000 RR", "Sport"),
        MotorcycleModelEntry("R 18", "Cruiser")
    ),
    "KTM" to listOf(
        MotorcycleModelEntry("Duke 125 / 200 / 250 / 390", "Naked"),
        MotorcycleModelEntry("RC 200 / 250 / 390", "Sport"),
        MotorcycleModelEntry("Adventure 250 / 390", "Adventure / Touring"),
        MotorcycleModelEntry("EXC", "Trail / Enduro"),
        MotorcycleModelEntry("SX", "Trail / Enduro")
    ),
    "Husqvarna" to listOf(
        MotorcycleModelEntry("Svartpilen", "Naked"),
        MotorcycleModelEntry("Vitpilen", "Naked"),
        MotorcycleModelEntry("701 Enduro", "Trail / Enduro"),
        MotorcycleModelEntry("TE", "Trail / Enduro"),
        MotorcycleModelEntry("FE", "Trail / Enduro")
    ),
    "Royal Enfield" to listOf(
        MotorcycleModelEntry("Classic 350", "Naked"),
        MotorcycleModelEntry("Bullet", "Naked"),
        MotorcycleModelEntry("Meteor 350", "Cruiser"),
        MotorcycleModelEntry("Hunter 350", "Naked"),
        MotorcycleModelEntry("Himalayan", "Adventure / Touring"),
        MotorcycleModelEntry("Interceptor 650", "Naked"),
        MotorcycleModelEntry("Continental GT 650", "Sport")
    ),
    "Benelli" to listOf(
        MotorcycleModelEntry("TNT 135", "Naked"),
        MotorcycleModelEntry("TNT 249S", "Naked"),
        MotorcycleModelEntry("Leoncino", "Naked"),
        MotorcycleModelEntry("TRK 251 / 502", "Adventure / Touring"),
        MotorcycleModelEntry("Imperiale 400", "Naked")
    ),
    "Ducati" to listOf(
        MotorcycleModelEntry("Monster", "Naked"),
        MotorcycleModelEntry("Panigale", "Sport"),
        MotorcycleModelEntry("Streetfighter", "Naked"),
        MotorcycleModelEntry("Multistrada", "Adventure / Touring"),
        MotorcycleModelEntry("Scrambler", "Naked"),
        MotorcycleModelEntry("Diavel", "Cruiser")
    ),
    "Aprilia" to listOf(
        MotorcycleModelEntry("SR GT", "Scooter / Matic"),
        MotorcycleModelEntry("RS 660", "Sport"),
        MotorcycleModelEntry("RSV4", "Sport"),
        MotorcycleModelEntry("Tuono", "Naked"),
        MotorcycleModelEntry("Tuareg 660", "Adventure / Touring")
    ),
    "Triumph" to listOf(
        MotorcycleModelEntry("Bonneville", "Naked"),
        MotorcycleModelEntry("Street Triple", "Naked"),
        MotorcycleModelEntry("Speed Triple", "Naked"),
        MotorcycleModelEntry("Tiger", "Adventure / Touring"),
        MotorcycleModelEntry("Daytona", "Sport"),
        MotorcycleModelEntry("Scrambler", "Naked")
    ),
    "Harley-Davidson" to listOf(
        MotorcycleModelEntry("Sportster", "Cruiser"),
        MotorcycleModelEntry("Softail", "Cruiser"),
        MotorcycleModelEntry("Dyna", "Cruiser"),
        MotorcycleModelEntry("Touring", "Touring"),
        MotorcycleModelEntry("Pan America", "Adventure / Touring")
    ),
    "CFMOTO" to listOf(
        MotorcycleModelEntry("250NK", "Naked"),
        MotorcycleModelEntry("300SR", "Sport"),
        MotorcycleModelEntry("450SR", "Sport"),
        MotorcycleModelEntry("650MT", "Adventure / Touring"),
        MotorcycleModelEntry("800MT", "Adventure / Touring")
    ),
    "QJMotor" to listOf(
        MotorcycleModelEntry("SRK", "Naked"),
        MotorcycleModelEntry("SRV", "Cruiser"),
        MotorcycleModelEntry("SRT", "Adventure / Touring"),
        MotorcycleModelEntry("ATR", "Scooter / Matic")
    ),
    "SYM" to listOf(
        MotorcycleModelEntry("Jet", "Scooter / Matic"),
        MotorcycleModelEntry("Cruisym", "Scooter / Matic"),
        MotorcycleModelEntry("Maxsym", "Scooter / Matic"),
        MotorcycleModelEntry("Bonus", "Underbone / Cub")
    ),
    "Kymco" to listOf(
        MotorcycleModelEntry("Like", "Scooter / Matic"),
        MotorcycleModelEntry("K-XCT", "Scooter / Matic"),
        MotorcycleModelEntry("Downtown", "Scooter / Matic"),
        MotorcycleModelEntry("AK", "Scooter / Matic")
    ),
    "Viar" to listOf(
        MotorcycleModelEntry("Cross", "Trail / Enduro"),
        MotorcycleModelEntry("Vortex", "Adventure / Touring"),
        MotorcycleModelEntry("Star", "Underbone / Cub"),
        MotorcycleModelEntry("Q1", "Scooter / Matic")
    ),
    "Peugeot Motocycles" to listOf(
        MotorcycleModelEntry("Django", "Scooter / Matic"),
        MotorcycleModelEntry("Tweet", "Scooter / Matic"),
        MotorcycleModelEntry("Metropolis", "Scooter / Matic")
    )
)

@Composable
internal fun MotorcycleCatalogScreen() {
    val brands = remember { srtBrandModels.keys.sorted() }
    var brand by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var modelCode by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    val allModels = srtBrandModels[brand].orEmpty()
    val categories = allModels.map { it.category }.distinct().sorted()
    val categoryModels = allModels.filter { category.isBlank() || it.category == category }
    val context = LocalContext.current
    val catalog = remember { CatalogRepository.load(context) }
    val records = if (brand.isNotBlank() && model.isNotBlank()) catalog.generationsFor(brand, model) else emptyList()
    val selectedRecord = records.firstOrNull { it.label == modelCode }
    val modelCodes = if (records.isNotEmpty()) records.map { it.label } else listOf("Not yet verified — exact type/model code pending source")
    val years = selectedRecord?.let { SpecFormat.yearOptions(it, java.time.Year.now().value) }?.takeIf { it.isNotEmpty() }
        ?: listOf("Not yet verified — production-year mapping pending source")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Motorcycle catalogue")
        Text("Select the motorcycle in sequence. Each list is filtered by the selection before it.")

        SearchDropdown(
            label = "1. Manufacturer",
            options = brands,
            selected = brand,
            onSelected = {
                brand = it
                category = ""
                model = ""
                modelCode = ""
                year = ""
            }
        )
        SearchDropdown(
            label = "2. Motorcycle type",
            options = categories,
            selected = category,
            enabled = brand.isNotBlank(),
            onSelected = {
                category = it
                model = ""
                modelCode = ""
                year = ""
            }
        )
        SearchDropdown(
            label = "3. Model / series",
            options = categoryModels.map { it.name }.distinct().sorted(),
            selected = model,
            enabled = brand.isNotBlank() && category.isNotBlank(),
            onSelected = {
                model = it
                modelCode = ""
                year = ""
            }
        )
        SearchDropdown(
            label = "4. Generation / variant",
            options = modelCodes,
            selected = modelCode,
            enabled = model.isNotBlank(),
            onSelected = {
                modelCode = it
                year = ""
            }
        )
        SearchDropdown(
            label = "5. Production year",
            options = years,
            selected = year,
            enabled = modelCode.isNotBlank(),
            onSelected = { year = it }
        )

        if (model.isNotBlank()) {
            Text("Selected: $brand · $category · $model")
            if (selectedRecord != null) {
                SpecDetail(selectedRecord, catalog)
            } else if (records.isNotEmpty()) {
                Text("Choose a generation / variant to see its specifications.")
            } else {
                Text("Exact model/type code and year are not yet published for this entry. They will appear only after source verification.")
                Text("Generation: not assigned — requires model-year documentation.")
            }
        } else {
            Text("Choose a manufacturer, type and model to view its catalogue record.")
        }
        Text("Catalogue status: model-family index with " + catalog.generations.size + " sourced record(s). Models without a record have no verified specifications yet.")
    }
}

@Composable
private fun SearchDropdown(
    label: String,
    options: List<String>,
    selected: String,
    enabled: Boolean = true,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(selected) { mutableStateOf("") }
    val filtered = options.filter { it.contains(query, ignoreCase = true) }

    Column {
        OutlinedTextField(
            value = if (expanded) query else selected,
            onValueChange = {
                query = it
                expanded = true
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = false,
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = { expanded = !expanded },
                    enabled = enabled
                ) { Text("▼") }
            }
        )
        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            filtered.take(80).forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        query = ""
                        expanded = false
                    }
                )
            }
            if (filtered.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No matching entry") },
                    onClick = { expanded = false },
                    enabled = false
                )
            }
        }
    }
}

package id.santosa.racingtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.santosa.racingtools.calc.ClearanceMath
import id.santosa.racingtools.calc.EngineMath
import id.santosa.racingtools.calc.GearMath

private val Bg = Color(0xFF101214)
private val SurfaceDark = Color(0xFF1B2024)
private val SurfaceRaised = Color(0xFF252C31)
private val Accent = Color(0xFFE05A32)
private val TextMain = Color(0xFFF2F3F4)
private val TextMuted = Color(0xFF9BA5AC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SantosaApp() }
    }
}

@Composable
private fun SantosaApp() {
    var tab by remember { mutableStateOf(0) }
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            secondary = Color(0xFFFFA078),
            background = Bg,
            surface = SurfaceDark,
            onBackground = TextMain,
            onSurface = TextMain
        )
    ) {
        Scaffold(
            containerColor = Bg,
            bottomBar = {
                NavigationBar(containerColor = SurfaceDark, contentColor = TextMain) {
                    val items = listOf(
                        Triple("Home", Icons.Default.Home, 0),
                        Triple("Calculators", Icons.Default.Calculate, 1),
                        Triple("Motorcycles", Icons.Default.DirectionsBike, 2),
                        Triple("Workshop", Icons.Default.MenuBook, 3),
                        Triple("Settings", Icons.Default.Settings, 4)
                    )
                    items.forEach { (label, icon, index) ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                indicatorColor = SurfaceRaised,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (tab) {
                    0 -> HomeScreen(onCalculators = { tab = 1 })
                    1 -> CalculatorsScreen()
                    2 -> MotorcyclesScreen()
                    3 -> WorkshopScreen()
                    else -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Composable
private fun Eyebrow(text: String) {
    Text(text.uppercase(), color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = TextMain, fontSize = 22.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(SurfaceDark, MaterialTheme.shapes.medium).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
private fun HomeScreen(onCalculators: () -> Unit) {
    ScreenColumn {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Santosa Racing Tools",
                modifier = Modifier.size(76.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SectionTitle("Santosa Racing Tools")
                Text("Workshop engineering suite", color = TextMuted, fontSize = 14.sp)
            }
        }
        Panel {
            Eyebrow("START HERE")
            Text("Engine displacement", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text("Calculate bore, stroke and cylinder capacity with transparent inputs and formulas.", color = TextMuted)
            Button(onClick = onCalculators, colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)) {
                Text("Open calculators")
            }
        }
        Panel {
            Text("Project status", fontWeight = FontWeight.SemiBold)
            Text("Version 0.1.0", color = TextMuted)
            Text("Motorcycle specifications and workshop articles will be added in verified database releases.", color = TextMuted)
        }
        Panel {
            Text("Engineering note", fontWeight = FontWeight.SemiBold)
            Text("Calculated values are theoretical. Confirm component dimensions, clearances and operating limits with physical measurements before machining or assembly.", color = TextMuted)
        }
    }
}

@Composable
private fun CalculatorsScreen() {
    var selected by remember { mutableStateOf("Displacement") }
    ScreenColumn {
        Eyebrow("Calculation bench")
        SectionTitle("Calculators")
        Text("Select a calculation. Inputs use millimetres unless stated otherwise.", color = TextMuted)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                "Displacement", "Bore / stroke", "Compression", "Clearance",
                "Squish", "Piston speed", "Gear ratio", "Road speed"
            ).forEach { item ->
                FilterChip(
                    selected = selected == item,
                    onClick = { selected = item },
                    label = { Text(item) }
                )
            }
        }
        when (selected) {
            "Displacement" -> DisplacementCalculator()
            "Bore / stroke" -> BoreStrokeCalculator()
            "Compression" -> CompressionCalculator()
            "Clearance" -> ClearanceCalculator()
            "Squish" -> SquishCalculator()
            "Piston speed" -> PistonSpeedCalculator()
            "Road speed" -> RoadSpeedCalculator()
            else -> GearRatioCalculator()
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onValue: (String) -> Unit, suffix: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        suffix = { Text(suffix, color = TextMuted) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            focusedLabelColor = Accent,
            unfocusedBorderColor = Color(0xFF465057)
        )
    )
}

private fun parse(value: String): Double? = value.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 }

private fun parseNonNegative(value: String): Double? =
    value.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }

private fun parseSigned(value: String): Double? =
    value.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }

@Composable
private fun ResultPanel(title: String, result: String, note: String = "") {
    Panel {
        Eyebrow("RESULT")
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(result, color = Accent, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        if (note.isNotBlank()) Text(note, color = TextMuted, fontSize = 12.sp)
    }
}

@Composable
private fun DisplacementCalculator() {
    var bore by remember { mutableStateOf("52.4") }
    var stroke by remember { mutableStateOf("57.9") }
    var cylinders by remember { mutableStateOf("1") }
    val b = parse(bore)
    val s = parse(stroke)
    val n = cylinders.toIntOrNull()?.takeIf { it in 1..12 }
    val cc = if (b != null && s != null && n != null) EngineMath.displacementCc(b, s, n) else null

    Panel {
        Text("Displacement", fontWeight = FontWeight.SemiBold)
        NumberField("Bore", bore, { bore = it }, "mm")
        NumberField("Stroke", stroke, { stroke = it }, "mm")
        NumberField("Cylinders", cylinders, { cylinders = it }, "unit")
        Text("Formula: π ÷ 4 × bore² × stroke × cylinders ÷ 1,000", color = TextMuted, fontSize = 12.sp)
    }
    if (cc != null) ResultPanel("Calculated displacement", "%.2f cc".format(cc), "Theoretical swept volume.")
    else Text("Enter valid positive dimensions and 1–12 cylinders.", color = Color(0xFFFFB36B))
}

@Composable
private fun CompressionCalculator() {
    var swept by remember { mutableStateOf("124.0") }
    var clearance by remember { mutableStateOf("12.0") }
    val vs = parse(swept)
    val vc = parse(clearance)
    val ratio = if (vs != null && vc != null) EngineMath.staticCompressionRatio(vs, vc) else null
    Panel {
        Text("Static compression ratio", fontWeight = FontWeight.SemiBold)
        NumberField("Swept volume", swept, { swept = it }, "cc")
        NumberField("Total clearance volume", clearance, { clearance = it }, "cc")
        Text("Formula: (swept volume + clearance volume) ÷ clearance volume", color = TextMuted, fontSize = 12.sp)
    }
    if (ratio != null) ResultPanel("Static compression ratio", "%.2f : 1".format(ratio), "Use total clearance volume, including chamber, gasket, deck and piston crown contributions with the correct signs.")
    else Text("Enter valid positive volumes.", color = Color(0xFFFFB36B))
}

@Composable
private fun GearRatioCalculator() {
    var driving by remember { mutableStateOf("14") }
    var driven by remember { mutableStateOf("42") }
    var front by remember { mutableStateOf("14") }
    var rear by remember { mutableStateOf("42") }
    // The fields below are intentionally editable; unsupported values are not inferred.
    val a = parse(driving)
    val b = parse(driven)
    val f = parse(front)
    val r = parse(rear)
    val primary = if (a != null && b != null) GearMath.ratio(a, b) else null
    val final = if (f != null && r != null) GearMath.ratio(f, r) else null
    Panel {
        Text("Reduction ratios", fontWeight = FontWeight.SemiBold)
        NumberField("Driving teeth", driving, { driving = it }, "teeth")
        NumberField("Driven teeth", driven, { driven = it }, "teeth")
        NumberField("Front sprocket", front, { front = it }, "teeth")
        NumberField("Rear sprocket", rear, { rear = it }, "teeth")
        Text("Enter the actual tooth counts for each pair. This is a basic ratio tool, not a complete gearbox speed model.", color = TextMuted, fontSize = 12.sp)
    }
    if (primary != null && final != null) {
        ResultPanel("Primary / gear pair ratio", "%.4f".format(primary), "Final drive ratio: %.4f".format(final))
    } else {
        Text("Enter valid positive tooth counts.", color = Color(0xFFFFB36B))
    }
}

@Composable
private fun PistonSpeedCalculator() {
    var stroke by remember { mutableStateOf("57.9") }
    var rpm by remember { mutableStateOf("8000") }
    val st = parse(stroke)
    val rv = parse(rpm)
    val speed = if (st != null && rv != null) EngineMath.meanPistonSpeedMs(st, rv) else null
    Panel {
        Text("Mean piston speed", fontWeight = FontWeight.SemiBold)
        NumberField("Stroke", stroke, { stroke = it }, "mm")
        NumberField("Engine speed", rpm, { rpm = it }, "rpm")
        Text("Formula: 2 × stroke × rpm ÷ 60, with stroke in metres", color = TextMuted, fontSize = 12.sp)
    }
    if (speed != null) ResultPanel("Mean piston speed", "%.2f m/s".format(speed), "Average over one revolution.")
    else Text("Enter valid positive values.", color = Color(0xFFFFB36B))
}

@Composable
private fun BoreStrokeCalculator() {
    var boreText by remember { mutableStateOf("52.4") }
    var strokeText by remember { mutableStateOf("57.9") }
    var newBoreText by remember { mutableStateOf("54.0") }
    var newStrokeText by remember { mutableStateOf("57.9") }
    var cylinderText by remember { mutableStateOf("1") }
    val bore = parse(boreText)
    val stroke = parse(strokeText)
    val newBore = parse(newBoreText)
    val newStroke = parse(newStrokeText)
    val cylinders = cylinderText.toIntOrNull()?.takeIf { it in 1..EngineMath.MAX_CYLINDERS }
    val before = if (bore != null && stroke != null && cylinders != null) EngineMath.displacementCc(bore, stroke, cylinders) else null
    val after = if (newBore != null && newStroke != null && cylinders != null) EngineMath.displacementCc(newBore, newStroke, cylinders) else null
    val change = if (before != null && after != null) EngineMath.percentChange(before, after) else null

    Panel {
        Text("Bore-up / stroke-up", fontWeight = FontWeight.SemiBold)
        NumberField("Current bore", boreText, { boreText = it }, "mm")
        NumberField("Current stroke", strokeText, { strokeText = it }, "mm")
        NumberField("New bore", newBoreText, { newBoreText = it }, "mm")
        NumberField("New stroke", newStrokeText, { newStrokeText = it }, "mm")
        NumberField("Cylinders", cylinderText, { cylinderText = it }, "unit")
        Text("Keep a new value equal to the current one to change only the bore or only the stroke.", color = TextMuted, fontSize = 12.sp)
    }
    if (before != null && after != null && change != null) {
        ResultPanel(
            "New displacement",
            "%.2f cc".format(after),
            "Current %.2f cc · change %+.2f cc (%+.1f%%)".format(before, after - before, change)
        )
    } else {
        Text("Enter valid positive dimensions and 1–12 cylinders.", color = Color(0xFFFFB36B))
    }
}

@Composable
private fun ClearanceCalculator() {
    var boreText by remember { mutableStateOf("52.4") }
    var strokeText by remember { mutableStateOf("57.9") }
    var startText by remember { mutableStateOf("0.0") }
    var endText by remember { mutableStateOf("12.0") }
    var gasketText by remember { mutableStateOf("0.5") }
    var deckText by remember { mutableStateOf("0.0") }
    var crownText by remember { mutableStateOf("0.0") }
    val bore = parse(boreText)
    val stroke = parse(strokeText)
    val start = parseNonNegative(startText)
    val end = parse(endText)
    val gasketThickness = parseNonNegative(gasketText)
    val deckHeight = parseNonNegative(deckText)
    val crown = parseSigned(crownText)
    val chamber = if (start != null && end != null) ClearanceMath.buretteVolumeCc(start, end) else null
    val gasket = if (bore != null && gasketThickness != null) ClearanceMath.discVolumeCc(bore, gasketThickness) else null
    val deck = if (bore != null && deckHeight != null) ClearanceMath.discVolumeCc(bore, deckHeight) else null
    val total = if (chamber != null && gasket != null && deck != null && crown != null) ClearanceMath.totalCc(chamber, gasket, deck, crown) else null
    val swept = if (bore != null && stroke != null) EngineMath.displacementCc(bore, stroke, 1) else null
    val ratio = if (swept != null && total != null) EngineMath.staticCompressionRatio(swept, total) else null

    Panel {
        Text("Clearance volume", fontWeight = FontWeight.SemiBold)
        NumberField("Bore", boreText, { boreText = it }, "mm")
        NumberField("Stroke", strokeText, { strokeText = it }, "mm")
        NumberField("Burette start reading", startText, { startText = it }, "cc")
        NumberField("Burette end reading", endText, { endText = it }, "cc")
        NumberField("Compressed gasket thickness", gasketText, { gasketText = it }, "mm")
        NumberField("Piston below deck at TDC", deckText, { deckText = it }, "mm")
        NumberField("Piston crown volume", crownText, { crownText = it }, "cc")
        Text("Chamber = end reading − start reading. Crown volume is positive for a dish and negative for a dome. Values are for one cylinder.", color = TextMuted, fontSize = 12.sp)
    }
    if (total != null) {
        val note = if (ratio != null) "Static compression ratio: %.2f : 1 (per cylinder)".format(ratio) else ""
        ResultPanel("Total clearance volume", "%.2f cc".format(total), note)
    } else {
        Text("Enter valid values. The end reading must be above the start reading.", color = Color(0xFFFFB36B))
    }
}

@Composable
private fun SquishCalculator() {
    var deckText by remember { mutableStateOf("0.30") }
    var gasketText by remember { mutableStateOf("0.50") }
    val deck = parseSigned(deckText)
    val gasket = parseNonNegative(gasketText)
    val squish = if (deck != null && gasket != null) EngineMath.squishClearanceMm(deck, gasket) else null

    Panel {
        Text("Squish clearance", fontWeight = FontWeight.SemiBold)
        NumberField("Piston below deck at TDC", deckText, { deckText = it }, "mm")
        NumberField("Compressed gasket thickness", gasketText, { gasketText = it }, "mm")
        Text("Formula: piston below deck + compressed gasket thickness. Use a negative value if the piston stands above the deck.", color = TextMuted, fontSize = 12.sp)
    }
    if (squish != null) {
        ResultPanel("Squish clearance", "%.2f mm".format(squish), "Measure the gasket as installed. Check the allowed clearance with the engine builder or the manufacturer data.")
    } else {
        Text("Enter valid values. The clearance must be above zero.", color = Color(0xFFFFB36B))
    }
}

@Composable
private fun RoadSpeedCalculator() {
    var rpmText by remember { mutableStateOf("8000") }
    var primaryText by remember { mutableStateOf("3.0") }
    var gearText by remember { mutableStateOf("1.0") }
    var frontText by remember { mutableStateOf("14") }
    var rearText by remember { mutableStateOf("42") }
    var widthText by remember { mutableStateOf("80") }
    var aspectText by remember { mutableStateOf("90") }
    var rimText by remember { mutableStateOf("17") }
    val rpm = parse(rpmText)
    val primary = parse(primaryText)
    val gear = parse(gearText)
    val front = parse(frontText)
    val rear = parse(rearText)
    val width = parse(widthText)
    val aspect = parse(aspectText)
    val rim = parse(rimText)
    val circumference = if (width != null && aspect != null && rim != null) GearMath.tyreCircumferenceMm(width, aspect, rim) else null
    val speed = if (rpm != null && primary != null && gear != null && front != null && rear != null && circumference != null) {
        GearMath.roadSpeedKmh(rpm, primary, gear, front, rear, circumference)
    } else {
        null
    }

    Panel {
        Text("RPM to road speed", fontWeight = FontWeight.SemiBold)
        NumberField("Engine speed", rpmText, { rpmText = it }, "rpm")
        NumberField("Primary ratio", primaryText, { primaryText = it }, ": 1")
        NumberField("Gear ratio", gearText, { gearText = it }, ": 1")
        NumberField("Front sprocket", frontText, { frontText = it }, "teeth")
        NumberField("Rear sprocket", rearText, { rearText = it }, "teeth")
        NumberField("Tyre width", widthText, { widthText = it }, "mm")
        NumberField("Tyre aspect ratio", aspectText, { aspectText = it }, "%")
        NumberField("Rim diameter", rimText, { rimText = it }, "inch")
        Text("Tyre size as printed on the sidewall, for example 80/90-17. Get the primary and gear ratios from the Gear ratio calculator.", color = TextMuted, fontSize = 12.sp)
    }
    if (speed != null && circumference != null) {
        ResultPanel("Road speed", "%.1f km/h".format(speed), "Wheel circumference %.0f mm. Theoretical value: no slip and no tyre growth. Not valid for CVT drives.".format(circumference))
    } else {
        Text("Enter valid positive values.", color = Color(0xFFFFB36B))
    }
}

@Composable
private fun MotorcyclesScreen() {
    ScreenColumn {
        Eyebrow("Specification library")
        SectionTitle("Motorcycles")
        Panel {
            Text("Database", fontWeight = FontWeight.SemiBold)
            Text("The full Indonesian-market catalogue is not bundled yet. Models will only be published with generation-specific specifications and source/verification status.", color = TextMuted)
        }
        Panel {
            Text("Planned coverage", fontWeight = FontWeight.SemiBold)
            Text("Honda · Yamaha · Suzuki · Kawasaki · TVS · Vespa / Piaggio · KTM · BMW Motorrad · Benelli · Royal Enfield · Triumph · Harley-Davidson · CFMOTO · QJMotor and other documented Indonesian-market brands.", color = TextMuted)
        }
    }
}

@Composable
private fun WorkshopScreen() {
    ScreenColumn {
        Eyebrow("Technical library")
        SectionTitle("Workshop")
        listOf(
            "Measuring bore and piston clearance",
            "Burette measurement fundamentals",
            "Static compression ratio",
            "Squish clearance measurement",
            "Gear ratio fundamentals",
            "Pre-start inspection after engine assembly"
        ).forEach { topic ->
            Panel {
                Text(topic, fontWeight = FontWeight.SemiBold)
                Text("Article content is planned for a reviewed technical release.", color = TextMuted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SettingsScreen() {
    ScreenColumn {
        Eyebrow("Application")
        SectionTitle("Settings")
        Panel {
            Text("Santosa Racing Tools", fontWeight = FontWeight.SemiBold)
            Text("Version 0.1.0", color = TextMuted)
            Text("Language selection and persistent preferences will be implemented with the localization and settings module.", color = TextMuted)
        }
        Panel {
            Text("Calculation policy", fontWeight = FontWeight.SemiBold)
            Text("SI units internally. Results are theoretical unless explicitly identified as measured or verified.", color = TextMuted)
        }
    }
}

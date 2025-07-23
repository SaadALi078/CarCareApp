package com.example.carcare.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.carcare.data.model.Workshop
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapViewComposable(
    userLocation: GeoPoint,
    workshops: List<Workshop>,
    onMarkerClick: (Workshop) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                controller.setZoom(15.0)
                controller.setCenter(userLocation)

                // Add user's location marker
                val userMarker = Marker(this)
                userMarker.position = userLocation
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                userMarker.title = "You are here"
                overlays.add(userMarker)

                // Add mechanic workshop markers
                workshops.forEach { workshop ->
                    val marker = Marker(this).apply {
                        position = GeoPoint(workshop.latitude, workshop.longitude)
                        title = workshop.name
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            onMarkerClick(workshop)
                            true
                        }
                    }
                    overlays.add(marker)
                }
            }
        }
    )
}

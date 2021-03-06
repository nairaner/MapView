package com.peterlaurence.mapview.demo.fragments

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.peterlaurence.mapview.MapView
import com.peterlaurence.mapview.MapViewConfiguration
import com.peterlaurence.mapview.api.MinimumScaleMode
import com.peterlaurence.mapview.core.TileStreamProvider
import com.peterlaurence.mapview.demo.R
import com.peterlaurence.mapview.paths.PathPoint
import com.peterlaurence.mapview.paths.PathView
import com.peterlaurence.mapview.paths.addPathView
import com.peterlaurence.mapview.paths.toFloatArray
import java.io.InputStream

/**
 * An example showing the usage of paths.
 */
class MapPathFragment : Fragment() {
    private lateinit var parentView: ViewGroup

    /**
     * The [MapView] should always be added inside [onCreateView], to ensure state save/restore.
     * In this example, the configuration is done **immediately** after the [MapView] is added to
     * the view hierarchy.
     * But it's not mandatory, it could have been done later on.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout, container, false).also {
            parentView = it as ViewGroup
            makeMapView()?.addToFragment()
        }
    }

    private fun makeMapView(): MapView? {
        val context = context ?: return null
        val mapView = MapView(context)

        val tileStreamProvider = object : TileStreamProvider {
            override fun getTileStream(row: Int, col: Int, zoomLvl: Int): InputStream? {
                return try {
                    context.assets.open("tiles/esp/$zoomLvl/$row/$col.jpg")
                } catch (e: Exception) {
                    null
                }
            }
        }
        val config = MapViewConfiguration(
            5, 8192, 8192, 256, tileStreamProvider
        ).setMaxScale(2f).setMinScale(0.07f).setStartScale(0.4f)
            .setMinimumScaleMode(MinimumScaleMode.NONE)

        mapView.configure(config)

        mapView.defineBounds(0.0, 0.0, 1.0, 1.0)

        val pathView = PathView(context)
        mapView.addPathView(pathView)

        val pathList = listOfNotNull(
            listOf(
                PathPoint(0.2, 0.3), PathPoint(0.25, 0.15), PathPoint(0.32, 0.1),
                PathPoint(0.427, 0.212), PathPoint(0.6, 0.15), PathPoint(0.67, 0.1)
            ).toFloatArray(mapView),
            listOf(
                PathPoint(0.5, 0.5), PathPoint(0.55, 0.52), PathPoint(0.57, 0.54),
                PathPoint(0.59, 0.52), PathPoint(0.6, 0.51), PathPoint(0.59, 0.5),
                PathPoint(0.578, 0.447), PathPoint(0.46, 0.44), PathPoint(0.5, 0.5)
            ).toFloatArray(mapView)
        ).map {
            object : PathView.DrawablePath {
                override val visible: Boolean = true
                override var path: FloatArray = it
                override var paint: Paint? = null
                override val width: Float? = null
            }
        }

        pathView.updatePaths(pathList)

        return mapView
    }

    /**
     * Assign an id the the [MapView] (it's necessary to enable state save/restore).
     */
    private fun MapView.addToFragment() = apply {
        id = R.id.mapview_id
        isSaveEnabled = true

        parentView.addView(this, 0)
    }
}
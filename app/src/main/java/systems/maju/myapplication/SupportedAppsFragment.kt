package systems.maju.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_app_list.view.*

private const val DARK_APPS_TYPE = "DarkAppsType"

class SupportedAppsFragment : Fragment() {

    private var appTypeType: AppType = AppType.AUTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appTypeType = it.getSerializable(DARK_APPS_TYPE) as AppType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)
        view.app_list.apply {
            this@SupportedAppsFragment.context?.let { context ->
                layoutManager = GridLayoutManager(context, 2)
                adapter = RecyclerViewAdapter(context, RecyclerViewAdapter.Type.GRID).apply {

                    val allApps: MutableList<App> = AppList.getInstalledApps(context)
                    val autoSupportedApps = AppList.getList(context, AppType.AUTO)
                    val manualApps = AppList.getList(context, AppType.MANUAL)

                    val supportedApps = when (appTypeType) {
                        AppType.AUTO -> mergeLists(allApps, autoSupportedApps)
                        AppType.MANUAL -> mergeLists(allApps, manualApps)
                    }
                    addItems(supportedApps)
                }
            }
        }
        return view
    }


    fun mergeLists(
        installedApps: MutableList<App>,
        listedApps: MutableList<App>
    ): MutableList<App> {

        val result = installedApps.filter { installed ->
            listedApps.any { listed ->
                installed.packageName == listed.packageName
            }
        }.toMutableList()

        result.forEach { app ->
            val listedApp = listedApps.find { listed ->
                app.packageName == listed.packageName
            }

            app.website = listedApp?.website
            app.requiredAndroid = listedApp?.requiredAndroid
            app.requiredVersionCode = listedApp?.requiredVersionCode
        }

        return result
    }

    companion object {
        @JvmStatic
        fun newInstance(type: AppType) =
            SupportedAppsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DARK_APPS_TYPE, type)
                }
            }
    }
}

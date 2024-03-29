package com.codelixir.retrofit.ui

import com.codelixir.retrofit.util.RomUtils
import com.codelixir.retrofit.util.RomUtils.askIgnoreBatteryOptimization
import com.codelixir.retrofit.util.RomUtils.askMiuiIgnoreBatteryOptimization
import com.codelixir.retrofit.util.RomUtils.hasBatteryOptimization
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.codelixir.retrofit.Application
import com.codelixir.retrofit.NavGraphDirections
import com.codelixir.retrofit.R
import com.codelixir.retrofit.data.GitHubViewModel
import com.codelixir.retrofit.data.Resource
import com.codelixir.retrofit.databinding.ActivityMainBinding
import com.codelixir.retrofit.util.toast
import dagger.hilt.android.AndroidEntryPoint
import org.funktionale.currying.curried

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel by viewModels<GitHubViewModel>()

    override fun getViewBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)
    override fun getToolbar(): Toolbar = binding.toolbar
    override fun getToolbarTitle(): TextView = binding.tvToolbarTitle
    override fun getNavController() = findNavController(R.id.nav_host_fragment)

    override fun getProgressView(): View = binding.progressBar.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appBarConfiguration =
            AppBarConfiguration
                .Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()

        binding.toolbar.setupWithNavController(getNavController(), appBarConfiguration)

        val count = Application.getSetting("worker", 0)

        Toast.makeText(this, "Worker Run Count: $count", Toast.LENGTH_SHORT).show()

        val add = { x: Int, y: Int -> { x + y } }.curried()
        add(4)(5)

        binding.btnNew.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }

        binding.tvToolbarTitle.setOnClickListener {
            toast(this, "Optimization:" + hasBatteryOptimization().toString())
        }

        binding.btnKill.setOnClickListener {
            finish()
            moveTaskToBack(true)
            System.exit(0)
        }

        binding.btnR.setOnClickListener {
            refreshData()
        }

        binding.btnB.setOnClickListener {
            navigateTo(NavGraphDirections.actionToGlobalBlank("Blank from Activity"))
        }

        binding.btnC.setOnClickListener {
            cleanNavigateTo(NavGraphDirections.actionToGlobalBlank("Blank from Activity"))
        }

        val graph = getNavController().navInflater.inflate(R.navigation.nav_graph)
        getNavController().graph = graph

        binding.rootScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.tv1.text = scrollY.toString()
        })

        checkBatteryOptimization()
    }

    private fun checkBatteryOptimization() {
        if (hasBatteryOptimization()) {
            val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                toast(this, "Optimization: " + hasBatteryOptimization().toString())
            }

            if (RomUtils.isMiui())
                askMiuiIgnoreBatteryOptimization(startForResult)
            else
                askIgnoreBatteryOptimization(startForResult)
        }


    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home && getNavController().backQueue.size < 1) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun refreshData() {
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show()
        viewModel.getSharedRepositories()
            .observe(this) { out ->
                if (out.status == Resource.Status.SUCCESS) {
                    out.data?.let {
                        Toast.makeText(this, "Total items: ${it.size}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IGNORE_BATTERY_OPTIMIZATIONS_REQUEST_CODE) {
            toast(this, "Optimization: " + hasBatteryOptimization().toString())
        }
    }*/

}

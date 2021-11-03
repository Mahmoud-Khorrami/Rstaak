package com.example.rstaak.general

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.rstaak.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Named
import kotlin.math.log

/**
 * Ported from: https://github.com/android/architecture-components-samples/blob/master/NavigationAdvancedSample
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */

class BottomNavManager @AssistedInject constructor(@Assisted private val fragmentManager: FragmentManager, @Named("container id") private val containerId: Int, @Assisted private val bottomNavigationView: BottomNavigationView)
{

  @AssistedFactory
  interface BottomNavManagerFactory
  {
    fun create(fragmentManager: FragmentManager, bottomNavigationView: BottomNavigationView): BottomNavManager
  }

  companion object
  {
    private const val KEY_NAV_HISTORY = "nav_history"
  }

  // Graph Id's of the tabs
  private val navGraphIds = listOf(R.navigation.others_nav,R.navigation.chat_nav,R.navigation.my_shop_nav,R.navigation.shops_nav, R.navigation.products_nav,   )

  // Map of tags
  private val graphIdToTagMap = SparseArray<String>()

  // holds the start destination of all the graphs with their bottomNavigationView item id, used for back press
  private var navGraphStartDestinations = mutableMapOf<Int, Int>()

  private var navHistory = BottomNavHistory().apply { push(R.id.navigation_products) }

  private var selectedNavController: NavController? = null

  fun onBottomNavChanged(listener: NavController.OnDestinationChangedListener)
  {
    selectedNavController?.addOnDestinationChangedListener(listener)
  }

  init
  {
    setupNavController()
  }

  fun setupNavController()
  {
    navGraphStartDestinations.clear()
    graphIdToTagMap.clear()

    // create a NavHostFragment for each NavGraph ID
    createNavHostFragmentsForGraphs()

    // When a navigation item is selected
    bottomNavigationView.setupItemClickListener()
  }

  private fun createNavHostFragmentsForGraphs()
  {
    // create a NavHostFragment for each NavGraph ID
    navGraphIds.forEachIndexed { index, navGraphId ->
      val fragmentTag = getFragmentTag(index)

      // Find or create the Navigation host fragment
      val navHostFragment = obtainNavHostFragment(fragmentTag, navGraphId)

      // Obtain its id
      val graphId = navHostFragment.navController.graph.id
      navGraphStartDestinations[graphId] = navHostFragment.navController.graph.startDestinationId

      // Save to the map
      graphIdToTagMap[graphId] = fragmentTag

      // Attach or detach nav host fragment depending on whether it's the selected item.
      if(bottomNavigationView.selectedItemId == graphId)
      {
        // Update nav controller with the selected graph
        selectedNavController = navHostFragment.navController
        showNavHostFragment(navHostFragment, true)
      } else
      {
        showNavHostFragment(navHostFragment, false)
      }
    }
  }

  private fun BottomNavigationView.setupItemClickListener()
  {
    menu.forEach { item ->
      item.setOnMenuItemClickListener {

        // do nothing on tab re-selection
        if(item.isChecked)
        {
          return@setOnMenuItemClickListener true
        }

        if(!fragmentManager.isStateSaved)
        {
          item.isChecked = true
          navHistory.push(item.itemId)

          val newlySelectedItemTag = graphIdToTagMap[item.itemId]
          val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment

          fragmentManager.beginTransaction().show(selectedFragment)
            .setMaxLifecycle(selectedFragment, Lifecycle.State.RESUMED)
            .setPrimaryNavigationFragment(selectedFragment).apply {
              // Detach all other Fragments
              graphIdToTagMap.forEach { _, fragmentTag ->
                if(fragmentTag != newlySelectedItemTag)
                {
                  val fragment = fragmentManager.findFragmentByTag(fragmentTag)!!
                  hide(fragment)
                  setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                }
              }
            }.commit()

          selectedNavController = selectedFragment.navController
          visibility()
        }

        true
      }
    }
  }

  fun visibility()
  {
    selectedNavController?.addOnDestinationChangedListener { _, destination, _ ->
      Log.i("TAG", "visibility: ${destination.id}")
      when(destination.id)
      {
        R.id.fragment0 -> bottomNavigationView.visibility = View.GONE
        R.id.fragment1 -> bottomNavigationView.visibility = View.GONE
        R.id.fragment2 -> bottomNavigationView.visibility = View.GONE
        R.id.fragment3 -> bottomNavigationView.visibility = View.VISIBLE
        R.id.F4Shops -> bottomNavigationView.visibility = View.VISIBLE
        R.id.f5MyShop -> bottomNavigationView.visibility = View.VISIBLE
        R.id.f6Chats -> bottomNavigationView.visibility = View.VISIBLE
        R.id.fragment7 -> bottomNavigationView.visibility = View.VISIBLE
        R.id.fragment8 -> bottomNavigationView.visibility = View.GONE
        R.id.fragment9 -> bottomNavigationView.visibility = View.GONE
        R.id.F41ShopDetails -> bottomNavigationView.visibility = View.GONE
        R.id.f61Chat -> bottomNavigationView.visibility = View.GONE
      }
    }
  }

  // select particular bottom navigation item
  private fun selectItem(itemId: Int)
  {
    bottomNavigationView.menu.findItem(itemId)?.let {
        bottomNavigationView.menu.performIdentifierAction(itemId, 0)
      }
  }

  // controls the back press mechanism
  fun onBackPressed(): Boolean
  {
    return if(navHistory.isNotEmpty)
    {
      selectedNavController?.let {
        if(it.currentDestination == null || it.currentDestination?.id == navGraphStartDestinations[bottomNavigationView.selectedItemId])
        {
          if(isFirstTab()) return false

          navHistory.pop(bottomNavigationView.selectedItemId)
          selectItem(navHistory.current())
          return true
        }
        return false // super.onBackPressed() will be called, which will pop the fragment itself
      } ?: false
    } else false
  }

  // to save the tab history during any configuration change
  fun onSaveInstanceState(outState: Bundle?)
  {
    outState?.putParcelable(KEY_NAV_HISTORY, navHistory)
  }

  // to restore the tab history after any configuration change
  fun onRestoreInstanceState(savedInstanceState: Bundle?)
  {
    savedInstanceState?.let {
      navHistory = it.getParcelable<BottomNavHistory>(KEY_NAV_HISTORY) as BottomNavHistory
    }
  }

  // gets the NavHostFragment for particular index
  fun getNavHostFragment(index: Int): NavHostFragment?
  {
    return fragmentManager.findFragmentByTag(getFragmentTag(index)) as NavHostFragment?
  }

  private fun isFirstTab(): Boolean
  {
    return bottomNavigationView.selectedItemId == R.id.navigation_products
  }

  private fun showNavHostFragment(navHostFragment: NavHostFragment, show: Boolean)
  {
    fragmentManager.beginTransaction().apply {
        if(show)
        {
          show(navHostFragment)
          setMaxLifecycle(navHostFragment, Lifecycle.State.RESUMED)
        } else
        {
          hide(navHostFragment)
          setMaxLifecycle(navHostFragment, Lifecycle.State.STARTED)
        }
      }.commitNow()
  }

  private fun obtainNavHostFragment(fragmentTag: String, navGraphId: Int): NavHostFragment
  {
    // If the Nav Host fragment exists, return it
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
    existingFragment?.let { return it }

    // Otherwise, create it and return it.
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction().add(containerId, navHostFragment, fragmentTag).commitNow()
    return navHostFragment
  }

  private fun FragmentManager.isOnBackStack(backStackName: String): Boolean
  {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount)
    {
      if(getBackStackEntryAt(index).name == backStackName)
      {
        return true
      }
    }
    return false
  }

  private fun getFragmentTag(index: Int) = "BottomNavManager#$index"
}
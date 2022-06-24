//package com.haallo.ui.home.view;
//
//import static com.haallo.ui.home.view.FragmentHomeContainer.BACK_STACK_ROOT_NAME;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.haallo.R;
//import com.haallo.application.HaalloApplication;
//import com.haallo.base.RxBus;
//import com.haallo.base.RxEvent;
//import com.haallo.base.helper.OnBackPressListener;
//import com.haallo.ui.home.HomeActivity;
//
//import java.util.HashMap;
//
//import timber.log.Timber;
//
//public class HomeTabManager {
//
//    private final HomeActivity homeActivity;
//    private final HaalloTabBarView tabBar;
//    private final ViewPager2 viewPager;
//    private final FragmentStateAdapter viewPagerAdapter;
//
//    public HomeTabManager(HomeActivity activity, @Nullable Bundle savedInstanceState) {
//        homeActivity = activity;
//
//        HaalloApplication.component.inject(this);
//
//        tabBar = homeActivity.findViewById(R.id.tabBar);
//        tabBar.setOnTabItemClickListener(tabType -> {
//            selectTab(tabType, true);
//        });
//        viewPager = homeActivity.findViewById(R.id.viewPager);
//        viewPager.setUserInputEnabled(false);
//        viewPager.setOffscreenPageLimit(2);
//        viewPagerAdapter = new FragmentStateAdapter(homeActivity.getSupportFragmentManager(), homeActivity.getLifecycle()) {
//            @Override
//            public int getItemCount() {
//                return 5;
//            }
//
//            private final HashMap<Integer, Fragment> fragments = new HashMap<>();
//
//            @NonNull
//            @Override
//            public Fragment createFragment(int position) {
//                if (!fragments.containsKey(position) || fragments.get(position) == null) {
//                    FragmentHomeContainer homeContainer = FragmentHomeContainer.create(position);
//                    fragments.put(position, homeContainer);
//                }
//                return fragments.get(position);
//            }
//        };
//        viewPager.setAdapter(viewPagerAdapter);
//        tabBar.setActivatedTab(HaalloTabBarView.TAB_CHAT);
//    }
//
//    public void selectTab(@HaalloTabBarView.TabType int tabType, boolean actionByUser) {
//        tabBar.setActivatedTab(tabType);
//        if (actionByUser && tabType == viewPager.getCurrentItem()) {
//            //tap the one which has been selected
//            Fragment currentSelectedFragmentContainer = viewPagerAdapter.createFragment(tabType);
//            if (currentSelectedFragmentContainer.isAdded()) {
//                // try to pop up all the top fragments
//                if (currentSelectedFragmentContainer.getChildFragmentManager().getBackStackEntryCount() > 1) {
//                    currentSelectedFragmentContainer.getChildFragmentManager().popBackStack(BACK_STACK_ROOT_NAME, 0);
//                }
//            }
//        } else {
//            viewPager.setCurrentItem(tabType, false);
//        }
//        RxBus.INSTANCE.publish(new RxEvent.HomeTabChangeEvent(tabType));
//    }
//
//    public @HaalloTabBarView.TabType
//    int getActivatedTab() {
//        return tabBar.getActivatedTab();
//    }
//
//    public void addChildFragment(@HaalloTabBarView.TabType int tabType, final Fragment targetFragment, boolean withAnimation) {
//        Fragment fragment = viewPagerAdapter.createFragment(tabType);
//        if (fragment instanceof FragmentHomeContainer) {
//            FragmentHomeContainer homeContainer = (FragmentHomeContainer) fragment;
//            homeContainer.addChildFragment(targetFragment, withAnimation);
//        } else {
//            Timber.e("Something wrong with the base container fragment in viewpager");
//        }
//        tabBar.setActivatedTab(tabType);
//    }
//
//    public void addFragmentToCurrentTab(Fragment fragment, boolean withAnimation) {
//        addChildFragment(viewPager.getCurrentItem(), fragment, withAnimation);
//    }
//
//    public boolean onBackPressed() {
//        Fragment fragment = viewPagerAdapter.createFragment(viewPager.getCurrentItem());
//        if (fragment != null && fragment instanceof OnBackPressListener) {
//            return ((OnBackPressListener) fragment).onBackPressed();
//        } else {
//            return false;
//        }
//    }
//}

//package com.haallo.ui.home.view;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.haallo.R;
//import com.haallo.base.BaseFragment;
//import com.haallo.base.helper.OnBackPressListener;
//import com.haallo.ui.home.call.HomeCallFragmentOld;
//import com.haallo.ui.home.camera.HomeCameraFragmentOld;
//import com.haallo.ui.home.chat.HomeChatFragment;
//import com.haallo.ui.home.setting.HomeSettingFragment;
//import com.haallo.ui.home.status.HomeStatusFragmentOld;
//import com.haallo.util.UiUtils;
//
//import java.util.List;
//
//import timber.log.Timber;
//
//public class FragmentHomeContainer extends BaseFragment implements OnBackPressListener {
//
//    public static final String BACK_STACK_ROOT_NAME = "com.haallo.ui" + ".HOME_CONTAINER_BACK_STACK_ROOT_NAME";
//    private static final String BUNDLE_KEY_TAB_TYPE = "BUNDLE_KEY_TAB_TYPE";
//
//    //Now this is only used for launching ActivityHome by notification and we need to start another
//    //fragment on top of root fragment.
//    private Fragment mPendingFragment;
//
//    public static FragmentHomeContainer create(@HaalloTabBarView.TabType int homeTabType) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(BUNDLE_KEY_TAB_TYPE, homeTabType);
//        FragmentHomeContainer fragmentHomeContainer = new FragmentHomeContainer();
//        fragmentHomeContainer.setArguments(bundle);
//        return fragmentHomeContainer;
//    }
//
//    @Override
//    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home_container, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (getArguments() != null) {
//            @HaalloTabBarView.TabType int tabType = getArguments().getInt(BUNDLE_KEY_TAB_TYPE);
//            Fragment defaultRootFragment = null;
//            switch (tabType) {
//                case HaalloTabBarView.TAB_CHAT:
//                    defaultRootFragment = HomeChatFragment.newInstance();
//                    break;
//                case HaalloTabBarView.TAB_STORY:
//                    defaultRootFragment = HomeStatusFragmentOld.newInstance();
//                    break;
//                case HaalloTabBarView.TAB_CAMERA:
//                    defaultRootFragment = HomeCameraFragmentOld.newInstance();
//                    break;
//                case HaalloTabBarView.TAB_CALL:
//                    defaultRootFragment = HomeCallFragmentOld.newInstance();
//                    break;
//                case HaalloTabBarView.TAB_SETTING:
//                    defaultRootFragment = HomeSettingFragment.newInstance();
//                    break;
//            }
//
//            if (defaultRootFragment != null) {
//                getChildFragmentManager().beginTransaction()
//                        .add(R.id.child_fragment_container, defaultRootFragment)
//                        .addToBackStack(BACK_STACK_ROOT_NAME)
//                        .commitAllowingStateLoss();
//            }
//
//            if (mPendingFragment != null) {
//                addChildFragment(mPendingFragment, defaultRootFragment);
//                mPendingFragment = null;
//            }
//        }
//    }
//
//    public void addChildFragment(Fragment fragment, boolean withAnimation) {
//        addChildFragment(fragment, null, withAnimation);
//    }
//
//    public void addChildFragment(Fragment fragment, Fragment forceHiddenFragment) {
//        addChildFragment(fragment, forceHiddenFragment, true);
//    }
//
//    public void addChildFragment(Fragment fragment, Fragment forceHiddenFragment, boolean withAnimation) {
//        if (fragment == null) {
//            return;
//        }
//        if (!isAdded()) {
//            mPendingFragment = fragment;
//            return;
//        }
//        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
////        if (withAnimation) {
////            fragmentTransaction.setCustomAnimations(
////                    R.anim.fade_in,
////                    R.anim.fade_out,
////                    R.anim.fade_in,
////                    R.anim.fade_out
////            );
////        }
//
//        if (forceHiddenFragment == null) {
//            //If force hidden fragment is null, we hide the currently shown fragment.
//            forceHiddenFragment = getChildFragmentManager().findFragmentById(R.id.child_fragment_container);
//        }
//
//        if (forceHiddenFragment != null) {
//            fragmentTransaction.hide(forceHiddenFragment);
//        }
//        fragmentTransaction
//                .add(R.id.child_fragment_container, fragment)
//                .addToBackStack(fragment.getClass().getName())
//                .commitAllowingStateLoss();
//        UiUtils.hideKeyboard(getContext());
//    }
//
//    /**
//     * We need to manually call onHiddenChanged of child fragment. Otherwise, child fragments will
//     * not be aware of its hidden state.
//     *
//     * @param hidden
//     */
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!isAdded()) {
//            return;
//        }
//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        if (fragments != null) {
//            for (Fragment fragment : fragments) {
//                fragment.onHiddenChanged(hidden);
//            }
//        }
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (!isAdded()) {
//            return;
//        }
//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        if (fragments != null) {
//            for (Fragment fragment : fragments) {
//                fragment.setUserVisibleHint(isVisibleToUser);
//            }
//        }
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        super.onBackPressed();
//        if (!isAdded()) {
//            return true;
//        }
//
//        FragmentManager fragmentManager = getChildFragmentManager();
//        Fragment currentTopFragment = UiUtils.findTopFragment(fragmentManager, R.id.child_fragment_container);
//        if (currentTopFragment != null && currentTopFragment instanceof OnBackPressListener) {
//            if (((OnBackPressListener) currentTopFragment).onBackPressed()) {
//                return true;
//            }
//        }
//
//        if (fragmentManager.getBackStackEntryCount() > 1) {
//            try {
//                fragmentManager.popBackStack();
//            } catch (IllegalStateException e) {
//                Timber.e(e, "Failed to pop back stack");
//            }
//            return true;
//        }
//        return false;
//    }
//}
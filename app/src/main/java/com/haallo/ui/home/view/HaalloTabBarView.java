//package com.haallo.ui.home.view;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import androidx.annotation.AttrRes;
//import androidx.annotation.IntDef;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.annotation.StyleRes;
//import androidx.appcompat.widget.AppCompatImageView;
//import androidx.appcompat.widget.AppCompatTextView;
//import androidx.core.content.ContextCompat;
//
//import com.haallo.R;
//
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
//import timber.log.Timber;
//
//public class HaalloTabBarView extends FrameLayout {
//
//    public static final String INTENT_KEY_DEFAULT_TAB = "INTENT_KEY_DEFAULT_TAB";
//    public static final int TAB_CHAT = 0;
//    public static final int TAB_STORY = 1;
//    public static final int TAB_CAMERA = 2;
//    public static final int TAB_CALL = 3;
//    public static final int TAB_SETTING = 4;
//
//    private View llChat;
//    private AppCompatImageView ivChat;
//    private AppCompatTextView tvChat;
//
//    private View llStory;
//    private AppCompatImageView ivStory;
//    private AppCompatTextView tvStory;
//
//    private View llCamera;
//    private AppCompatImageView ivCamera;
//    private AppCompatTextView tvCamera;
//
//    private View llCall;
//    private AppCompatImageView ivCall;
//    private AppCompatTextView tvCall;
//
//    private View llSetting;
//    private AppCompatImageView ivSetting;
//    private AppCompatTextView tvSetting;
//
//    private int currentTab;
//
//    private TabBarItemClickListener mListener;
//
//    private int activeTabColor;
//    private int inActiveTabColor;
//
//    public HaalloTabBarView(@NonNull Context context) {
//        super(context);
//        init(context, null);
//    }
//
//    public HaalloTabBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    public HaalloTabBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public HaalloTabBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context, attrs);
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        inflate(context, R.layout.haallo_tab_bar_view, this);
//
//        activeTabColor = ContextCompat.getColor(context, R.color.dark_sky_blue);
//        inActiveTabColor = ContextCompat.getColor(context, R.color.color_737373);
//
//        llChat = findViewById(R.id.llChat);
//        ivChat = findViewById(R.id.ivChat);
//        tvChat = findViewById(R.id.tvChat);
//
//        llStory = findViewById(R.id.llStory);
//        ivStory = findViewById(R.id.ivStory);
//        tvStory = findViewById(R.id.tvStory);
//
//        llCamera = findViewById(R.id.llCamera);
//        ivCamera = findViewById(R.id.ivCamera);
//        tvCamera = findViewById(R.id.tvCamera);
//
//        llCall = findViewById(R.id.llCall);
//        ivCall = findViewById(R.id.ivCall);
//        tvCall = findViewById(R.id.tvCall);
//
//        llSetting = findViewById(R.id.llSetting);
//        ivSetting = findViewById(R.id.ivSetting);
//        tvSetting = findViewById(R.id.tvSetting);
//
//        llChat.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onTabBarItemClicked(TAB_CHAT);
//            }
//        });
//
//        llStory.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onTabBarItemClicked(TAB_STORY);
//            }
//        });
//
//        llCamera.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onTabBarItemClicked(TAB_CAMERA);
//            }
//        });
//
//        llCall.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onTabBarItemClicked(TAB_CALL);
//            }
//        });
//
//        llSetting.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onTabBarItemClicked(TAB_SETTING);
//            }
//        });
//    }
//
//    public void setOnTabItemClickListener(TabBarItemClickListener listener) {
//        mListener = listener;
//    }
//
//    public @TabType
//    int getActivatedTab() {
//        return currentTab;
//    }
//
//    public void setActivatedTab(@TabType int tabType) {
//        currentTab = tabType;
//        llChat.setActivated(tabType == TAB_CHAT);
//        llStory.setActivated(tabType == TAB_STORY);
//        llCamera.setActivated(tabType == TAB_CAMERA);
//        llCall.setActivated(tabType == TAB_CALL);
//        llSetting.setActivated(tabType == TAB_SETTING);
//
//        ivChat.setImageResource(R.drawable.chat_icon_white);
//        tvChat.setTextColor(inActiveTabColor);
//
//        ivStory.setImageResource(R.drawable.story);
//        tvStory.setTextColor(inActiveTabColor);
//
//        ivCamera.setImageResource(R.drawable.camera_disable);
//        tvCamera.setTextColor(inActiveTabColor);
//
//        ivCall.setImageResource(R.drawable.call_disable);
//        tvCall.setTextColor(inActiveTabColor);
//
//        ivSetting.setImageResource(R.drawable.settings);
//        tvSetting.setTextColor(inActiveTabColor);
//
//        switch (tabType) {
//            case TAB_CHAT:
//                ivChat.setImageResource(R.drawable.chats_icon_blue);
//                tvChat.setTextColor(activeTabColor);
//                break;
//            case TAB_STORY:
//                ivStory.setImageResource(R.drawable.story_blue);
//                tvStory.setTextColor(activeTabColor);
//                break;
//            case TAB_CAMERA:
//                ivCamera.setImageResource(R.drawable.camera_full_blue);
//                tvCamera.setTextColor(activeTabColor);
//                break;
//            case TAB_CALL:
//                ivCall.setImageResource(R.drawable.calls_blue);
//                tvCall.setTextColor(activeTabColor);
//                break;
//            case TAB_SETTING:
//                ivSetting.setImageResource(R.drawable.settings_a);
//                tvSetting.setTextColor(activeTabColor);
//                break;
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState newState = new SavedState(superState);
//        newState.currentPosition = currentTab;
//        return newState;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        SavedState savedState = (SavedState) state;
//        try {
//            super.onRestoreInstanceState(state);
//        } catch (Exception e) {
//            Timber.w(e, "Different Android OS codes has different codes at this place which might cause crash.");
//        }
//        currentTab = savedState.currentPosition;
//        setActivatedTab(currentTab);
//    }
//
//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({TAB_CHAT, TAB_STORY, TAB_CAMERA, TAB_CALL, TAB_SETTING})
//    public @interface TabType {
//    }
//
//    public interface TabBarItemClickListener {
//        void onTabBarItemClicked(@TabType int tabType);
//    }
//
//    static class SavedState extends BaseSavedState {
//
//        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
//
//            @Override
//            public SavedState createFromParcel(Parcel source) {
//                return new SavedState(source);
//            }
//
//            @Override
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//
//        int currentPosition;
//
//        public SavedState(Parcel source) {
//            super(source);
//            currentPosition = source.readInt();
//        }
//
//        public SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        @Override
//        public void writeToParcel(Parcel out, int flags) {
//            super.writeToParcel(out, flags);
//            out.writeInt(currentPosition);
//        }
//    }
//}

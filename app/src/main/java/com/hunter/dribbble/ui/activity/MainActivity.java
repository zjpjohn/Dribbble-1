package com.hunter.dribbble.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hunter.dribbble.App;
import com.hunter.dribbble.AppConstants;
import com.hunter.dribbble.R;
import com.hunter.dribbble.event.EventViewMode;
import com.hunter.dribbble.ui.base.BaseActivity;
import com.hunter.dribbble.ui.fragment.HomeFragment;
import com.hunter.dribbble.widget.spinner.MaterialSpinner;
import com.hunter.library.util.SPUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements AccountHeader.OnAccountHeaderListener,
                                                          Toolbar.OnMenuItemClickListener {

    private static final int VIEW_MODE_ICON_RES[] = {
            R.mipmap.ic_action_small_info,
            R.mipmap.ic_action_small,
            R.mipmap.ic_action_large_info,
            R.mipmap.ic_action_large
    };

    private static final int VIEW_MODE_TITLE_RES[] = {
            R.string.menu_view_mode_small_info,
            R.string.menu_view_mode_small,
            R.string.menu_view_mode_large_info,
            R.string.menu_view_mode_large
    };

    @BindView(R.id.toolbar_main)
    Toolbar         mToolbar;
    @BindView(R.id.container_main)
    FrameLayout     mContainerMain;
    @BindView(R.id.ll_tab_selector)
    LinearLayout    mLlTabSelector;
    @BindView(R.id.spinner_selector_type)
    MaterialSpinner mSpinnerSelectorType;
    @BindView(R.id.spinner_selector_sort)
    MaterialSpinner mSpinnerSelectorSort;
    @BindView(R.id.spinner_selector_time)
    MaterialSpinner mSpinnerSelectorTime;

    private Drawer            mDrawer;
    private ProfileDrawerItem mNavHeader;

    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        shouldAuthorize();
        initNavDrawer();
        initFragment();
        initSpinner();
    }

    private void initNavDrawer() {
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(this);

        mNavHeader = new ProfileDrawerItem().withName("点击头像登录")
                                            .withIcon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
        AccountHeader accountHeader = new AccountHeaderBuilder().withActivity(this)
                                                                .addProfiles(mNavHeader)
                                                                .withSelectionListEnabled(false)
                                                                .withOnAccountHeaderListener(this)
                                                                .build();
        DrawerBuilder builder = new DrawerBuilder();
        builder.withActivity(this)
               .withToolbar(mToolbar)
               .withActionBarDrawerToggleAnimated(true)
               .withAccountHeader(accountHeader)
               .addDrawerItems(new PrimaryDrawerItem().withName("Home")
                                                      .withIcon(R.drawable.ic_vector_nav_home)
                                                      .withSelectedIcon(R.drawable.ic_vector_nav_home_selected),
                               new PrimaryDrawerItem().withName("Follower")
                                                      .withIcon(R.drawable.ic_vector_nav_follow)
                                                      .withSelectedIcon(R.drawable.ic_vector_nav_follow_selected),
                               new PrimaryDrawerItem().withName("My Buckets")
                                                      .withIcon(R.drawable.ic_vector_bucket)
                                                      .withSelectedIcon(R.drawable.ic_vector_bucket_selected),
                               new PrimaryDrawerItem().withName("My Likes")
                                                      .withIcon(R.drawable.ic_vector_nav_like)
                                                      .withSelectedIcon(R.drawable.ic_vector_nav_like_selected),
                               new DividerDrawerItem(),
                               new SecondaryDrawerItem().withName("Settings")
                                                        .withIcon(R.drawable.ic_vector_settings)
                                                        .withSelectedIcon(R.drawable.ic_vector_settings_selected));

        mDrawer = builder.build();
    }

    private void initSpinner() {
        mSpinnerSelectorType.setItems(AppConstants.SELECTOR_TYPE);
        mSpinnerSelectorType.setOnItemClickListener(mHomeFragment);

        mSpinnerSelectorSort.setItems(AppConstants.SELECTOR_SORT);
        mSpinnerSelectorSort.setOnItemClickListener(mHomeFragment);

        mSpinnerSelectorTime.setItems(AppConstants.SELECTOR_TIME);
        mSpinnerSelectorTime.setOnItemClickListener(mHomeFragment);
    }

    private void shouldAuthorize() {
        if (!(boolean) SPUtils.get(this, AppConstants.SP_IS_FIRST, false)) {
            startActivityForResult(new Intent(this, LoginActivity.class), AppConstants.REQUEST_CODE_LOGIN);
            SPUtils.put(this, AppConstants.SP_IS_FIRST, true);
        }
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mHomeFragment = HomeFragment.newInstance();
        transaction.add(R.id.container_main, mHomeFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AppConstants.REQUEST_CODE_LOGIN) {
            toast("授权成功");
        }
    }

    /**
     * 点击侧边栏头像事件
     */
    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
        startActivityForResult(new Intent(this, LoginActivity.class), AppConstants.REQUEST_CODE_LOGIN);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_mode, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_small_info:
            case R.id.menu_small:
            case R.id.menu_large_info:
            case R.id.menu_large:
                int viewModeIndex = item.getOrder();
                App.getInstance().setViewMode(viewModeIndex);
                invalidateOptionsMenu();
                toast("浏览模式切换为：" + getString(VIEW_MODE_TITLE_RES[viewModeIndex]));

                EventBus.getDefault().post(new EventViewMode(viewModeIndex));
                break;

            case R.id.menu_search:
                break;
        }

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_view_mode).setIcon(VIEW_MODE_ICON_RES[App.getInstance().getViewMode()]);
        return true;
    }

}

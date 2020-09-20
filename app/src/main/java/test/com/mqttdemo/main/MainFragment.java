package test.com.mqttdemo.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;

import test.com.mqttdemo.BaseFragment;
import test.com.mqttdemo.R;
import test.com.mqttdemo.adapter.MainViewPagerAdapter;
import test.com.mqttdemo.thirdparty.NoScrollViewPager;


public class MainFragment extends BaseFragment implements View.OnClickListener {

    private final int[] TAB_TITLES = new int[]{R.string.main_receiver, R.string.main_sender, R.string.main_contacts, R.string.main_setting};
    private final int[] TAB_IMGS = new int[]{R.drawable.ic_map_selector, R.drawable.ic_locale_selector, R.drawable.ic_contact_selector, R.drawable.ic_setting_selector};

    private NoScrollViewPager mViewPager;
    private TabLayout mTabLayout;
    private MainViewPagerAdapter mAdapter;
    private LinkedList<BaseFragment> mFragments = new LinkedList<>();
    private ClientFragment mReceiverFragment;
    private ClientFragment mSenderFragment;
    private ContactFragment mClientsFragment;
    private SettingFragment mSettingFragment;
    private TextView mTitle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) mViewPager.setOnPageChangeListener(null);
        if (mTabLayout != null) mTabLayout.setOnTabSelectedListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void initView(View view) {
        mTitle = view.findViewById(R.id.tv_name);

        mViewPager = view.findViewById(R.id.vp_view);
        mTabLayout = view.findViewById(R.id.tl_view);

        addTabs(mTabLayout, getLayoutInflater(), TAB_TITLES, TAB_IMGS);

        mAdapter = new MainViewPagerAdapter(getActivity().getSupportFragmentManager());
        mAdapter.setFragments(mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(TAB_TITLES.length - 1);
        mViewPager.setPagerEnabled(false);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                updatePageTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });
        updatePageTitle(0);
    }

    private void addFragment() {
        mReceiverFragment = new ClientFragment();
        mSenderFragment = new ClientFragment();
        mClientsFragment = new ContactFragment();
        mSettingFragment = new SettingFragment();
        mFragments.add(mReceiverFragment);
        mFragments.add(mSenderFragment);
        mFragments.add(mClientsFragment);
        mFragments.add(mSettingFragment);
    }

    /**
     * 添加 tab
     */
    private void addTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitles, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.tab_layout_item, null);
            tab.setCustomView(view);

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_tab);
            tvTitle.setText(tabTitles[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImgs[i]);
            tabLayout.addTab(tab);
        }
    }

    private void updatePageTitle(int position) {
        switch (position) {
            case 0:
                mTitle.setText("Client receiver");
                break;
            case 1:
                mTitle.setText("Client sender");
                break;
            case 2:
                mTitle.setText("Clients");
                break;
            case 3:
                mTitle.setText("Setting");
                break;
        }
    }
}

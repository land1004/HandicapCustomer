package kr.or.hsnarae.transporthelp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

public class ZipcodeFindActivity extends FragmentActivity
{
    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    //ViewPager에는 한번에 하나의 섹션만 보여진다.
    private static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode_find);

        //어댑터를 생성한다. 섹션마다 프래그먼트를 생성하여 리턴해준다.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        // Initializing the TabLayout
        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tablayout);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(mViewPager);
    }

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter
    {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "도로명주소", "지번주소" };

        private FragmentManager fm;

        public AppSectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int pos)
        {
            //태그로 프래그먼트를 찾는다.
            Fragment fragment = fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + getItemId(pos));

            //프래그먼트가 이미 생성되어 있는 경우에는 리턴
            if (fragment != null) {
                return fragment;
            }

            //프래그먼트의 인스턴스를 생성한다.
            switch(pos)
            {
                case 0:
                    fragment = NewZipcodeFragment.newInstance("FirstFragment, Instance 1");
                    break;
                case 1:
                    fragment = OldZipcodeFragment.newInstance("SecondFragment, Instance 1");
                    break;
            }

            return fragment;
        }

        //프래그먼트를 최대 2개를 생성할 것임
        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        //탭의 제목으로 사용되는 문자열 생성
        @Override
        public CharSequence getPageTitle(int position)
        {
            return tabTitles[position];
        }
    }
}

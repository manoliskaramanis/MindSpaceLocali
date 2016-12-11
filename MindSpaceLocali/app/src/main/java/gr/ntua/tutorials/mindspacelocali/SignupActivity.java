package gr.ntua.tutorials.mindspacelocali;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gr.ntua.tutorials.mindspacelocali.fragments.First;
import gr.ntua.tutorials.mindspacelocali.fragments.Second;

public class SignupActivity extends AppCompatActivity {

    public ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new SectionsAdapter(getSupportFragmentManager()));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("password", "1111");
        setResult(RESULT_OK, intent);
        finish();
    }


    public class SectionsAdapter extends FragmentPagerAdapter{

        public SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
return new First();
            }else if (position == 1){
return new Second();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return new SearchFragment();
//
//                case 1:
//                    return new NearMeFragment();
//
//                case 2:
//                    return new AboutMeFragment();
//
//            }
//            return null;
//        }
//
//        @Override
//        public int getCount() {
//            return 3;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return getString(R.string.search);
//                case 1:
//                    return getString(R.string.nearMe);
//                case 2:
//                    return getString(R.string.aboutMe);
//            }
//            return null;
//        }
//
//
//    }

}

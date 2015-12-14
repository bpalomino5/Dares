package com.palomino.mrbp.dares;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyMaterialUtils utils;
    private TabLayout tabLayout;
    private boolean isHot = false;
    static final int REQUEST_CODE = 0;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new MyMaterialUtils(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        Intent intent = getIntent();
        String mUsername = null;
        if (intent != null) {
            mUsername = intent.getExtras().getString("username");
            utils.setUsername(mUsername);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


/**
 * A placeholder fragment containing a simple view.
 */
public static class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format));
        return rootView;
    }
}

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);

    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

}

    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_timeline);
//            tabLayout.getTabAt(1).setIcon(R.drawable.ic_poll);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_person);

        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    //checking to see when the video recording finishes whether to save to local memory
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == utils.VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
//                watchVideoinFullscreenMode();
//                playbackRecordedVideo();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String post = data.getExtras().getString("Post");
                utils.addTextDare(post, this);
            }
        }
    }

    //onClick method for bottombar item
    public void postVideoDare(View view) {
        //start a recording activity
        utils.startRecordingVideo();
    }

    //onClick method for bottombar item
    public void postTextDare(View view) {
        //call postDare activity
        Intent intent = new Intent(this, PostDare.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void isHotDare(View view) {
        ImageButton imageButton = (ImageButton) view;
        if (!isHot) {
            imageButton.setColorFilter(getResources().getColor(R.color.hotDareAmber));
            isHot = true;
        } else {
            imageButton.setColorFilter(Color.WHITE);
            isHot = false;
        }
    }

    public void addComment(View view) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("username", utils.getUsername());
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new SimpleFragment(utils));
        mSectionsPagerAdapter.addFragment(new DareListFragment(utils));
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

}

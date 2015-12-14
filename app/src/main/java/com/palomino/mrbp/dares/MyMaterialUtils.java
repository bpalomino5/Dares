package com.palomino.mrbp.dares;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.PathMotion;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.bp on 12/4/15.
 */

/* This class has some prerequisites in order to operate.
   So far only these layout parameters are required:
   Assuming there is two layouts a content_main.xml and activity_main.xml
   1. activity_main must contain a CoordinatorLayout
   2. a floating action button with parameters
        * gravity = bottom|end
        * id = fab
   3. a toolbar at the bottom of the layout with parameters
        * id: bottombar
        * gravity = bottom

   Setup this up before using this class
   TO USE do as follows:
   1. Goto your MainActivity.java
   2. create a private member object of this class (ex. private MyMaterialUtils utils)
   3. In onCreate method, initialize object (ex. utils = new MyMaterialUtils(this) )
   4. Override onTouchEvent method, call object's gesture detector before returning super method
        (ex. utils.getGestureDetector().onTouchEvent(event)
             return super.( ... )
                                                             )
    5. Build Project and enjoy!
 */
public class MyMaterialUtils {
    //Activity Reference
    private Activity activity;
    //For transitions & Animations
    private TransitionSet transitionSetShow;
    private TransitionSet transitionSetHide;
    private ChangeBounds onShowBounds;
    private ChangeBounds onHideBounds;
    private ArcMotion arcMotion;
    private Fade fade;
    private Fade hideFade;
    private FloatingActionButton fab;
    private CoordinatorLayout.LayoutParams params;
    private Toolbar bottomBar;
    //For Video Management
    private Uri videoUri;
    public static final int VIDEO_CAPTURE = 101;
    //For RecyclerView
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private static ArrayList<Dare> items;


    //constructor with one argument taking the parameter of the MainActivity object
    public MyMaterialUtils(final Activity activity) {
        this.activity = activity;
        setupReferences(activity);
        setupTransitions();
        setupFabClickListener();
    }

    public MyMaterialUtils(final View view, Fragment fragment) {
        setupReferences(fragment.getActivity());
        setupTimelineReferences(view);
        setupTimelineLayout(fragment);
        addRecyclerViewScrollListener();
        addSwipeRefreshListener();
    }

    public void setupFragmentMethods(final View view, Fragment fragment){
        setupReferences(fragment.getActivity());
        setupTimelineReferences(view);
        setupTimelineLayout(fragment);
        addRecyclerViewScrollListener();
        addSwipeRefreshListener();
    }

    //helper method to setup references for views
    private void setupReferences(Activity activity) {
        fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        bottomBar = (Toolbar) activity.findViewById(R.id.bottombar);
        //set bottombar invisible first
        bottomBar.setVisibility(View.INVISIBLE);
    }

    //method to call ArcMotion transition when moving fab to center
    private void ArcMotionShow(final View view, CoordinatorLayout.LayoutParams params) {
        //Get ViewGroup from view
        ViewGroup sceneRoot = (ViewGroup) view.getParent();

        //start transition with manager, viewgroup, and transition (ChangeBounds)
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSetShow);

        //change scene by setting fab to center in parent
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        view.setLayoutParams(params);
        view.setVisibility(View.INVISIBLE);
    }

    //method to call ArcMotion transition when moving fab to end
    private void ArcMotionHide(final View view, CoordinatorLayout.LayoutParams params) {
        //Get ViewGroup from view
        ViewGroup sceneRoot = (ViewGroup) view.getParent();

        //start transition with manager, viewgroup, and transition (ChangeBounds)
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSetHide);

        //change scene by setting center in parent to false to move back to top left corner
        params.gravity = Gravity.BOTTOM | Gravity.END;
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
    }

    //helper method to setup ArcMotion transition and ChangeBounds parameters
    private void setupTransitions() {
        //setup ArcMotion
        arcMotion = new ArcMotion();
        arcMotion.setMinimumVerticalAngle(0);
        arcMotion.setMinimumHorizontalAngle(90);
        arcMotion.setMaximumAngle(90);

        //setup onHideBounds
        onHideBounds = new ChangeBounds();
        onHideBounds.setDuration(200);
        onHideBounds.setInterpolator(new LinearOutSlowInInterpolator());
        onHideBounds.setStartDelay(180);
        onHideBounds.setPathMotion((PathMotion) arcMotion);     //setting ArcMotion in ChangeBounds by casting it to PathMotion

        //setup onShowBounds
        onShowBounds = new ChangeBounds();
        onShowBounds.setDuration(300);
        onShowBounds.setInterpolator(new LinearOutSlowInInterpolator());
        onShowBounds.setStartDelay(150);
        onShowBounds.setPathMotion((PathMotion) arcMotion);     //setting ArcMotion in ChangeBounds by casting it to PathMotion

        //setup hiding fade for fab
        fade = new Fade();
        fade.setStartDelay(150);
        fade.setDuration(50);
        fade.setInterpolator(new DecelerateInterpolator());

        hideFade = new Fade();
        hideFade.setStartDelay(190);
        hideFade.setDuration(180);
        hideFade.setInterpolator(new DecelerateInterpolator());

        //transition set to handle two at the same time (fade & Arc motion)
        transitionSetShow = new TransitionSet();
        transitionSetShow.addTransition(onShowBounds);
        transitionSetShow.addTransition(fade);

        //reverse transition set to handle two at the same time (fade & Arc motion)
        transitionSetHide = new TransitionSet();
        transitionSetHide.addTransition(onHideBounds);
        transitionSetHide.addTransition(hideFade);
    }

    //method to animate a circular reveal from center out
    private void animateRevealShow(final View viewRoot) {
        //get positions, start radius, and final radius for reveal
        int cx = (viewRoot.getWidth()) / 2 + 300;
        int cy = (viewRoot.getHeight()) / 2;
        int startRadius = fab.getWidth() / 2;
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        //Animate
        Animator animator = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, startRadius, finalRadius);
        animator.setDuration(200);
        animator.setStartDelay(200);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewRoot.setVisibility(View.VISIBLE);
            }
        });
        animator.start();
    }

    //method to animate a circular reveal from border in
    private void animateRevealHide(final View viewRoot) {
        //get positions, start radius, and final radius for reveal
        int cx = (viewRoot.getWidth()) / 2 + 250;
        int cy = (viewRoot.getHeight()) / 2;
        int startRadius = 0;
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        //Animate
        Animator animator = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, finalRadius, startRadius);
        animator.setDuration(225);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        animator.start();
    }

    //helper method to setup fab's onClickListener with reveals and Arc motion
    private void setupFabClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFabAnimation();
            }
        });
    }

    //method to start thread which determines if to start animations
    private void startFabAnimation() {
        class FabAnimationTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                int check = params.gravity;
                if (check == (Gravity.BOTTOM | Gravity.END)) {
                    //move to center
                    publishProgress();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                ArcMotionShow(fab, params);
                animateRevealShow(bottomBar);
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }
        FabAnimationTask fabAnimationTask = new FabAnimationTask();
        fabAnimationTask.execute();
    }

    //Method to start Video Recording
    public void startRecordingVideo() {
        if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            File mediaFile = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
            videoUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            activity.startActivityForResult(intent, VIDEO_CAPTURE);
        } else {
            Toast.makeText(activity, "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    //helper method to help setup RecyclerView adapter, uses Information (class)
    public static List<Information> getData() {
        List<Information> data = new ArrayList<>();
        int profileDrawables = R.mipmap.ic_launcher;
        String descriptions = "The greatest dare ever seen on DareTime!";
        String[] profileNames = {"Brandon", "Tony", "Ian", "Chris"};

        for (int i = 0; i < profileNames.length; i++) {
            Information current = new Information();
            current.profileDrawableId = profileDrawables;
            current.description = descriptions;
            current.profileName = profileNames[i];

            data.add(current);
        }

        return data;
    }

    public static ArrayList<Dare> getDares(){
        return items;
    }

    //helper method to setup Dare information for RecyclerView (Timeline)
    public static ArrayList<Dare> getStartDares() {
        items = new ArrayList<>();
        int profileDrawables = R.mipmap.ic_launcher;    //sample profile pic
        String descriptions = "The greatest dare ever seen on DareTime!";
        String posts = "I dare you to make a social Network!";
        String[] profileNames = {"Brandon", "Tony", "Ian", "Chris", "Sancho", "David", "Max"};


        items.add(new TextDare(profileDrawables, profileNames[0], posts));
        items.add(new VideoDare(profileDrawables, profileNames[1], descriptions));
        items.add(new TextDare(profileDrawables, profileNames[2], posts));
        items.add(new VideoDare(profileDrawables, profileNames[3], descriptions));
        items.add(new VideoDare(profileDrawables, profileNames[4], descriptions));
        items.add(new VideoDare(profileDrawables, profileNames[5], descriptions));
        items.add(new TextDare(profileDrawables, profileNames[6], posts));

        return items;
    }

    public static void addTextDare(String post){
        int profileDrawable = R.mipmap.ic_launcher;
        String profileName = "Foo";
        items.add(new TextDare(profileDrawable, profileName, post));
    }

    //method to setup layout for timeline fragment
    public void setupTimelineLayout(Fragment fragment) {
        adapter = new MyAdapter(fragment.getContext(), getStartDares());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
    }

    //method to setup timeline fragment references for MyMaterial use
    public void setupTimelineReferences(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    }

    //method to add Scroll Listener and fab animations to RecyclerView
    private void addRecyclerViewScrollListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int check = params.gravity;
                if (check == (Gravity.BOTTOM | Gravity.CENTER)) {
                    animateRevealHide(bottomBar);
                    ArcMotionHide(fab, params);
                }
            }

        });
    }

    private void addSwipeRefreshListener() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                adapter.addAll(getDares());
                swipeContainer.setRefreshing(false);
            }
        });
    }
}

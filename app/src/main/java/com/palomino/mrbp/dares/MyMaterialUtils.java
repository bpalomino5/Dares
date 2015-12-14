package com.palomino.mrbp.dares;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static ArrayList<NameValuePair> postsList;
    private static ArrayList<String> mUsernames;
    private JSONArray posts;
    private JSONArray users;
    private static final String POSTS_URL = "http://192.168.2.23:8888/dares/posts.php";
    private static final String ADD_POST_URL = "http://192.168.2.23:8888/dares/addpost.php";
    private static final String USERS_URL = "http://192.168.2.23:8888/dares/users.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_USER_ID = "user_id";
    private static final String TAG_USERS = "users";
    private static final String TAG_MESSAGE = "message";
    //For Profile
    private static String mUsername;


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
        new LoadPosts().execute();
//        new NameTask().execute();
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
    private  ArrayList<Dare> getData() {
        //get profile picture from user_id
        int profileDrawable = R.mipmap.ic_launcher;    //sample profile pic
        items = new ArrayList<>();


        for(int i=0; i< postsList.size(); i++){
            NameValuePair postInfo = postsList.get(i);
            items.add(new TextDare(profileDrawable, postInfo.getName(), postInfo.getValue()));
        }
        return items;
    }


    class NameTask extends AsyncTask<Void, Void, Void> {
        ArrayList<String> usernames = new ArrayList<>();


        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(POSTS_URL);


            try {
                users = json.getJSONArray(TAG_POSTS);

                for(int i=0 ;i < users.length(); i++){
                    JSONObject c = users.getJSONObject(i);

                    usernames.add(c.getString("username"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            mUsernames = usernames;
        }
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


    public void updateJSONdata() {
        postsList = new ArrayList<>();

        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(POSTS_URL);

        try {

            posts = json.getJSONArray(TAG_POSTS);

            // looping through all posts according to the json object returned
            for (int i = 0; i < posts.length(); i++) {
                JSONObject c = posts.getJSONObject(i);

                //gets the content of each tag
                String user_id = c.getString(TAG_USER_ID);
                String description = c.getString(TAG_DESCRIPTION);

                postsList.add(new BasicNameValuePair(user_id,description));//
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadPosts extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            updateJSONdata();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //updateList
            adapter.clear();
            adapter.addAll(getData());
        }
    }

    public  void addTextDare(final String post, final Context context){
        int profileDrawable = R.mipmap.ic_launcher;

        //add to database with asynctask

        class AddDareTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                int success;
                try {
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    parameters.add(new BasicNameValuePair("username", mUsername));
                    parameters.add(new BasicNameValuePair("post", post));

                    JSONParser jParser = new JSONParser();
                    JSONObject json = jParser.makeHttpRequest(ADD_POST_URL,"POST",parameters);


                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        return json.getString(TAG_MESSAGE);
                    }else{
                        return json.getString(TAG_MESSAGE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context,s,Toast.LENGTH_LONG).show();
                new LoadPosts().execute();

            }
        }

        AddDareTask addDareTask = new AddDareTask();
        addDareTask.execute();
    }

    //method to setup layout for timeline fragment
    public void setupTimelineLayout(Fragment fragment) {
        adapter = new MyAdapter(fragment.getContext(), getData());
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
                adapter.addAll(getData());
                swipeContainer.setRefreshing(false);
            }
        });
    }



    public void addRecyclerViewScrollListener2(RecyclerView recyclerView) {
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

    public void setUsername(String username){
        this.mUsername = username;
    }

}

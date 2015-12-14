package com.palomino.mrbp.dares;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by PERU on 12/11/2015.
 */
public class DareListFragment extends Fragment{
    private RecyclerView mDareRecyclerView;
    private DareAdapter mAdapter;
    private Button mFriendButton;
    private MyMaterialUtils utils;
    private boolean isHot = false;

    public DareListFragment(MyMaterialUtils utils) {
        this.utils = utils;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mDareRecyclerView = (RecyclerView) view
                .findViewById(R.id.dare_recycler_view);

        mFriendButton = (Button) view
                .findViewById(R.id.buttonFriend);


        //////////////////////////IMPLEMENT THIS BUTTON/////////////////////////////////////////////
        mFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ALREADY FRIENDS", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        mDareRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        utils.addRecyclerViewScrollListener2(mDareRecyclerView);
        return view;
    }

    private void updateUI() {
        DareList dareList = DareList.get(getActivity());
        List<Dare2> dares = dareList.getDares();
        mAdapter = new DareAdapter(dares);
        mDareRecyclerView.setAdapter(mAdapter);
    }


    private class DareHolder extends RecyclerView.ViewHolder {
        private Dare2 mDare;
        public CardView mDareCardView;
        public ImageButton mHotButton;
        public ImageButton mCommentButton;
        public TextView mProfilePoints;
        public TextView mPost;
        public TextView mProfile;


        public DareHolder(View itemView) {
            super(itemView);

            //////////////////////////////IMPLEMENT HOT AND COMMENT BUTTON HERE/////////////////////
            class PostOnClickListener implements View.OnClickListener{
                @Override
                public void onClick(View view){
                    switch(view.getId()){
                        case R.id.hotDare:
                            System.out.println("HOT");
                            System.out.println(getAdapterPosition());
                            mAdapter.changeLikes(getAdapterPosition());
//                            ImageButton imageButton = (ImageButton) view;
//
//                            if (!isHot) {
//                                imageButton.setColorFilter(getResources().getColor(R.color.hotDareAmber));
//                                isHot = true;
//                            } else {
//                                imageButton.setColorFilter(Color.WHITE);
//                                isHot = false;
//                            }
                            view.setOnClickListener(null);
                            break;
                        case R.id.comment:
                            System.out.println("Comment");
                            System.out.println(getAdapterPosition());
                            break;
                    }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////

            View.OnClickListener listener = new PostOnClickListener();
            mDareCardView = (CardView)
                    itemView.findViewById(R.id.textCardView);

            ///////////////////TEXT VIEWS//////////////////////////////
            mProfilePoints = (TextView)
                    itemView.findViewById(R.id.profile_points);
            mProfile = (TextView)
                    itemView.findViewById(R.id.textdare_posterid);
            mPost = (TextView)
                    itemView.findViewById(R.id.textdare_post);
            ///////////////////////////////////////////////////////////

            ///////////////////BUTTONS/////////////////////////////////
            mHotButton = (ImageButton)
                    itemView.findViewById(R.id.hotDare);
//            mHotButton.setOnClickListener(listener);

            mCommentButton = (ImageButton)
                    itemView.findViewById(R.id.comment);
//            mCommentButton.setOnClickListener(listener);
            ///////////////////////////////////////////////////////////

            ///////////////////RECYCLER VIEW////////////////////////////


        }

        public void bindDare(Dare2 dare){
            mDare = dare;
            mProfilePoints.setText("" + mDare.getPoints());
            mProfile.setText(mDare.getDareTitle());
            mPost.setText(mDare.getDareDescription());
        }

    }

    private class DareAdapter extends RecyclerView.Adapter<DareHolder> {
        private List<Dare2> mDares;
        public DareAdapter(List<Dare2> dares) {
            mDares = dares;
        }

        @Override
        public DareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.text_card, parent, false);
            return new DareHolder(view);
        }
        @Override
        public void onBindViewHolder(DareHolder holder, int position) {
            Dare2 dare = mDares.get(position);
            holder.bindDare(dare);
        }
        @Override
        public int getItemCount() {
            return mDares.size();
        }

        public void changeLikes(int position){
            Dare2 dare = mDares.get(position);
            dare.setPoints(dare.getPoints() + 1);
            mDares.set(position, dare);
            notifyDataSetChanged();
        }
    }

}

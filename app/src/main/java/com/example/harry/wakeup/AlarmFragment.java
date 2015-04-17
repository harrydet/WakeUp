package com.example.harry.wakeup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.harry.wakeup.adapters.AlarmListAdapter;
import com.example.harry.wakeup.helpers.DatabaseHelper;
import com.software.shell.fab.ActionButton;

import java.util.List;


public class AlarmFragment extends ListFragment implements View.OnClickListener, AlarmListAdapter.AdapterCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseHelper dbHelper;

    private ActionButton fab;

    private List<Alarm> alarmsList;

    private ListAdapter mAdapter;

    private TextView emptyListText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());

        alarmsList = dbHelper.getAllAlarms();

        this.mAdapter = new AlarmListAdapter(getActivity(), alarmsList, this);
        setListAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        fab = (ActionButton) rootView.findViewById(R.id.fab_activity_action_button);
        fab.setOnClickListener(this);

        emptyListText = (TextView) rootView.findViewById(R.id.empty_alarm_list);
        if(alarmsList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        this.alarmsList = dbHelper.getAllAlarms();
        ((AlarmListAdapter) this.mAdapter).updateDataset(this.alarmsList);
        if(this.alarmsList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getListView().setDivider(new ColorDrawable(Color.BLUE));
        getListView().setDividerHeight(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_activity_action_button:
                Intent intent = new Intent(getActivity(), NewAlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    public void onMethodCallback() {
        if(alarmsList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
        } else {
            emptyListText.setVisibility(View.GONE);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAlarmFragmentInteraction(String id);
    }

}

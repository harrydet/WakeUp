package com.example.harry.wakeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayListFragment extends ListFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmActivity inst;
    private TextView alarmTextView;
    private Ringtone ringtone;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayList.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayListFragment newInstance(String param1, String param2) {
        TodayListFragment fragment = new TodayListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TodayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        String jsonString = settings.getString("json_object", null/*default value*/);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_today_list, container, false);

        alarmTextView = (TextView) rootView.findViewById(R.id.alarmText);
        Button silence = (Button) rootView.findViewById(R.id.silence_button);
        silence.setOnClickListener(this);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        String alarmTextString = settings.getString("alarm_text", null/*default value*/);
        if(alarmTextString != null){
            alarmTextView.setText(alarmTextString);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("alarm_text");
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        public void onTodayListFragmentInteraction(String id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.silence_button:
                Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                alarmManager.cancel(PendingIntent.getBroadcast(getActivity(), 1, myIntent, 0));

                Intent stopIntent = new Intent(getActivity(), RingtonePlayingService.class);
                getActivity().stopService(stopIntent);
                break;
        }
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }

}

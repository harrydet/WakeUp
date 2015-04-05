package com.example.harry.wakeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
 * {@link AlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmActivity inst;
    private TextView alarmTextView;
    private Ringtone ringtone;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        alarmTimePicker = (TimePicker) rootView.findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) rootView.findViewById(R.id.alarmText);
        ToggleButton alarmToggle = (ToggleButton) rootView.findViewById(R.id.alarmToggle);
        alarmToggle.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.silence_button:
                Intent stopIntent = new Intent(getActivity(), RingtonePlayingService.class);
                getActivity().stopService(stopIntent);
                break;
            case R.id.alarmToggle:
                if (((ToggleButton) v).isChecked()) {
                    Log.d("AlarmActivity", "Alarm On");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                    Intent myIntent = new Intent(getActivity(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, myIntent, 0);
                    MainScreen mainActivity = (MainScreen) getActivity();
                    mainActivity.getAlarmManager().set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    MainScreen mainActivity = (MainScreen) getActivity();
                    mainActivity.getAlarmManager().cancel(pendingIntent);
                    setAlarmText("");
                    Log.d("MyActivity", "Alarm Off");
                }
        }
    }



    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
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
        public void onFragmentInteraction(Uri uri);
    }

}

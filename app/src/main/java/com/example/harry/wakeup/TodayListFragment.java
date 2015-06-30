package com.example.harry.wakeup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harry.wakeup.adapters.AlarmListAdapter;
import com.example.harry.wakeup.adapters.CardViewAdapter;
import com.example.harry.wakeup.adapters.ListTaskListAdapter;
import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayListFragment extends Fragment implements View.OnClickListener, AlarmListAdapter.AdapterCallback,
        ListTaskListAdapter.AdapterCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<Task> items;
    int taskListId;
    SharedPreferences settings;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseHelper dbHelper;
    private List<TaskList> taskLists;
    private CardViewAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private TextView emptyList;

    public TodayListFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        taskListId = settings.getInt("tasklist_id_to_display", -1);

        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());


        if (taskListId != -1) {
            TaskList currentList = dbHelper.getTaskList(taskListId);
            items = dbHelper.getTasksByTaskList(currentList);
        } else {
            items = new ArrayList<>();
        }
        adapter = new CardViewAdapter(items);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_today_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        emptyList = (TextView) rootView.findViewById(R.id.empty_today_list);
        if (taskListId == -1) {
            emptyList.setVisibility(View.VISIBLE);
        } else {
            emptyList.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateDataset(items);
        taskListId = settings.getInt("tasklist_id_to_display", -1);
        if (taskListId != -1) {
            TaskList currentList = dbHelper.getTaskList(taskListId);
            if (currentList != null) {
                items = dbHelper.getTasksByTaskList(currentList);
                refreshTodayTasks();
                adapter.updateDataset(items);
            }
        } else {
            items = new ArrayList<>();
            adapter.updateDataset(items);
        }

        if (taskListId == -1) {
            items = new ArrayList<>();
            adapter.updateDataset(items);
            emptyList.setVisibility(View.VISIBLE);
        } else if (dbHelper.getTaskList(taskListId) == null) {
            items = null;
            adapter.updateDataset(null);
        } else {
            refreshTodayTasks();
            adapter.updateDataset(items);
            emptyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMethodCallback() {
        if (items.size() == 0) {
            adapter.updateDataset(items);
            emptyList.setVisibility(View.VISIBLE);
        } else {
            emptyList.setVisibility(View.GONE);
        }
    }

    public void refreshTodayTasks() {
        TaskList currentList = dbHelper.getTaskList(taskListId);
        this.items = dbHelper.getTasksByTaskList(currentList);
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
    }


}

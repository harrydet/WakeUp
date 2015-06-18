package com.example.harry.wakeup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.harry.wakeup.adapters.ListTaskListAdapter;
import com.example.harry.wakeup.helpers.DatabaseHelper;
import com.software.shell.fab.ActionButton;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ListTaskListFragment extends ListFragment implements View.OnClickListener, ListTaskListAdapter.AdapterCallback{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ActionButton fab;

    private DatabaseHelper dbHelper;
    private List<TaskList> taskLists;

    private ListTaskListAdapter adapter;

    private TextView emptyTasklistList;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button addTaskButton;
/*    private Button deleteAllTasksButton;*/

    private Callbacks mCallbacks = itemSelectedCallback;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private static Callbacks itemSelectedCallback = new Callbacks() {
        @Override
        public void onListItemSelected(int id) {
        }
    };

    // TODO: Rename and change types of parameters
    public static ListTaskListFragment newInstance(String param1, String param2) {
        ListTaskListFragment fragment = new ListTaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListTaskListFragment() {
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

        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());

        taskLists = dbHelper.getAllTaskLists();

        Log.e("Total Tasks", Integer.toString(taskLists.size()));

        this.adapter = new ListTaskListAdapter(getActivity(), taskLists, this);
        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasklist, container, false);

/*        deleteAllTasksButton = (Button) view.findViewById(R.id.deleteTasksButton);
        deleteAllTasksButton.setOnClickListener(this);*/

        emptyTasklistList = (TextView) view.findViewById(R.id.empty_tasklist_list);
        if(taskLists.size() == 0){
            emptyTasklistList.setVisibility(View.VISIBLE);
        } else {
            emptyTasklistList.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshTaskLists();
        ListTaskListAdapter listTaskListAdapter = (ListTaskListAdapter)getListAdapter();
        listTaskListAdapter.updateDataset(this.taskLists);

        if(taskLists.size() == 0){
            emptyTasklistList.setVisibility(View.VISIBLE);
        } else {
            emptyTasklistList.setVisibility(View.GONE);
        }

    }

    private void refreshTaskLists(){
        this.taskLists = dbHelper.getAllTaskLists();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /*case R.id.addButton:
                Intent intent = new Intent(getActivity().getApplicationContext(), NewTaskListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            /*case R.id.deleteTasksButton:
                dbHelper.deleteAllTasks();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Deleted " + dbHelper.deleteAllTasks() + " rows.", Toast.LENGTH_LONG).show();*/
            default:
                break;
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){
        super.onListItemClick(listView, view, position, id);
        Intent detailIntent = new Intent(getActivity(), TaskListDetailsActivty.class);
        detailIntent.putExtra("tasklist_id", taskLists.get(position).getId());
        startActivity(detailIntent);
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
        public void onTaskListFragmentInteraction(String id);
    }

    public static interface Callbacks {

        public abstract void onListItemSelected(int id);
    }

    @Override
    public void onMethodCallback() {
        if(taskLists.size() == 0){
            emptyTasklistList.setVisibility(View.VISIBLE);
        } else {
            emptyTasklistList.setVisibility(View.GONE);
        }
    }


}

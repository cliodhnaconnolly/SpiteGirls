package com.example.spitegirls.eventme;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MyEventsFragment extends Fragment {

    private ListView listView;
    public ProgressBar spinner;
    // We no longer need this to have such a scope
    private ArrayList<Event> eventList;

    private ArrayList<Event> pastEvents;
    private ArrayList<Event> futureEvents;

    public static MyEventsFragment newInstance(Bundle bundle) {
        MyEventsFragment fragment = new MyEventsFragment();
        if(bundle != null){
            fragment.setArguments(bundle);
//            Log.d("INPUT TO ", bundle.toString());
//            Log.d("SET ARGS", fragment.getArguments().toString());

        }
        return fragment;
    }

    public MyEventsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        spinner = (ProgressBar) view.findViewById(R.id.spinner);


        //https://developer.android.com/training/swipe/respond-refresh-request.html#RespondRefresh
        //http://www.survivingwithandroid.com/2014/05/android-swiperefreshlayout-tutorial-2.html
        //https://www.bignerdranch.com/blog/implementing-swipe-to-refresh/
        //Didn't use that last one AS much

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        //I don't like this being final but I'm going blank on how to get around that

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Log.d("NAVE", "lol");
                ((MainActivity) getActivity()).refreshData();
            }
                });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(this.getArguments() != null){
            Bundle bundle = this.getArguments();

            if(bundle.getSerializable("arraylist") != null){
                eventList = (ArrayList<Event>) bundle.getSerializable("arraylist");
                futureEvents = new ArrayList<Event>();
                pastEvents = new ArrayList<Event>();
                // Sort list and populate pastEvents and FutureEvents
                Calendar today = Calendar.getInstance();

                for(int i = 0; i < eventList.size(); i++){
                    if(today.after(eventList.get(i).getCalendarDate())) {
                        pastEvents.add(eventList.get(i));
                    }
                    else{
                        futureEvents.add(eventList.get(i));
                    }
                }

                //Sorting list by date
                Collections.sort(futureEvents, new Comparator<Event>() {
                    public int compare(Event ev1, Event ev2) {
                        if (ev2.startTime == null || ev1.startTime == null) return 0;
                        return ev1.startTime.compareTo(ev2.startTime);
                    }
                });

                 //Sort list and populate pastEvents and FutureEvents


                // Default is future so populate future in this adapter initially
                CustomListAdapter adapter = new CustomListAdapter(getActivity(), R.layout.list_layout, futureEvents);
                listView.setAdapter(adapter);
                spinner.setVisibility(View.INVISIBLE);

                // Adding functionality for user clicks
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("INT I IS", Integer.toString(i));
                        Log.d("LONG L IS", Long.toString(l));

                        Bundle args = new Bundle();
                        // this needs to become futureEvents.get(i)
                        MenuItem menuItem = (MenuItem) view.findViewById(R.id.menu_item_time_future);
                        if(menuItem.isChecked()){
                            args.putSerializable("event", futureEvents.get(i));
                        } else {
                            args.putSerializable("event", pastEvents.get(i));
                        }
                        
                        EventDetailsFragment eventFrag = EventDetailsFragment.newInstance(args);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.my_frame, eventFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });

            }

        } else {
            spinner.setVisibility(View.VISIBLE);
            Log.d("INSTEAD", "IS NULL");
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.time_frame, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_time_past:
                item.setChecked(true);
                // REMOVE THIS TOAST AFTER TESTING
                Toast.makeText(getActivity(), "MENU PAST CLICK", Toast.LENGTH_SHORT).show();

                // Create custom list adapter with past events
                // check for nulls before hand that pastEvents has shit although i think itll be fine without
                // unless you want to make a "No events" textview appear
                // set listview to have custom adapter
                // if this takes a longer period of time put up a spinner and take it down when done

                if(pastEvents != null){
                    CustomListAdapter adapter = new CustomListAdapter(getActivity(), R.layout.list_layout, pastEvents);
                    listView.setAdapter(adapter);
                    spinner.setVisibility(View.INVISIBLE);
                }
                return true;
            case R.id.menu_item_time_future:
                item.setChecked(true);
                // REMOVE THIS TOAST AFTER TESTING
                Toast.makeText(getActivity(), "MENU FUTURE CLICK", Toast.LENGTH_SHORT).show();

                // Create custom list adapter with future events
                // check for nulls before hand that futureEvents has shit although i think itll be fine without
                // unless you want to make a "No events" textview appear
                // set listview to have custom adapter
                // if this takes a longer period of time put up a spinner and take it down when done
                if(futureEvents != null){
                    CustomListAdapter adapter = new CustomListAdapter(getActivity(), R.layout.list_layout, futureEvents);
                    listView.setAdapter(adapter);
                    spinner.setVisibility(View.INVISIBLE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // When orientation changes we want to maintain the item in bottom nav
    // Don't really need this cause 0 is default anyways
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        BottomNavigationView bottomNavigationView;

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        }
    }

}

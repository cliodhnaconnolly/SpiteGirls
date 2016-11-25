package com.example.spitegirls.eventme;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ShareActionProvider;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsFragment extends Fragment{

    private Event details;
    private TextView title;
    private TextView description;
    private TextView startDate;
    private Button findOnMap;

    public static EventDetailsFragment newInstance(Bundle details) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        if(details != null){
            fragment.setArguments(details);
            Log.d("INPUT TO ", details.toString());
            Log.d("SET ARGS", fragment.getArguments().toString());

        }
        return fragment;
    }

    public EventDetailsFragment() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if(this.getArguments() != null){
            Bundle bundle = this.getArguments();

            if(bundle.getSerializable("event") != null){

                details = (Event) bundle.getSerializable("event");
                String timeDetails = details.startTime;
                String eventDate = getEventDate(timeDetails);
                // Set up info
                title = (TextView) view.findViewById(R.id.title);
                title.setText(details.name);
                description = (TextView) view.findViewById(R.id.description);
                description.setText(details.description);

                startDate = (TextView) view.findViewById(R.id.event_date);
                startDate.setText(eventDate);

                TextView placeName = (TextView) view.findViewById(R.id.place_Name);
                if(!(details.placeName == null) &&!(details.placeName.isEmpty())){
                    placeName.setText(details.placeName);
                } else {placeName.setVisibility(View.GONE); }

                TextView city = (TextView) view.findViewById(R.id.city_details);
                if(!(details.city == null) && !details.city.isEmpty()){
                    city.setText(details.city);
                } else { city.setVisibility(View.GONE); }

                TextView country = (TextView) view.findViewById(R.id.country_details);
                if(!(details.country == null) && !details.country.isEmpty()){
                    country.setText(details.country);
                } else { country.setVisibility(View.GONE); }

                // Doesn't work yet but trying
//                String source = (String) bundle.getSerializable("source");
//                getCoverPhotoSource(details.get("id"));

                if( details.coverURL != null) {
                    new DownloadImage((ImageView) view.findViewById(R.id.cover_photo)).execute(details.coverURL);
                } else {
                    Log.d("Source is ", "source is null");
                    view.findViewById(R.id.cover_photo).setVisibility(View.GONE);
                }

                if(details.longitude.equals("")){
                    findOnMap = (Button) view.findViewById(R.id.find_on_map);
                    findOnMap.setVisibility(View.GONE);
                }
            }
        }

        // FB doesn't work because they're assholes
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources = getResources();

                String shareBody = "I think you might be interested in this event!" +
                        "\n\nEvent Name: " + title.getText().toString() + "\n\nDescription: "
                        + description.getText().toString() + "\nStart Time: "
                        + startDate.getText().toString() + "\n\n"
                        + "Go to Eventure to find out more!";

                String shareSubject = "Event you might be interested in! - Eventure";


                PackageManager pm = getActivity().getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                Intent blankIntent = new Intent();
                blankIntent.setAction(Intent.ACTION_SEND);
                Intent openInChooser = Intent.createChooser(blankIntent, "Share via..");

                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Making sure only what we want shows up
                    ResolveInfo resolveInfo = resInfo.get(i);
                    String packageName = resolveInfo.activityInfo.packageName;
                    // If you wish to add more options add them below to the if statement if I'm missing any
                    if(packageName.contains("twitter") || packageName.contains("sms") || packageName.contains("android.gm")
                            || packageName.contains("email") || packageName.contains("snapchat") || packageName.contains("android")
                            || packageName.contains("whatsapp") || packageName.contains("viber") || packageName.contains("skype")
                            || packageName.contains("outlook")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                        intentList.add(new LabeledIntent(intent, packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
                    }
                }

                // Converts list with intents to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    private String getEventDate(String timeDetails) {
        String split[] = timeDetails.split("T");
        String data[] = split[0].split("-");

        int date = Integer.parseInt(data[2]);
        int month = Integer.parseInt(data[1]);

        // Format date
        if(date % 10 == 1 && date != 11) {
            data[2] = data[2] + "st";
        } else if(date % 10 == 2 && date != 12) {
            data[2] = data[2] + "nd";
        } else if(date % 10 == 3 && date != 13) {
            data[2] = data[2] + "rd";
        } else {
            data[2] = data[2] + "th";
        }

        // Format month
        switch(month) {
            case 1 : data[1] = "January"; break;
            case 2 : data[1] = "February"; break;
            case 3 : data[1] = "March"; break;
            case 4 : data[1] = "April"; break;
            case 5 : data[1] = "May"; break;
            case 6 : data[1] = "June"; break;
            case 7 : data[1] = "July"; break;
            case 8 : data[1] = "August"; break;
            case 9 : data[1] = "September"; break;
            case 10 : data[1] = "October"; break;
            case 11 : data[1] = "November"; break;
            case 12 : data[1] = "December"; break;
        }

        String formattedDate = data[2] + " " + data[1] + " " + data[0];

        String time[] = split[1].split(":");

        String formattedTime = " at " + time[0] + ":" + time[1];

        return formattedDate + formattedTime;
    }

    // Commenting out for the moment as it doesn't work but may be useful later
    // Makes Facebook Graph API call to get specific event data
//    private void getCoverPhotoSource(String id){
//
//        Bundle coverBundle = new Bundle();
//        coverBundle.putString("fields", "cover,id");
//        // Getting cover photo from event_id
//        GraphRequest request = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/" + id,
//                coverBundle,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                        JSONObject responseJSONObject = response.getJSONObject();
//                        Log.d("RESPONSE IS", "<" + responseJSONObject.toString() + ">");
//                        if (responseJSONObject != null && responseJSONObject.has("cover")) {
//                            try {
////                                sources.put(responseJSONObject.getString("id"), responseJSONObject.getString("source"));
//                                source = responseJSONObject.getString("source");
//                                //Log.d("SORUCE IS NOW", "<" + source + ">");
//                                //setDone(true);
//
//                            } catch (JSONException e) { e.printStackTrace(); }
//                        } else {
//                            Log.d("FALSE", "ALARM");
//                        }
//
//                    }
//                }
//        );
//        request.executeAsync();
//    }

}

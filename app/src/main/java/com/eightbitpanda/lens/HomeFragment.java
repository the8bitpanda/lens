package com.eightbitpanda.lens;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        HashMap<String, Button> buttonsMap = new HashMap<String, Button>();
        buttonsMap.put("Weblink", (Button) view.findViewById(R.id.button_weblink));
        buttonsMap.put("Call", (Button) view.findViewById(R.id.button_call));
        buttonsMap.put("Business Card", (Button) view.findViewById(R.id.button_bussinesscard));
        buttonsMap.put("Translate", (Button) view.findViewById(R.id.button_translate));
        buttonsMap.put("Search", (Button) view.findViewById(R.id.button_search));

        for (String type : buttonsMap.keySet()) {
            setButtonMapping(type, buttonsMap.get(type));
        }

        return view;
    }

    private void setButtonMapping(final String type, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call scanner activity
                Intent i = new Intent(getActivity(), ScannerActivity.class);
                i.putExtra("Type", type);
                startActivity(i);

            }
        });
    }


}

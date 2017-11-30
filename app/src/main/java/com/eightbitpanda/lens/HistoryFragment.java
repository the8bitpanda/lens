package com.eightbitpanda.lens;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eightbitpanda.lens.helper.DatabaseHandler;
import com.eightbitpanda.lens.helper.HistoryItem;

import java.util.List;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        DatabaseHandler db = new DatabaseHandler(getActivity());
        List<HistoryItem> historyItemsList = db.getHistory();

        for (HistoryItem historyItem : historyItemsList)
            Toast.makeText(getActivity(), "" + historyItem.getId() + " " + historyItem.getType() + " " + historyItem.getText() + " " + historyItem.getTime(), Toast.LENGTH_SHORT).show();

        return view;
    }

}

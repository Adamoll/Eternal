package com.eternalsrv.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.asynctasks.model.TestReply;
import com.eternalsrv.utils.constant.ServerMethodsConsts;

public class TestResultsFragment extends Fragment {
    private TextView TextEResult;
    private TextView TextIResult;
    private TextView TextSResult;
    private TextView TextNResult;
    private TextView TextJResult;
    private TextView TextPResult;
    private TextView TextFResult;
    private TextView TextTResult;
    private TextView typeTResult;

    private PreferencesManager preferencesManager;
    private MyPreferences myPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_results, container, false);

        preferencesManager = App.getPreferencesManager();
        myPreferences = App.getPreferences();

        GetResults gR = new GetResults(ServerMethodsConsts.RESULTS + "/" + myPreferences.getUserId());
        gR.execute();

        Button backButton = (Button) view.findViewById(R.id.backFromResults);
        TextEResult = (TextView) view.findViewById(com.eternalsrv.R.id.EText);
        TextIResult = (TextView) view.findViewById(com.eternalsrv.R.id.IText);
        TextSResult = (TextView) view.findViewById(com.eternalsrv.R.id.SText);
        TextNResult = (TextView) view.findViewById(com.eternalsrv.R.id.NText);
        TextTResult = (TextView) view.findViewById(com.eternalsrv.R.id.TText);
        TextFResult = (TextView) view.findViewById(com.eternalsrv.R.id.FText);
        TextPResult = (TextView) view.findViewById(com.eternalsrv.R.id.PText);
        TextJResult = (TextView) view.findViewById(com.eternalsrv.R.id.JText);
        typeTResult = (TextView) view.findViewById(com.eternalsrv.R.id.resultType);

        backButton.setOnClickListener(v -> ((MainActivity) getActivity()).getFbLoginFragment().changeFragment((MainActivity) getActivity()));

        return view;
    }

    private class GetResults extends BaseAsyncTask<Void> {
        ProgressDialog resultProgress;

        public GetResults(String urn) {
            super(urn, null);
            resultProgress = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            resultProgress.setTitle("Loading");
            resultProgress.setIndeterminate(true);
            resultProgress.setCancelable(false);
            resultProgress.show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                TestReply testReply = App.getGson().fromJson(result, TestReply.class);
                myPreferences.setMbtiType(testReply.getType());
                preferencesManager.savePreferences();
                typeTResult.setText(testReply.getType());
                TextEResult.setText(String.format("Extraversion: %d%%", testReply.getE()));
                TextIResult.setText(String.format("Introversion: %d%%", 100 - testReply.getE()));
                TextSResult.setText(String.format("Sensing: %d%%", 100 - testReply.getN()));
                TextNResult.setText(String.format("Intuition: %d%%", testReply.getN()));
                TextTResult.setText(String.format("Thinking: %d%%", testReply.getT()));
                TextFResult.setText(String.format("Feeling: %d%%", 100 - testReply.getT()));
                TextJResult.setText(String.format("Judging: %d%%", 100 - testReply.getP()));
                TextPResult.setText(String.format("Perceiving: %d%%", testReply.getP()));
            } else {
                Toast.makeText(getActivity(), "server error", Toast.LENGTH_LONG).show();
            }
            if (resultProgress.isShowing())
                resultProgress.dismiss();
        }
    }
}

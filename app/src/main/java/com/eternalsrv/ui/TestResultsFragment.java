package com.eternalsrv.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.constant.ServerMethodsConsts;

import org.json.JSONException;
import org.json.JSONObject;

public class TestResultsFragment extends Fragment{
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

        GetResults gR = new GetResults(ServerMethodsConsts.RESULTS + "/" + myPreferences.getUserId(), null);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getFbLoginFragment().changeFragment((MainActivity)getActivity());
            }
        });

        return view;
    }

    private class GetResults extends BaseAsyncTask{
        ProgressDialog resultProgress;

        public GetResults(String urn, AsyncTaskParams params) {
            super(urn, params);
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
            try {
                if (result != null) {
                    JSONObject obj = new JSONObject(result);
                    String type = obj.getString("type");
                    int e = obj.getInt("e");
                    int n = obj.getInt("n");
                    int t = obj.getInt("t");
                    int p = obj.getInt("p");
                    myPreferences.setMbtiType(type);
                    preferencesManager.savePreferences();
                    typeTResult.setText(type);
                    TextEResult.setText(String.format("Extraversion: %d%%", e));
                    TextIResult.setText(String.format("Introversion: %d%%", 100 - e));
                    TextSResult.setText(String.format("Sensing: %d%%", 100 - n));
                    TextNResult.setText(String.format("Intuition: %d%%", n));
                    TextTResult.setText(String.format("Thinking: %d%%", t));
                    TextFResult.setText(String.format("Feeling: %d%%", 100 - t));
                    TextJResult.setText(String.format("Judging: %d%%", 100 - p));
                    TextPResult.setText(String.format("Perceiving: %d%%", p));
                } else {
                    Toast.makeText(getActivity(), "server error", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (resultProgress.isShowing())
                resultProgress.dismiss();
        }
    }
}

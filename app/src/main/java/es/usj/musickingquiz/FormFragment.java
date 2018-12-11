package es.usj.musickingquiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class FormFragment extends Fragment {


    View view;
    private RadioButton rbtn1;
    private RadioButton rbtn2;
    private RadioButton rbtn3;
    private RadioGroup rgOptions;
    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_form, container, false);

        rgOptions = view.findViewById(R.id.rgOptions);

        rbtn1 = view.findViewById(R.id.rbtn1);
        rbtn2 = view.findViewById(R.id.rbtn2);
        rbtn3 = view.findViewById(R.id.rbtn3);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //mi

    public void setRadioButtons(String option1, String option2, String option3) {
        rbtn1.setText(option1);
        rbtn2.setText(option2);
        rbtn3.setText(option3);
    }

    public String getSelectedOption() {
        int rbID = rgOptions.getCheckedRadioButtonId();
        RadioButton rbSeleccted = view.findViewById(rbID);
        String selectedString = "";
        if (rbSeleccted != null) selectedString = rbSeleccted.getText().toString();
        else selectedString = "";
        return selectedString;
    }

}

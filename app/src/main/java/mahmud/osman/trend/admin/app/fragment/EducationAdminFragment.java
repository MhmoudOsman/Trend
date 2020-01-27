package mahmud.osman.trend.admin.app.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mahmud.osman.trend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EducationAdminFragment extends Fragment {


      public EducationAdminFragment() {
            // Required empty public constructor
      }


      @Override
      public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_education_admin , container , false);
      }

}

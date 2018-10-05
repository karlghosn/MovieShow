package com.gdevelopers.movies.model;

import androidx.fragment.app.Fragment;

import java.util.List;


@SuppressWarnings("unused")
abstract public class KFragment extends Fragment {
    abstract public void serviceResponse(int responseID, List<KObject> objects);
    abstract public void update(ModelService service, boolean reload);
}

package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.adapters.UserListAdapter;
import com.gdevelopers.movies.helpers.Connection;
import com.gdevelopers.movies.helpers.Constants;
import com.gdevelopers.movies.helpers.DialogHelper;
import com.gdevelopers.movies.helpers.OfflineLayout;
import com.gdevelopers.movies.helpers.PostRetrofit;
import com.gdevelopers.movies.helpers.PreferencesHelper;
import com.gdevelopers.movies.helpers.RecyclerItemTouchHelper;
import com.gdevelopers.movies.helpers.Response;
import com.gdevelopers.movies.helpers.Validation;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.objects.Section;
import com.gdevelopers.movies.objects.UserList;
import com.gdevelopers.movies.rest.ApiClient;
import com.gdevelopers.movies.rest.ApiInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;


@SuppressWarnings("WeakerAccess")
public class FragmentLists extends KFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private MainActivity activity;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private UserListAdapter adapter;
    @BindView(R.id.created_lists)
    RecyclerView listRv;
    private ViewGroup container;
    private List<UserList> userLists;
    private boolean clicked = false;
    private ModelService service;
    private Context context;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    private int listCount = 0;
    private View rootView;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    private Unbinder unbinder;
    @BindDrawable(R.drawable.divider)
    Drawable divider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        this.container = container;
        activity = (MainActivity) getActivity();
        context = getContext();
        if (activity != null) {
            activity.setVisibleFragment(this);
            service = activity.getService();
        }


        listRv.setLayoutManager(new LinearLayoutManager(context));
        listRv.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(divider);
        listRv.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listRv);

        this.update(service, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean hasConnection = Connection.isNetworkAvailable(context);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.your_lists);

        activity.getNavigationView().setCheckedItem(R.id.nav_lists);
        FloatingActionButton fab = activity.getAddFab();
        if (hasConnection) fab.show();
        else fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listCount < 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View alertView = LayoutInflater.from(context).inflate(R.layout.add_list_dialog, container, false);
                    final TextInputLayout nameTl = alertView.findViewById(R.id.list_name);
                    final TextInputLayout descriptionTl = alertView.findViewById(R.id.list_description);
                    Button cancelBt = alertView.findViewById(R.id.cancel);
                    Button createBt = alertView.findViewById(R.id.create_list);
                    builder.setView(alertView);
                    final AlertDialog dialog = builder.create();
                    cancelBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                    createBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Validation.hasText(nameTl) && Validation.isName(descriptionTl)) {
                                @SuppressWarnings("ConstantConditions") String name = nameTl.getEditText().getText().toString();
                                @SuppressWarnings("ConstantConditions") String description = descriptionTl.getEditText().getText().toString();
                                PostRetrofit postRetrofit = new PostRetrofit();
                                postRetrofit.setName(name);
                                postRetrofit.setDescription(description);
                                postRetrofit.setLanguage();

                                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                                Call<Response> call = apiService.createList(PreferencesHelper.getSessionId(context), postRetrofit);
                                call.enqueue(new Callback<Response>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                        int statusCode = response.code();
                                        if (statusCode == 201) {
                                            update(service, true);
                                            dialog.cancel();
                                        }

                                        //noinspection ConstantConditions
                                        Snackbar.make(activity.findViewById(R.id.adView), response.body().getMessage(),
                                                Snackbar.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                        // Log error here since request failed
                                        Log.e("Failure", t.toString());
                                    }
                                });
                            }
                        }
                    });
                    dialog.show();
                } else DialogHelper.proVersionDialog(context);
            }
        });
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        progressBar.setVisibility(View.GONE);

        if (responseID == Constants.GET_CREATED_LISTS && objects != null) {
            userLists = new ArrayList<>();

            Section section = (Section) objects.get(0);

            boolean hide = section.getUserLists().isEmpty();
            listRv.setVisibility(hide ? View.GONE : View.VISIBLE);
            emptyLayout.setVisibility(hide ? View.VISIBLE : View.GONE);
            List<UserList> lists = section.getUserLists();
            listCount = lists.size();
            userLists.addAll(lists);

            adapter = new UserListAdapter(context, userLists);
            listRv.setAdapter(adapter);

            adapter.setOnItemClickListener(new UserListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    UserList userList = (UserList) adapter.getItem(position);
                    FragmentListDetails fragmentListDetails = new FragmentListDetails();
                    fragmentListDetails.setListId(String.valueOf(userList.id()));
                    fragmentListDetails.setTitle(userList.getName());
                    activity.replaceFragment(fragmentListDetails);
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lists, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sync) {
            this.update(service, true);
            progressBar.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(ModelService service, boolean reload) {
        boolean hasConnection = Connection.isNetworkAvailable(context);
        isOnline(hasConnection);
        if (hasConnection && service != null)
            service.getCreatedLists(PreferencesHelper.getAccountId(context), PreferencesHelper.getSessionId(context), reload);
        else OfflineLayout.init(context, rootView, this, service);
    }


    private void isOnline(boolean isOnline) {
        progressBar.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        listRv.setVisibility(isOnline ? View.VISIBLE : View.GONE);
        offlineLayout.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof UserListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = userLists.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final UserList deletedItem = userLists.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.ads_layout), name + " removed from list", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    clicked = true;
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (!clicked) {
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<Response> call = apiService.deleteList((int) deletedItem.id(),
                                PreferencesHelper.getSessionId(context));
                        call.enqueue(new Callback<Response>() {
                            @Override
                            public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                                int statusCode = response.code();
                                Log.d("Status Code", String.valueOf(statusCode));
                                if (statusCode == 500) {
                                    Snackbar.make(activity.findViewById(R.id.adView), "List successfully deleted",
                                            Snackbar.LENGTH_SHORT).show();
                                } else {
                                    @SuppressWarnings("ConstantConditions") String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                                // Log error here since request failed
                                Log.e("Failure", t.toString());
                            }
                        });
                    }

                    clicked = false;
                    super.onDismissed(transientBottomBar, event);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

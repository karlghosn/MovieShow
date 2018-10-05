package br.com.mauker.materialsearchview;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.adapters.CursorSearchAdapter;
import br.com.mauker.materialsearchview.db.HistoryContract;
import br.com.mauker.materialsearchview.utils.AnimationUtils;

/**
 * Created by Mauker and Adam McNeilly on 30/03/2016. dd/MM/YY.
 * Maintained by Mauker, Adam McNeilly and our beautiful open source community <3
 * Based on stadiko on 6/8/15. https://github.com/krishnakapil/MaterialSeachView
 */
public class MaterialSearchView extends FrameLayout {
    private boolean mOpen;
    private final Context mContext;
    private final boolean mShouldAnimate;
    private final boolean mShouldKeepHistory;
    private boolean mClearingFocus;
    private LinearLayout mRoot;
    private LinearLayout mSearchBar;
    private EditText mSearchEditText;
    private ImageButton mBack;
    private ImageButton mClear;
    private ListView mSuggestionsListView;
    private CursorSearchAdapter mAdapter;
    private CharSequence mCurrentQuery;
    private OnQueryTextListener mOnQueryTextListener;
    private OnBackClickListener onBackClickListener;

    public List<String> getSuggestionsList() {
        return mAdapter.getStringList();
    }

    //region Constructors
    public MaterialSearchView(Context context) {
        this(context, null);
    }

    public MaterialSearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MaterialSearchView(Context context, AttributeSet attributeSet, int defStyleAttributes) {
        super(context, attributeSet);

        // Set variables
        this.mContext = context;
        this.mShouldAnimate = false;
        this.mShouldKeepHistory = true;

        // Initialize view
        init();

        // Initialize style
        initStyle(attributeSet, defStyleAttributes);
    }
    //endregion

    //region Initializers


    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    public interface OnBackClickListener {
        void onBackClickListener();
    }

    /**
     * Preforms any required initializations for the search view.
     */
    private void init() {
        // Inflate view
        LayoutInflater.from(mContext).inflate(R.layout.search_view, this, true);

        // Get items
        mRoot = findViewById(R.id.search_layout);
        mSearchBar = mRoot.findViewById(R.id.search_bar);
        mBack = mRoot.findViewById(R.id.action_back);
        mSearchEditText = mRoot.findViewById(R.id.et_search);
        mClear = mRoot.findViewById(R.id.action_clear);
        ImageButton mSettings = mRoot.findViewById(R.id.action_settings);
        mSuggestionsListView = mRoot.findViewById(R.id.suggestion_list);

        // Set click listeners
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClickListener.onBackClickListener();
            }
        });

        mSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_clear_history) {
                            clearAll();
                            dismissSuggestions();
                        }
                        return true;
                    }
                });
                inflater.inflate(R.menu.menu_settings, popup.getMenu());
                popup.show();
            }
        });

        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditText.setText("");
                dismissSuggestions();
            }
        });

        // Initialize the search view.
        initSearchView();

        mAdapter = new CursorSearchAdapter(mContext, getHistoryCursor());
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String filter = constraint.toString();

                if (filter.isEmpty()) {
                    return getHistoryCursor();
                } else {
                    return mContext.getContentResolver().query(
                            HistoryContract.HistoryEntry.CONTENT_URI,
                            null,
                            HistoryContract.HistoryEntry.COLUMN_QUERY + " LIKE ?",
                            new String[]{"%" + filter + "%"},
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " DESC, " +
                                    HistoryContract.HistoryEntry.COLUMN_QUERY
                    );
                }
            }
        });
        mSuggestionsListView.setAdapter(mAdapter);
        mSuggestionsListView.setTextFilterEnabled(true);
    }

    /**
     * Initializes the style of this view.
     *
     * @param attributeSet      The attributes to apply to the view.
     * @param defStyleAttribute An attribute to the style theme applied to this view.
     */
    private void initStyle(AttributeSet attributeSet, int defStyleAttribute) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.MaterialSearchView, defStyleAttribute, 0);

        if (typedArray != null) {
            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
                setBackground(typedArray.getDrawable(R.styleable.MaterialSearchView_searchBackground));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColor,
                        ContextCompat.getColor(mContext, R.color.black)));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(typedArray.getColor(R.styleable.MaterialSearchView_android_textColorHint,
                        ContextCompat.getColor(mContext, R.color.gray_50)));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_hint)) {
                setHint(typedArray.getString(R.styleable.MaterialSearchView_android_hint));
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
                setClearIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchCloseIcon,
                        R.drawable.ic_action_navigation_close)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchBackIcon,
                        R.drawable.ic_action_navigation_arrow_back)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(typedArray.getResourceId(
                        R.styleable.MaterialSearchView_searchSuggestionBackground,
                        R.color.search_layover_bg)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_android_inputType)) {
                setInputType(typedArray.getInteger(
                        R.styleable.MaterialSearchView_android_inputType,
                        InputType.TYPE_CLASS_TEXT)
                );
            }

            if (typedArray.hasValue(R.styleable.MaterialSearchView_searchBarHeight)) {
                setSearchBarHeight(typedArray.getDimensionPixelSize(R.styleable.MaterialSearchView_searchBarHeight, getAppCompatActionBarHeight()));
            } else {
                setSearchBarHeight(getAppCompatActionBarHeight());
            }

            typedArray.recycle();
        }
    }

    /**
     * Preforms necessary initializations on the SearchView.
     */
    private void initSearchView() {
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // When an edit occurs, submit the query.
                onSubmitQuery();
                return true;
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dismissSuggestions();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When the text changes, filter
                mAdapter.getFilter().filter(s.toString());
                mAdapter.notifyDataSetChanged();
                MaterialSearchView.this.onTextChanged();
                showSuggestions();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSearchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If we gain focus, show keyboard and show suggestions.
                if (hasFocus) {
                    showKeyboard(mSearchEditText);
                    dismissSuggestions();
                } else dismissSuggestions();
            }
        });
    }
    //endregion

    //region Show Methods

    /**
     * Displays the keyboard with a focus on the Search EditText.
     *
     * @param view The view to attach the keyboard to.
     */
    private void showKeyboard(View view) {
        view.requestFocus();

        if (!isHardKeyboardAvailable()) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    /**
     * Method that checks if there's a physical keyboard on the phone.
     *
     * @return true if there's a physical keyboard connected, false otherwise.
     */
    private boolean isHardKeyboardAvailable() {
        return mContext.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }

    /**
     * Changes the visibility of the clear button to VISIBLE or GONE.
     *
     * @param display True to display the clear button, false to hide it.
     */
    private void displayClearButton(boolean display) {
        mClear.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    /**
     * Displays the available suggestions, if any.
     */
    private void showSuggestions() {
        mSuggestionsListView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the SearchView.
     */
    public void openSearch() {
        // If search is already open, just return.
        if (mOpen) {
            return;
        }

        // Get focus
        mSearchEditText.setText("");
        mSearchEditText.requestFocus();

        if (mShouldAnimate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRoot.setVisibility(View.VISIBLE);
                AnimationUtils.circleRevealView(mSearchBar);
            } else {
                AnimationUtils.fadeInView(mRoot);
            }

        } else {
            mRoot.setVisibility(View.VISIBLE);
        }

        mOpen = true;
    }
    //endregion

    //region Hide Methods

    /**
     * Hides the suggestion list.
     */
    public void dismissSuggestions() {
        mSuggestionsListView.setVisibility(View.GONE);
    }

    public boolean isSuggestionVisible() {
        return mSuggestionsListView.isShown();
    }

    /**
     * Hides the keyboard displayed for the SearchEditText.
     *
     * @param view The view to detach the keyboard from.
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * Filters and updates the buttons when text is changed.
     */
    private void onTextChanged() {
        // Get current query
        mCurrentQuery = mSearchEditText.getText();

        // If the text is not empty, show the empty button and hide the voice button
        if (!TextUtils.isEmpty(mCurrentQuery))
            displayClearButton(true);
        else
            displayClearButton(false);

        /*// If we have a query listener and the text has changed, call it.
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextChange();
        }*/
    }

    /**
     * Called when a query is submitted. This will close the search view.
     */
    private void onSubmitQuery() {
        // Get the query.
        CharSequence query = mSearchEditText.getText();

        // If the query is not null and it has some text, submit it.
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {

            // If we don't have a listener, or if the search view handled the query, close it.
            // TODO - Improve.
            if (mOnQueryTextListener == null || !mOnQueryTextListener.onQueryTextSubmit(query.toString())) {

                if (mShouldKeepHistory) {
                    saveQueryToDb(query.toString(), System.currentTimeMillis());
                }

                // Refresh the cursor on the adapter,
                // so the new entry will be shown on the next time the user opens the search view.
//                refreshAdapterCursor();
                dismissSuggestions();
//                mSearchEditText.setText("");
            }
        }
    }


    //endregion

    //region Mutators
    public void setOnQueryTextListener(OnQueryTextListener mOnQueryTextListener) {
        this.mOnQueryTextListener = mOnQueryTextListener;
    }

    /**
     * Sets an OnItemClickListener to the suggestion list.
     *
     * @param listener - The ItemClickListener.
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSuggestionsListView.setOnItemClickListener(listener);
    }

    /**
     * Sets an OnItemLongClickListener to the suggestion list.
     *
     * @param listener - The ItemLongClickListener.
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mSuggestionsListView.setOnItemLongClickListener(listener);
    }

    /**
     * Set the query to search view. If submit is set to true, it'll submit the query.
     *
     * @param query - The Query value.
     */
    public void setQuery(CharSequence query) {
        mSearchEditText.setText(query);

        if (query != null) {
            mSearchEditText.setSelection(mSearchEditText.length());
            mCurrentQuery = query;
        }

        if (!TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    /**
     * Sets the background color of the SearchView.
     *
     * @param color The color to use for the background.
     */
    @Override
    public void setBackgroundColor(int color) {
//        setTintColor(color);
    }

    /**
     * Sets the text color of the EditText.
     *
     * @param color The color to use for the EditText.
     */
    private void setTextColor(int color) {
        mSearchEditText.setTextColor(color);
    }

    /**
     * Sets the text color of the search hint.
     *
     * @param color The color to be used for the hint text.
     */
    private void setHintTextColor(int color) {
        mSearchEditText.setHintTextColor(color);
    }

    /**
     * Sets the hint to be used for the search EditText.
     *
     * @param hint The hint to be displayed in the search EditText.
     */
    private void setHint(CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    /**
     * Sets the icon for the clear action.
     *
     * @param resourceId The resource ID of drawable that will represent the clear action.
     */
    private void setClearIcon(int resourceId) {
        mClear.setImageResource(resourceId);
    }

    /**
     * Sets the icon for the back action.
     *
     * @param resourceId The resource Id of the drawable that will represent the back action.
     */
    private void setBackIcon(int resourceId) {
        mBack.setImageResource(resourceId);
    }

    /**
     * Sets the background of the suggestions ListView.
     *
     * @param resource The resource to use as a background for the
     *                 suggestions listview.
     */
    private void setSuggestionBackground(int resource) {
        if (resource > 0) {
            mSuggestionsListView.setBackgroundResource(resource);
        }
    }

    /**
     * Sets the input type of the SearchEditText.
     *
     * @param inputType The input type to set to the EditText.
     */
    private void setInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }


    /**
     * Sets the bar height if prefered to not use the existing actionbar height value
     *
     * @param height The value of the height in pixels
     */
    private void setSearchBarHeight(final int height) {
        mSearchBar.setMinimumHeight(height);
        mSearchBar.getLayoutParams().height = height;
    }


    /**
     * Returns the actual AppCompat ActionBar height value. This will be used as the default
     *
     * @return The value of the actual actionbar height in pixels
     */
    private int getAppCompatActionBarHeight() {
        TypedValue tv = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
        return getResources().getDimensionPixelSize(tv.resourceId);
    }

    /*public CursorAdapter getAdapter() {
        return mAdapter;
    }*/
    //endregion*/

    //region Accessors

    /**
     * Gets the current text on the SearchView, if any. Returns an empty String if no text is available.
     *
     * @return The current query, or an empty String if there's no query.
     */
    public String getCurrentQuery() {
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            return mCurrentQuery.toString();
        }
        return "";
    }

    /**
     * Retrieves a suggestion at a given index in the adapter.
     *
     * @return The search suggestion for that index.
     */
    public String getSuggestionAtPosition(int position) {
        // If position is out of range just return empty string.
        if (position < 0 || position >= mAdapter.getCount()) {
            return "";
        } else {
            return mAdapter.getItem(position).toString();
        }
    }
    //endregion

    //region View Methods

    /**
     * Handles any cleanup when focus is cleared from the view.
     */
    @Override
    public void clearFocus() {
        this.mClearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        mSearchEditText.clearFocus();
        this.mClearingFocus = false;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept if we are clearing focus, or if the view isn't focusable.
        return !(mClearingFocus || !isFocusable()) && mSearchEditText.requestFocus(direction, previouslyFocusedRect);
    }

    //----- Lifecycle methods -----//

//    public void activityPaused() {
//        Cursor cursor = ((CursorAdapter)mAdapter).getCursor();
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//    }

    public void activityResumed() {
        refreshAdapterCursor();
    }
    //endregion

    //region Database Methods

    /**
     * Save a query to the local database.
     *
     * @param query - The query to be saved. Can't be empty or null.
     * @param ms    - The insert date, in millis. As a suggestion, use System.currentTimeMillis();
     **/
    private synchronized void saveQueryToDb(String query, long ms) {
        if (!TextUtils.isEmpty(query) && ms > 0) {
            ContentValues values = new ContentValues();

            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, query);
            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, ms);
            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 1); // Saving as history.

            mContext.getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI, values);
        }
    }

    public synchronized void addSuggestions(List<String> suggestions) {
        ArrayList<ContentValues> toSave = new ArrayList<>();
        for (String str : suggestions) {
            ContentValues value = new ContentValues();
            value.put(HistoryContract.HistoryEntry.COLUMN_QUERY, str);
            value.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, System.currentTimeMillis());
            value.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, 0); // Saving as suggestion.

            toSave.add(value);
        }

        ContentValues[] values = toSave.toArray(new ContentValues[0]);

        mContext.getContentResolver().bulkInsert(
                HistoryContract.HistoryEntry.CONTENT_URI,
                values
        );
    }

    private Cursor getHistoryCursor() {
        int MAX_HISTORY = BuildConfig.MAX_HISTORY;
        return mContext.getContentResolver().query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"},
                HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " DESC LIMIT " + MAX_HISTORY
        );
    }

    private void refreshAdapterCursor() {
        Cursor historyCursor = getHistoryCursor();
        mAdapter.changeCursor(historyCursor);
    }

    public synchronized void clearSuggestions() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"0"}
        );
    }

    public synchronized void removeHistory(String suggestion) {
        if (!TextUtils.isEmpty(suggestion)) {
            mContext.getContentResolver().delete(
                    HistoryContract.HistoryEntry.CONTENT_URI,
                    HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_QUERY +
                            " = ? AND " +
                            HistoryContract.HistoryEntry.TABLE_NAME +
                            "." +
                            HistoryContract.HistoryEntry.COLUMN_IS_HISTORY +
                            " = ?"
                    ,
                    new String[]{suggestion, String.valueOf(1)}
            );
        }
        refreshAdapterCursor();
    }

    private synchronized void clearAll() {
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        );
    }
//endregion

//region Interfaces

    /**
     * Interface that handles the submission and change of search queries.
     */
    public interface OnQueryTextListener {
        /**
         * Called when a search query is submitted.
         *
         * @param query The text that will be searched.
         * @return True when the query is handled by the listener, false to let the SearchView handle the default case.
         */
        @SuppressWarnings("SameReturnValue")
        boolean onQueryTextSubmit(String query);

       /* @SuppressWarnings("SameReturnValue")
        void onQueryTextChange();*/
    }

    //endregion
}

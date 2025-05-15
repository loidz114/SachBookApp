package com.example.sachbook.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sachbook.R;
import com.example.sachbook.data.model.BookModel;
import com.example.sachbook.data.repository.BookRepository;
import com.example.sachbook.ui.adapter.BookAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private TextInputEditText searchView;
    private MaterialButton searchButton;
    private ImageButton backButton;
    private TextView searchQueryText;
    private RecyclerView searchResultsRecyclerView;
    private TextView noResultsText;
    private ProgressBar progressBar;
    private BookRepository repository;
    private NavController navController;
    private BookAdapter bookAdapter;
    private Handler handler;
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize repository
        repository = new BookRepository(requireContext());

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        // Bind views
        searchView = view.findViewById(R.id.searchView);
        searchButton = view.findViewById(R.id.searchButton);
        backButton = view.findViewById(R.id.backButton);
        searchQueryText = view.findViewById(R.id.searchQueryText);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);
        progressBar = view.findViewById(R.id.progressBar);

        // Set up NavController
        navController = NavHostFragment.findNavController(this);

        // Set up RecyclerView with BookAdapter
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        bookAdapter = new BookAdapter(requireContext(), new ArrayList<>());
        searchResultsRecyclerView.setAdapter(bookAdapter);
        bookAdapter.setOnBookClickListener(this);

        // Add spacing between items
        searchResultsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));

        // Handle back button
        backButton.setOnClickListener(v -> navigateBack());

        // Handle search button click
        searchButton.setOnClickListener(v -> performSearch());

        // Handle Enter key press for search
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                return true;
            }
            return false;
        });

        // Real-time search with debounce
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        performSearch();
                    } else {
                        bookAdapter.updateBooks(new ArrayList<>());
                        noResultsText.setVisibility(View.VISIBLE);
                        searchQueryText.setText("");
                    }
                };
                handler.postDelayed(searchRunnable, 500); // Debounce 500ms
            }
        });

        // Load search query from arguments (if passed from HomeFragment)
        Bundle args = getArguments();
        if (args != null && args.containsKey("searchQuery")) {
            String query = args.getString("searchQuery", "");
            searchView.setText(query);
            searchQueryText.setText(query.isEmpty() ? "" : getString(R.string.search_results_title, query));
            if (!query.isEmpty()) {
                performSearch();
            }
        }

        return view;
    }

    private void navigateBack() {
        // Clear any pending search callbacks
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }

        // Clear focus and hide keyboard
        if (searchView.hasFocus()) {
            searchView.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }

        // Navigate back
        try {
            navController.navigateUp();
        } catch (IllegalArgumentException e) {
            Log.e("SearchFragment", "Navigation error: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Không thể quay lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch() {
        String query = searchView.getText().toString().trim().replaceAll("\\s+", " ");
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        // Update UI
        searchQueryText.setText(getString(R.string.search_results_title, query));
        noResultsText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Call API to search books
        Call<List<BookModel>> call = repository.searchBooksByKeyword(query);
        call.enqueue(new Callback<List<BookModel>>() {
            @Override
            public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<BookModel> books = response.body();
                    bookAdapter.updateBooks(books);
                    noResultsText.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(requireContext(), "Đã tìm thấy " + books.size() + " kết quả", Toast.LENGTH_SHORT).show();
                } else {
                    noResultsText.setVisibility(View.VISIBLE);
                    bookAdapter.updateBooks(new ArrayList<>());
                    Toast.makeText(requireContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noResultsText.setVisibility(View.VISIBLE);
                bookAdapter.updateBooks(new ArrayList<>());
                Log.e("SearchFragment", "Network error: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBookClick(BookModel book, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("bookId", book.getId());
        bundle.putString("bookTitle", book.getTitle());
        try {
            navController.navigate(R.id.action_search_to_book_detail, bundle);
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Không thể mở chi tiết sách", Toast.LENGTH_SHORT).show();
            Log.e("SearchFragment", "Navigation error: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler callbacks
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }

    // Custom ItemDecoration for GridLayoutManager
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // bottom edge
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
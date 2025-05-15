package com.example.sachbook.ui.fragment;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sachbook.R;
import com.example.sachbook.data.model.BookModel;
import com.example.sachbook.data.model.CategoryModel;
import com.example.sachbook.data.repository.BookRepository;
import com.example.sachbook.ui.adapter.BookAdapter;
import com.example.sachbook.ui.adapter.CategoryAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, BookAdapter.OnBookClickListener {

    private TextInputEditText searchView;
    private MaterialButton searchButton;
    private ViewPager2 bannerViewPager;
    private RecyclerView rvCategories, rvPopularBooks;
    private ProgressBar progressBar;
    private TextView noDataText;
    private BookRepository repository;
    private NavController navController;
    private CategoryAdapter categoryAdapter;
    private BookAdapter bookAdapter;
    private Handler handler;
    private Runnable searchRunnable;
    private TextWatcher searchTextWatcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize repository
        repository = new BookRepository(requireContext());

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        // Bind views
        searchView = view.findViewById(R.id.searchView);
        searchButton = view.findViewById(R.id.searchButton);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvPopularBooks = view.findViewById(R.id.rvPopularBooks);
        progressBar = view.findViewById(R.id.progressBar);
        noDataText = view.findViewById(R.id.noDataText);

        // Set up NavController
        navController = NavHostFragment.findNavController(this);

        // Set up RecyclerView for categories
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(requireContext(), new ArrayList<>());
        rvCategories.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryClickListener(this);

        // Set up RecyclerView for popular books
        rvPopularBooks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        bookAdapter = new BookAdapter(requireContext(), new ArrayList<>());
        rvPopularBooks.setAdapter(bookAdapter);
        bookAdapter.setOnBookClickListener(this);

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
        searchTextWatcher = new TextWatcher() {
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
                    }
                };
                handler.postDelayed(searchRunnable, 800); // Debounce 800ms
            }
        };
        searchView.addTextChangedListener(searchTextWatcher);

        // Load data
        loadCategories();
        loadPopularBooks();
        setupBanner();

        return view;
    }

    private void performSearch() {
        String query = searchView.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", query);
            navController.navigate(R.id.action_home_to_search, bundle);
        }
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<CategoryModel>> call = repository.getCategories();
        call.enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryModel> categories = response.body();
                    Log.d("CategoryResponse", new Gson().toJson(categories));
                    categoryAdapter.updateCategories(categories);
                    noDataText.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(requireContext(), "Đã tải " + categories.size() + " danh mục", Toast.LENGTH_SHORT).show();
                } else {
                    noDataText.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Không tải được danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noDataText.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularBooks() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<BookModel>> call = repository.getNewBooks();
        call.enqueue(new Callback<List<BookModel>>() {
            @Override
            public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<BookModel> books = response.body();
                    Log.d("BookResponse", new Gson().toJson(books));
                    bookAdapter.updateBooks(books);
                    noDataText.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(requireContext(), "Đã tải " + books.size() + " sách", Toast.LENGTH_SHORT).show();
                } else {
                    noDataText.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Không tải được sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noDataText.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBanner() {
        List<String> banners = new ArrayList<>();
        banners.add("banner1");
        banners.add("banner2");
        banners.add("banner3");
        // TODO: Implement BannerAdapter for ViewPager2
        // Example: bannerViewPager.setAdapter(new BannerAdapter(banners));
    }

    @Override
    public void onCategoryClick(CategoryModel category, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", category.getId());
        bundle.putString("categoryName", category.getName());
        navController.navigate(R.id.action_home_to_book_list, bundle);
    }

    @Override
    public void onBookClick(BookModel book, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("bookId", book.getId());
        bundle.putString("bookTitle", book.getTitle());
        navController.navigate(R.id.action_home_to_book_detail, bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove TextWatcher
        if (searchTextWatcher != null) {
            searchView.removeTextChangedListener(searchTextWatcher);
        }
        // Remove any pending search callbacks
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        // Clear searchView content
        searchView.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-attach TextWatcher when fragment resumes
        if (searchTextWatcher != null) {
            searchView.addTextChangedListener(searchTextWatcher);
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
}
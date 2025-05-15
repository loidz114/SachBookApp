package com.example.sachbook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private RecyclerView recyclerViewBooks;
    private ProgressBar progressBar;
    private TextView noBooksText;
    private TextView bookListTitle;
    private ImageView backArrow;
    private BookRepository repository;
    private NavController navController;
    private BookAdapter bookAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Initialize repository
        repository = new BookRepository(requireContext());

        // Initialize NavController
        navController = NavHostFragment.findNavController(this);

        // Bind views
        recyclerViewBooks = view.findViewById(R.id.recycler_view_books);
        progressBar = view.findViewById(R.id.progressBar);
        noBooksText = view.findViewById(R.id.noBooksText);
        bookListTitle = view.findViewById(R.id.bookListTitle);
        backArrow = view.findViewById(R.id.backArrow);

        // Set up RecyclerView
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        bookAdapter = new BookAdapter(requireContext(), new ArrayList<>());
        recyclerViewBooks.setAdapter(bookAdapter);
        bookAdapter.setOnBookClickListener(this);

        // Set up back arrow click listener
        backArrow.setOnClickListener(v -> {
            try {
                navController.navigate(R.id.action_book_list_to_home);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Unable to navigate back", Toast.LENGTH_SHORT).show();
            }
        });

        // Load books based on category
        Bundle args = getArguments();
        if (args != null && args.containsKey("categoryId")) {
            long categoryId = args.getLong("categoryId");
            String categoryName = args.getString("categoryName", "Danh sách sách");
            bookListTitle.setText(categoryName);
            loadBooksByCategory(categoryId);
        } else {
            bookListTitle.setText("Danh sách sách");
            loadBooks();
        }

        return view;
    }

    private void loadBooks() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<BookModel>> call = repository.getNewBooks();
        call.enqueue(new Callback<List<BookModel>>() {
            @Override
            public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<BookModel> books = response.body();
                    bookAdapter.updateBooks(books);
                    noBooksText.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(requireContext(), "Loaded " + books.size() + " books", Toast.LENGTH_SHORT).show();
                } else {
                    noBooksText.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noBooksText.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBooksByCategory(long categoryId) {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<BookModel>> call = repository.searchBooksByCategory(categoryId);
        call.enqueue(new Callback<List<BookModel>>() {
            @Override
            public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<BookModel> books = response.body();
                    bookAdapter.updateBooks(books);
                    noBooksText.setVisibility(books.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(requireContext(), "Loaded " + books.size() + " books", Toast.LENGTH_SHORT).show();
                } else {
                    noBooksText.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                noBooksText.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBookClick(BookModel book, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong("bookId", book.getId());
        bundle.putString("bookTitle", book.getTitle());
        try {
            navController.navigate(R.id.action_book_list_to_book_detail, bundle);
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Unable to open book details", Toast.LENGTH_SHORT).show();
        }
    }
}
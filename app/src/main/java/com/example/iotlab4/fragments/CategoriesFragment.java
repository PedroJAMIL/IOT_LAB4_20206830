package com.example.iotlab4.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.iotlab4.R;
import com.example.iotlab4.adapters.CategoryAdapter;
import com.example.iotlab4.databinding.FragmentCategoriesBinding;
import com.example.iotlab4.models.Category;
import com.example.iotlab4.network.MealApi;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.progressBar.setVisibility(View.VISIBLE);

        cargarCategorias();
    }

    private void cargarCategorias() {
        new Thread(() -> {
            try {
                List<Category> lista = MealApi.getCategories();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);

                    if (lista.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    CategoryAdapter adapter = new CategoryAdapter(lista, category -> {
                        Bundle args = new Bundle();
                        args.putString("strCategory", category.getStrCategory());
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_categories_to_meals, args);
                    });
                    binding.rvCategories.setAdapter(adapter);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText("Error al cargar categorías");
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
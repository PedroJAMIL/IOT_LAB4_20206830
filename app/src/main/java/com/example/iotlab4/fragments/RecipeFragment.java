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

import com.bumptech.glide.Glide;
import com.example.iotlab4.databinding.FragmentRecipeBinding;
import com.example.iotlab4.models.MealDetail;
import com.example.iotlab4.network.MealApi;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBuscarId.setOnClickListener(v -> {
            String id = binding.etIdMeal.getText().toString().trim();
            if (id.isEmpty()) {
                Toast.makeText(getContext(), "Ingresa un ID válido", Toast.LENGTH_SHORT).show();
                return;
            }
            cargarReceta(id);
        });

        Bundle args = getArguments();
        if (args != null) {
            String idMeal = args.getString("idMeal", null);
            if (idMeal != null && !idMeal.isEmpty()) {
                binding.etIdMeal.setText(idMeal);
                cargarReceta(idMeal);
            }
        }
    }

    private void cargarReceta(String idMeal) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutDetalle.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                MealDetail detail = MealApi.getMealDetail(idMeal);
                new Handler(Looper.getMainLooper()).post(() -> mostrarDetalle(detail));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding == null) return;
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void mostrarDetalle(MealDetail detail) {
        if (binding == null) return;
        binding.progressBar.setVisibility(View.GONE);

        if (detail == null) {
            Toast.makeText(getContext(), "No se encontró el plato", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.layoutDetalle.setVisibility(View.VISIBLE);
        binding.tvNombre.setText(detail.getStrMeal());
        binding.tvCategoria.setText("Categoría: " + detail.getStrCategory());
        binding.tvArea.setText("Origen: " + detail.getStrArea());
        binding.tvInstrucciones.setText(detail.getStrInstructions());

        Glide.with(requireContext())
                .load(detail.getStrMealThumb())
                .into(binding.imgPlato);

        StringBuilder sb = new StringBuilder();
        for (String ing : detail.getIngredients()) {
            sb.append("• ").append(ing).append("\n");
        }
        binding.tvIngredientes.setText(sb.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
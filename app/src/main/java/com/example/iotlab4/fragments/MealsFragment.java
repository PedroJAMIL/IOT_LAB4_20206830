package com.example.iotlab4.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.iotlab4.adapters.MealAdapter;
import com.example.iotlab4.databinding.FragmentMealsBinding;
import com.example.iotlab4.models.Meal;
import com.example.iotlab4.models.MealDetail;
import com.example.iotlab4.network.MealApi;

import java.util.List;

public class MealsFragment extends Fragment implements SensorEventListener {

    private FragmentMealsBinding binding;
    private SensorManager sensorManager;
    private Sensor acelerometro;
    private long ultimoShake = 0;
    private static final float UMBRAL = 4.0f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvMeals.setLayoutManager(new LinearLayoutManager(getContext()));

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        Bundle args = getArguments();
        String categoria = (args != null) ? args.getString("strCategory", null) : null;

        if (categoria != null && !categoria.isEmpty()) {
            binding.tvTitulo.setText("Platos de: " + categoria);
            binding.layoutBuscar.setVisibility(View.GONE);
            cargarPorCategoria(categoria);
        } else {
            binding.tvTitulo.setText("Buscar por ingrediente");
            binding.layoutBuscar.setVisibility(View.VISIBLE);

            binding.btnBuscar.setOnClickListener(v -> {
                String ing = binding.etIngrediente.getText().toString().trim();
                if (ing.isEmpty()) {
                    Toast.makeText(getContext(), "Ingresa un ingrediente", Toast.LENGTH_SHORT).show();
                    return;
                }
                cargarPorIngrediente(ing);
            });
        }
    }

    private void cargarPorCategoria(String categoria) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
        new Thread(() -> {
            try {
                List<Meal> lista = MealApi.getMealsByCategory(categoria);
                new Handler(Looper.getMainLooper()).post(() -> mostrarLista(lista));
            } catch (Exception e) {
                mostrarError(e);
            }
        }).start();
    }

    private void cargarPorIngrediente(String ingrediente) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
        new Thread(() -> {
            try {
                List<Meal> lista = MealApi.getMealsByIngredient(ingrediente);
                new Handler(Looper.getMainLooper()).post(() -> mostrarLista(lista));
            } catch (Exception e) {
                mostrarError(e);
            }
        }).start();
    }

    private void mostrarLista(List<Meal> lista) {
        if (binding == null) return;
        binding.progressBar.setVisibility(View.GONE);

        if (lista.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText("No se encontraron platos");
            binding.rvMeals.setAdapter(null);
            return;
        }

        binding.tvEmpty.setVisibility(View.GONE);
        MealAdapter adapter = new MealAdapter(lista, meal -> {
            Bundle args = new Bundle();
            args.putString("idMeal", meal.getIdMeal());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_meals_to_recipe, args);
        });
        binding.rvMeals.setAdapter(adapter);
    }

    private void mostrarError(Exception e) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.GONE);
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText("Error al cargar datos");
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float magnitud = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

        if (magnitud > UMBRAL) {
            long ahora = System.currentTimeMillis();
            if (ahora - ultimoShake < 2000) return;
            ultimoShake = ahora;

            obtenerRecetaAleatoria();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void obtenerRecetaAleatoria() {
        Toast.makeText(getContext(), "¡Movimiento detectado! Buscando receta...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                MealDetail detail = MealApi.getRandomMeal();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (binding == null || detail == null) return;
                    Bundle args = new Bundle();
                    args.putString("idMeal", detail.getIdMeal());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_meals_to_recipe, args);
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getContext(), "Error al obtener receta aleatoria", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.estrada.budget.util;

import java.time.YearMonth;
import com.estrada.budget.model.Budget;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonPersistence {

    private static final String APP_DATA_DIR = System.getProperty("user.home") + "/EstradaFamilyBudget";
    private static final String FILE_NAME = "budget.json";
    private static final Path DATA_PATH = Paths.get(APP_DATA_DIR, FILE_NAME);

    private final ObjectMapper mapper;

    public JsonPersistence() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // Required for LocalDate, YearMonth, etc.
        mapper.findAndRegisterModules();             // Auto-discover other modules if needed

        // Create the app data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(APP_DATA_DIR));
            System.out.println("App data directory ready: " + APP_DATA_DIR);
        } catch (IOException e) {
            System.err.println("Failed to create app data directory: " + e.getMessage());
        }
    }

    /**
     * Saves the current Budget object to JSON file.
     */
    public void save(Budget budget) throws IOException {
        if (budget == null) {
            System.err.println("Cannot save null budget.");
            return;
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(DATA_PATH.toFile(), budget);
        System.out.println("Budget saved successfully to: " + DATA_PATH);
    }

    /**
     * Loads the Budget from JSON file.
     * If file is missing → returns new default Budget
     * If file is corrupt/invalid → deletes it and returns new default Budget
     */
    public Budget load() throws IOException {
        if (!Files.exists(DATA_PATH)) {
            System.out.println("No saved budget file found. Starting with default budget.");
            return new Budget();
        }

        System.out.println("Loading budget from: " + DATA_PATH);

        try {
            Budget loaded = mapper.readValue(DATA_PATH.toFile(), Budget.class);

            // Extra safety: ensure currentMonth is never null after load
            if (loaded.getCurrentMonth() == null) {
                loaded.setCurrentMonth(YearMonth.now());
                System.out.println("Fixed null currentMonth after loading.");
            }

            return loaded;

        } catch (Exception e) {
            // Handle corrupt/incomplete JSON
            System.err.println("Failed to load budget JSON: " + e.getMessage());
            System.err.println("Deleting corrupt file and starting with default budget.");

            // Auto-delete the bad file
            try {
                Files.deleteIfExists(DATA_PATH);
                System.out.println("Corrupt file deleted: " + DATA_PATH);
            } catch (IOException deleteEx) {
                System.err.println("Failed to delete corrupt file: " + deleteEx.getMessage());
            }

            // Return fresh budget
            return new Budget();
        }
    }

    // Optional: Getter for the file path (useful for debugging or UI display)
    public Path getDataPath() {
        return DATA_PATH;
    }
}
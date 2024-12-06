
package com.monster.npd.governance.pdf.helpers;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.monster.npd.governance.pdf.utils.Utils;

public class CheckpointHandler {
    private static final Map<String, CheckpointDetails> checkpointMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CheckpointHandler.class);
    private static final String CP = "CP";

    static {
        checkpointMap.put("CP0", new CheckpointDetails("CHECKPOINT 0", new Color(186, 140, 220)));
        checkpointMap.put("CP1", new CheckpointDetails("CHECKPOINT 1", new Color(68, 114, 196)));
        checkpointMap.put("CP2", new CheckpointDetails("CHECKPOINT 2", new Color(255, 253, 2)));
        checkpointMap.put("CP3", new CheckpointDetails("CHECKPOINT 3", new Color(0, 176, 240)));
        checkpointMap.put("CP4", new CheckpointDetails("CHECKPOINT 4", new Color(140, 216, 114)));
    }

    private CheckpointHandler() {
    }

    public static CheckpointDetails getCheckpointDetails(String value) {
        String upperValue = value.toUpperCase();
        CheckpointDetails details = checkpointMap.get(upperValue);
        if (Utils.isNullOrEmptyObject(details)) {
            logger.warn("Checkpoint lookup failed: '{}' is not a valid checkpoint.", value);
            return new CheckpointDetails(
                    Utils.isNullOrEmptyString(value) ? "Oops! Checkpoint '" + value + "' is not identified." : value,
                    Color.LIGHT_GRAY);
        }
        return details;
    }

    public static Set<String> getCPSummary(String cpName) {
        Set<String> targetCpNames = new HashSet<>();
        if (!cpName.isEmpty() && cpName.startsWith(CP)) {
            try {
                int cpNumber = Integer.parseInt(cpName.substring(2));
                for (int i = 0; i <= cpNumber; i++) {
                    targetCpNames.add(CP + i);
                }
                targetCpNames.add(cpName);
            } catch (NumberFormatException e) {
                logger.error("Invalid CP name format: {}", cpName, e);
            }
        }
        return targetCpNames;
    }

    public static class CheckpointDetails {
        private final String name;
        private final Color baseColor;

        public CheckpointDetails(String name, Color baseColor) {
            this.name = name;
            this.baseColor = baseColor;
        }

        public String getName() {
            return name;
        }

        public Color getBaseColor() {
            return baseColor;
        }
    }
}


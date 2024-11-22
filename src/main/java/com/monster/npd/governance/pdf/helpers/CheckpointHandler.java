package com.monster.npd.governance.pdf.helpers;

import com.itextpdf.text.BaseColor;
import com.monster.npd.governance.pdf.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckpointHandler {
	private static final Map<String, CheckpointDetails> checkpointMap = new HashMap<>();
	private static final Logger logger = LogManager.getLogger(CheckpointHandler.class);
	static {
		checkpointMap.put("CP0", new CheckpointDetails("CHECKPOINT 0", new BaseColor(186, 140, 220)));
		checkpointMap.put("CP1", new CheckpointDetails("CHECKPOINT 1", new BaseColor(68, 114, 196)));
		checkpointMap.put("CP2", new CheckpointDetails("CHECKPOINT 2", new BaseColor(255, 253, 2)));
		checkpointMap.put("CP3", new CheckpointDetails("CHECKPOINT 3", new BaseColor(0, 176, 240)));
		checkpointMap.put("CP4", new CheckpointDetails("CHECKPOINT 4", new BaseColor(140, 216, 114)));
	}

	public static CheckpointDetails getCheckpointDetails(String value) {
		if (Utils.isNullOrEmptyObject(checkpointMap.get(value.toUpperCase()))) {
			logger.warn("Checkpoint lookup failed: '{}' is not a valid checkpoint.", value);

			return new CheckpointDetails(
					Utils.isNullOrEmptyString(value) ? "Oops! Checkpoint '" + value + "' is not identified." : value,
					BaseColor.LIGHT_GRAY);
		}
		return checkpointMap.get(value.toUpperCase());
	}

	public static class CheckpointDetails {
		private final String name;
		private final BaseColor baseColor;

		public CheckpointDetails(String name, BaseColor baseColor) {
			this.name = name;
			this.baseColor = baseColor;
		}

		public String getName() {
			return name;
		}

		public BaseColor getBaseColor() {
			return baseColor;
		}
	}
}

package com.monster.npd.governance.pdf.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckpointData {
    private MetricData anualizedValue;
    private MetricData threeMVolume; // renamed 3M volume for valid Java naming
    private MetricData nsv;
    private MetricData dp; // DP is kept as-is since it's a valid name.

    
}


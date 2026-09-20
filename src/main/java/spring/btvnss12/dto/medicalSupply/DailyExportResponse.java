package spring.btvnss12.dto.medicalSupply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyExportResponse {
    private Long supplyId;
    private String supplyName;
    private Long totalExportQuantity;
}
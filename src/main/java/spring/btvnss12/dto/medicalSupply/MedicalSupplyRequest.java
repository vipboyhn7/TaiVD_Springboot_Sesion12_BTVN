package spring.btvnss12.dto.medicalSupply;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalSupplyRequest {
    @NotBlank(message = "Tên vật tư không được để trống!")
    private String name;

    private String specification;

    private String provider;

    private String unit;
}

package spring.btvnss12.service;

import org.springframework.data.domain.Pageable;
import spring.btvnss12.dto.PageResponse;
import spring.btvnss12.dto.medicalSupply.AmountRequest;
import spring.btvnss12.dto.medicalSupply.MedicalSupplyRequest;
import spring.btvnss12.entity.MedicalSupply;

import java.util.Map;

public interface MedicalSupplyService {
    MedicalSupply createMedicalSupply(MedicalSupplyRequest request);

    MedicalSupply updateMedicalSupply(Long id, Map<String, Object> request);

    void deleteMedicalSupply(Long id);

    PageResponse<MedicalSupply> getAllMedicalSupply(Pageable pageable);

    PageResponse<MedicalSupply> searchMedicalSupply(String name, Pageable pageable);

    MedicalSupply exportMedicalSupply(Long id, AmountRequest amount);

    MedicalSupply importMedicalSupply(Long id, AmountRequest amount);
}

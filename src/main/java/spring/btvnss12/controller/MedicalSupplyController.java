package spring.btvnss12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.btvnss12.dto.ApiResponse;
import spring.btvnss12.dto.PageResponse;
import spring.btvnss12.dto.medicalSupply.AmountRequest;
import spring.btvnss12.dto.medicalSupply.MedicalSupplyRequest;
import spring.btvnss12.entity.MedicalSupply;
import spring.btvnss12.service.MedicalSupplyService;

import java.util.Map;

@RestController
@RequestMapping("api/v1/supplies")
@RequiredArgsConstructor
@Slf4j
public class MedicalSupplyController {
    private final MedicalSupplyService medicalSupplyService;

    @PostMapping
    public ResponseEntity<ApiResponse<MedicalSupply>> createMedicalSupply(@Valid @RequestBody MedicalSupplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Thêm mới vật tư thành công!", medicalSupplyService.createMedicalSupply(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalSupply>> updateMedicalSupply(@PathVariable Long id, @Valid @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Cập nhật vật tư thành công!", medicalSupplyService.updateMedicalSupply(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicalSupply(@PathVariable Long id) {
        medicalSupplyService.deleteMedicalSupply(id);
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Xóa vật tư thành công!"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<PageResponse<MedicalSupply>>> getAllMedicalSupplies(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Lấy danh sách vật tư thành công!", medicalSupplyService.getAllMedicalSupply(pageable)));
    }

    @GetMapping("/srearch")
    public ResponseEntity<ApiResponse<PageResponse<MedicalSupply>>> searchMedicalSupply(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Lấy danh sách vật tư thành công!", medicalSupplyService.searchMedicalSupply(name, pageable)));
    }

    @PatchMapping("/{id}/export")
    public ResponseEntity<ApiResponse<MedicalSupply>> exportMedicalSupply(@PathVariable Long id, @Valid @RequestBody AmountRequest amount) {
        MedicalSupply medicalSupply = medicalSupplyService.exportMedicalSupply(id, amount);
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Xuất kho vật tư thành công!", medicalSupply));
    }

    @PatchMapping("/{id}/import")
    public ResponseEntity<ApiResponse<MedicalSupply>> importMedicalSupply(@PathVariable Long id, @Valid @RequestBody AmountRequest amount) {
        MedicalSupply medicalSupply = medicalSupplyService.importMedicalSupply(id, amount);
        return ResponseEntity.ok()
                .body(ApiResponse.success(200, "Nhập kho vật tư thành công!", medicalSupply));
    }

}

package spring.btvnss12.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.btvnss12.dto.PageResponse;
import spring.btvnss12.dto.medicalSupply.AmountRequest;
import spring.btvnss12.dto.medicalSupply.MedicalSupplyRequest;
import spring.btvnss12.entity.MedicalSupply;
import spring.btvnss12.entity.SupplyTransaction;
import spring.btvnss12.entity.TransactionType;
import spring.btvnss12.exception.AppException;
import spring.btvnss12.repository.MedicalSupplyRepository;
import spring.btvnss12.repository.SupplyTransactionRepository;
import spring.btvnss12.service.MedicalSupplyService;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalSupplyServiceImpl implements MedicalSupplyService {
    private final MedicalSupplyRepository medicalSupplyRepository;
    private final SupplyTransactionRepository supplyTransactionRepository;

    @Override
    @Transactional
    public MedicalSupply createMedicalSupply(MedicalSupplyRequest request) {
        log.info("Bắt đầu thêm mới vật tư!");
        MedicalSupply medicalSupply = MedicalSupply.builder()
                .name(request.getName())
                .specification(request.getSpecification())
                .provider(request.getProvider())
                .unit(request.getUnit())
                .build();
        MedicalSupply saved = medicalSupplyRepository.save(medicalSupply);
        log.info("Đã tạo mới vật tư: [{}] với ID: [{}]", saved.getName(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public MedicalSupply updateMedicalSupply(Long id, Map<String, Object> request) {
        if (request.containsKey("id") || request.containsKey("quantity")) {
            log.warn("Client cố tình gửi dữ liệu cấm khi cập nhật vật tư ID: {}. Payload keys: {}", id, request.keySet());
            throw AppException.badRequest("Yêu cầu không hợp lệ: Không được phép cập nhật ID hoặc số lượng tồn kho qua API này");
        }

        MedicalSupply supply = medicalSupplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy vật tư với ID: " + id));

        if (request.containsKey("name")) {
            String name = (String) request.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Tên vật tư không được để trống");
            }
            supply.setName(name.trim());
        }
        if (request.containsKey("specification")) {
            supply.setSpecification((String) request.get("specification"));
        }
        if (request.containsKey("provider")) {
            supply.setProvider((String) request.get("provider"));
        }

        return medicalSupplyRepository.save(supply);
    }

    @Override
    @Transactional
    public void deleteMedicalSupply(Long id) {
        MedicalSupply supply = medicalSupplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy Vật tư có id: " + id));
        supply.setIsDeleted(true);
        medicalSupplyRepository.save(supply);
    }

    @Override
    public PageResponse<MedicalSupply> getAllMedicalSupply(Pageable pageable) {
        Page<MedicalSupply> medicalSupplies = medicalSupplyRepository.findAllByIsDeletedFalse(pageable);
        log.debug("Đã truy vấn thành công danh sách vật tư y tế. Số lượng bản ghi: {}", medicalSupplies.getNumberOfElements());
        return PageResponse.from(medicalSupplies);
    }

    @Override
    public PageResponse<MedicalSupply> searchMedicalSupply(String name, Pageable pageable) {
        Page<MedicalSupply> medicalSupplies = medicalSupplyRepository.findAllByName(name.trim(), pageable);
        if (medicalSupplies.isEmpty()) {
            log.info("Không tìm thấy vật tư có tên: " + name);
        }
        return PageResponse.from(medicalSupplies);
    }

    @Override
    public MedicalSupply exportMedicalSupply(Long id, AmountRequest amount) {
        MedicalSupply medicalSupply = medicalSupplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy Vật tư có id: " + id));

        if (medicalSupply.getQuantity() < amount.getAmount()) {
            log.error("Thất bại khi xuất kho ID [{}]: Yêu cầu [{}], hiện có [{}]", id, amount.getAmount(), medicalSupply.getQuantity());
            throw AppException.badRequest("Số lượng vật tư không đủ");
        }

        medicalSupply.setQuantity(medicalSupply.getQuantity() - amount.getAmount());
        MedicalSupply updateMedical = medicalSupplyRepository.save(medicalSupply);
        SupplyTransaction transaction = SupplyTransaction.builder()
                .supply(updateMedical)
                .type(TransactionType.EXPORT)
                .amount(amount.getAmount())
                .transactionTime(LocalDateTime.now())
                .build();
        supplyTransactionRepository.save(transaction);
        log.info("Thành công khi xuất kho ID [{}]: Yêu cầu [{}], hiện có [{}]", id, amount.getAmount(), updateMedical.getQuantity());
        return updateMedical;
    }

    public MedicalSupply importMedicalSupply(Long id, AmountRequest amount) {
        MedicalSupply medicalSupply = medicalSupplyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy Vật tư có id: " + id));
        medicalSupply.setQuantity(medicalSupply.getQuantity() + amount.getAmount());
        MedicalSupply updateMedical = medicalSupplyRepository.save(medicalSupply);
        SupplyTransaction transaction = SupplyTransaction.builder()
                .supply(updateMedical)
                .type(TransactionType.IMPORT)
                .amount(amount.getAmount())
                .transactionTime(LocalDateTime.now())
                .build();
        supplyTransactionRepository.save(transaction);
        log.info("Thành công khi nhập kho ID [{}]: Yêu cầu [{}], hiện có [{}]", id, amount.getAmount(), updateMedical.getQuantity());
        return updateMedical;
    }

}

package spring.btvnss12.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.btvnss12.dto.medicalSupply.DailyExportResponse;
import spring.btvnss12.entity.MedicalSupply;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalSupplyRepository extends JpaRepository<MedicalSupply, Long> {
    Optional<MedicalSupply> findByIdAndIsDeletedFalse(Long id);

    Page<MedicalSupply> findAllByIsDeletedFalse(Pageable pageable);

    @Query("""
            SELECT s FROM MedicalSupply s 
            WHERE s.isDeleted = false
                AND (s.name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%',:name,'%')))
            """)
    Page<MedicalSupply> findAllByName(@Param("name") String name, Pageable pageable);


}

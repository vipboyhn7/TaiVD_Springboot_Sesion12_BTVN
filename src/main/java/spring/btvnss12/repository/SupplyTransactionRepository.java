package spring.btvnss12.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.btvnss12.dto.medicalSupply.DailyExportResponse;
import spring.btvnss12.entity.SupplyTransaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplyTransactionRepository extends JpaRepository<SupplyTransaction, Long> {
    @Query("SELECT new spring.btvnss12.dto.medicalSupply.DailyExportResponse(s.id, s.name, SUM(t.amount)) " +
            "FROM SupplyTransaction t JOIN t.supply s " +
            "WHERE t.type = spring.btvnss12.entity.TransactionType.EXPORT " +
            "AND t.transactionTime BETWEEN :startOfDay AND :endOfDay " +
            "GROUP BY s.id, s.name")
    List<DailyExportResponse> findDailyExports(@Param("startOfDay") LocalDateTime startOfDay,
                                               @Param("endOfDay") LocalDateTime endOfDay);
}

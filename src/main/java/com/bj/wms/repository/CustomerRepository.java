package com.bj.wms.repository;

import com.bj.wms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 客户Repository
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    /**
     * 根据客户编码查询
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * 根据客户名称查询
     */
    List<Customer> findByCustomerNameContainingIgnoreCase(String customerName);

    /**
     * 查询启用的客户
     */
    @Query("SELECT c FROM Customer c WHERE c.deleted = 0 AND c.isEnabled = true ORDER BY c.customerCode")
    List<Customer> findEnabledCustomers();

    /**
     * 分页查询启用的客户
     */
    @Query("SELECT c FROM Customer c WHERE c.deleted = 0 AND c.isEnabled = true ORDER BY c.customerCode")
    Page<Customer> findEnabledCustomers(Pageable pageable);

    /**
     * 检查客户编码是否存在
     */
    boolean existsByCustomerCode(String customerCode);

    /**
     * 检查客户编码是否存在（排除指定ID）
     */
    boolean existsByCustomerCodeAndIdNot(String customerCode, Long id);
}
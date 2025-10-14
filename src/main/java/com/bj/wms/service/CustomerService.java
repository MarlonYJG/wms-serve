package com.bj.wms.service;

import com.bj.wms.entity.Customer;
import com.bj.wms.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Page<Customer> page(Integer page, Integer size, String keyword, String customerName, String customerCode, Boolean isEnabled) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Customer> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                predicate.getExpressions().add(
                    cb.or(
                        cb.like(root.get("customerName"), like),
                        cb.like(root.get("customerCode"), like),
                        cb.like(root.get("contactPerson"), like)
                    )
                );
            }
            if (customerName != null && !customerName.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("customerName"), "%" + customerName.trim() + "%"));
            }
            if (customerCode != null && !customerCode.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("customerCode"), "%" + customerCode.trim() + "%"));
            }
            if (isEnabled != null) {
                predicate.getExpressions().add(cb.equal(root.get("isEnabled"), isEnabled));
            }
            return predicate;
        };

        return customerRepository.findAll(spec, pageable);
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer create(Customer toCreate) {
        if (customerRepository.existsByCustomerCode(toCreate.getCustomerCode())) {
            throw new IllegalArgumentException("客户编码已存在");
        }
        return customerRepository.save(toCreate);
    }

    @Transactional
    public Customer update(Long id, Customer updates) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        if (updates.getCustomerCode() != null && !updates.getCustomerCode().equals(existing.getCustomerCode())) {
            if (customerRepository.existsByCustomerCode(updates.getCustomerCode())) {
                throw new IllegalArgumentException("客户编码已存在");
            }
            existing.setCustomerCode(updates.getCustomerCode());
        }

        if (updates.getCustomerName() != null) existing.setCustomerName(updates.getCustomerName());
        if (updates.getCustomerType() != null) existing.setCustomerType(updates.getCustomerType());
        if (updates.getContactPerson() != null) existing.setContactPerson(updates.getContactPerson());
        if (updates.getContactPhone() != null) existing.setContactPhone(updates.getContactPhone());
        if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
        if (updates.getAddress() != null) existing.setAddress(updates.getAddress());
        if (updates.getCreditRating() != null) existing.setCreditRating(updates.getCreditRating());
        if (updates.getCreditLimit() != null) existing.setCreditLimit(updates.getCreditLimit());
        if (updates.getIsEnabled() != null) existing.setIsEnabled(updates.getIsEnabled());

        return customerRepository.save(existing);
    }

    @Transactional
    public Customer updateStatus(Long id, Boolean isEnabled) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        existing.setIsEnabled(isEnabled);
        return customerRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}



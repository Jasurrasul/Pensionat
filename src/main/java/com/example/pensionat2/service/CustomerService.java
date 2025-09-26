package com.example.pensionat2.service;



import com.example.pensionat2.dto.CustomerDTO;
import com.example.pensionat2.model.Customer;
import com.example.pensionat2.repository.BookingRepository;
import com.example.pensionat2.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDTO findById(Long id) {
        return customerRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public void save(CustomerDTO dto) {
        Customer customer = toEntity(dto);
        customerRepository.save(customer);
    }

    public boolean delete(Long id) {
        // Kontrollera om kunden har bokningar
        if (bookingRepository.existsByCustomerId(id)) {
            // Finns bokningar, kan inte ta bort
            return false;
        }
        // Inga bokningar, ta bort kunden
        customerRepository.deleteById(id);
        return true;
    }

    private CustomerDTO toDto(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }

    private Customer toEntity(CustomerDTO dto) {
        return Customer.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();
    }
}


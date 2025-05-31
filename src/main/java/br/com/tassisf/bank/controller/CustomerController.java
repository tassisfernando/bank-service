package br.com.tassisf.bank.controller;

import br.com.tassisf.bank.business.CustomerService;
import br.com.tassisf.bank.controller.in.CustomerRequest;
import br.com.tassisf.bank.controller.out.CustomerResponse;
import br.com.tassisf.bank.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> saveCustomer(@RequestBody CustomerRequest customer) {
        CustomerResponse customerResponse = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }
}

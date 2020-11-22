package com.example.mongodbtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private Customer customer;

    @Autowired
    private List<Customer> customerList;

    @Autowired
    private Optional<Customer> optionalCustomer;

    @GetMapping("/test")
    public String test(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s", name);
    }

    @GetMapping("/customersTest")
    public Customer customerTest(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Customer("FirstName", "LastName");
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> customers() {
        customerList = repository.findAll();
        if (customerList.isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.status(HttpStatus.OK).body(customerList);
    }

    @GetMapping("/customer")
    public ResponseEntity<Customer> getCustomer(@RequestParam(value = "firstName", defaultValue = "") String name) {
        customer = repository.findByFirstName(name);
        if (customer == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @PostMapping("/customer")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer postedCustomer) {
        if (postedCustomer == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (repository.existsById(postedCustomer.id))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return saveCustomer(postedCustomer);
    }

    @PatchMapping("/customer")
    public ResponseEntity<Customer> editCustomer(@RequestBody Customer postedCustomer) {
        if (postedCustomer == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (!repository.existsById(postedCustomer.id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return saveCustomer(postedCustomer);
    }

    private ResponseEntity<Customer> saveCustomer(Customer postedCustomer) {
        customer = repository.save(postedCustomer);
        System.out.println("Saved customer ".concat(customer.toString()));
        if (customer != null)
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/customer")
    public ResponseEntity<Customer> deleteCustomer(@RequestParam(value = "id", defaultValue = "") String deleteId) {
        optionalCustomer = repository.findById(deleteId);
        if (!optionalCustomer.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        repository.deleteById(deleteId);
        return ResponseEntity.status(HttpStatus.OK).body(optionalCustomer.get());
    }
}
